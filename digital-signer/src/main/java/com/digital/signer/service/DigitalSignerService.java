package com.digital.signer.service;

import com.digital.signer.constant.Constant;
import com.digital.signer.constant.SQLConstant;
import com.digital.signer.dto.files.FileDTO;
import com.digital.signer.dto.files.ListFilesDTO;
import com.digital.signer.dto.key.GenerateKeyDTO;
import com.digital.signer.dto.transversal.ErrorDTO;
import com.digital.signer.dto.user.CreateUserRequestDTO;
import com.digital.signer.dto.user.SingInRequestDTO;
import com.digital.signer.dto.user.SingInResponseDTO;
import com.digital.signer.jdbc.CerrarRecursosJDBC;
import com.digital.signer.jdbc.UtilJDBC;
import com.digital.signer.jdbc.ValueSQL;
import com.digital.signer.util.JwtUtil;
import com.digital.signer.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

@Service
public class DigitalSignerService {

    private static final Logger logger = Logger.getLogger(DigitalSignerService.class.getName());

    private final DataSource dsDigitalSigner;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public DigitalSignerService(@Qualifier("digitalSignerDataSource")DataSource dsDigitalSigner,
                                JwtUtil jwtUtil) {
        this.dsDigitalSigner = dsDigitalSigner;
        this.jwtUtil=jwtUtil;
    }

    public CreateUserRequestDTO createUser(CreateUserRequestDTO user) {
        logger.log(INFO, Constant.START, Constant.CREATE_USER + user);
        try (Connection connection = this.dsDigitalSigner.getConnection()) {
            Long idUser = UtilJDBC.insertReturningID(connection,
                    SQLConstant.SAVE_USER,
                    ValueSQL.get(user.getUser(), Types.VARCHAR),
                    ValueSQL.get(user.getPassword(), Types.VARCHAR));
            user.setId(idUser);
        } catch (Exception e) {
            logger.log(SEVERE, Constant.END, Constant.CREATE_USER + e.getMessage());
        }
        logger.log(INFO, Constant.END, Constant.CREATE_USER + user);
        return user;
    }

    public GenerateKeyDTO generateKeyPairForUser(HttpServletRequest request) {
        logger.log(INFO, Constant.START, Constant.GENERATE_KEY_PAIR);

        GenerateKeyDTO response = new GenerateKeyDTO();

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userId = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            userId = jwtUtil.extractUserId(token);
        }

        if (userId == null || !jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid JWT Token");
        }

        try (Connection connection = this.dsDigitalSigner.getConnection()) {
            KeyPair keyPair = Util.generateKeyPair("RSA", 1024);
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            Long idKey = UtilJDBC.insertReturningID(connection,
                    SQLConstant.SAVE_KEY,
                    ValueSQL.get(Util.encodingPublicKeyBase64(publicKey.getEncoded()), Types.VARCHAR));

            UtilJDBC.insertUpdate(connection,
                    SQLConstant.SAVE_USER_KEY,
                    ValueSQL.get(userId, Types.INTEGER),
                    ValueSQL.get(idKey.intValue(), Types.INTEGER));

            response.setKey(privateKey.getEncoded());
        } catch (Exception e) {
            logger.log(SEVERE, Constant.END, Constant.GENERATE_KEY_PAIR + e.getMessage());
        }
        logger.log(INFO, Constant.END, Constant.GENERATE_KEY_PAIR + response);
        return response;
    }

    public SingInResponseDTO singIn(SingInRequestDTO request) throws Exception {
        logger.log(INFO, Constant.START, Constant.SING_IN + request);

        SingInResponseDTO response = new SingInResponseDTO();
        ErrorDTO error = new ErrorDTO();
        error.setErrorCode(Constant.ERROR_CODE_401);
        error.setErrorMessage(Constant.ERROR_MESSAGE_401);
        response.setError(error);

        PreparedStatement pst = null;
        ResultSet res = null;
        try (Connection connection = this.dsDigitalSigner.getConnection()) {

            pst = connection.prepareStatement(SQLConstant.SELECT_USER_INFO);
            pst.setString(1, request.getUser());
            pst.setString(2, request.getPassword());

            res = pst.executeQuery();

            if (res.next()) {
                response.setId(res.getInt(1));

                String jwtToken = jwtUtil.generateToken(response.getId());

                response.setJwt(jwtToken);

                error.setErrorCode(Constant.ERROR_CODE_200);
                error.setErrorMessage(Constant.ERROR_MESSAGE_200);
                response.setError(error);
            }
        } catch (Exception e) {
            logger.log(SEVERE, Constant.END, Constant.SING_IN + e.getMessage());
        } finally {
            CerrarRecursosJDBC.closeResultSet(res);
            CerrarRecursosJDBC.closePreparedStatement(pst);
        }
        logger.log(INFO, Constant.END, Constant.SING_IN + response);
        return response;
    }

    public ListFilesDTO listFiles(HttpServletRequest request) throws Exception {
        logger.log(INFO, Constant.START, Constant.LIST_FILES);

        ListFilesDTO response = new ListFilesDTO();
        List<FileDTO> files = new ArrayList<>();
        ErrorDTO error = new ErrorDTO();
        error.setErrorCode(Constant.ERROR_CODE_402);
        error.setErrorMessage(Constant.ERROR_MESSAGE_402);
        response.setError(error);

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userId = null;


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            userId = jwtUtil.extractUserId(token);
        }

        if (userId == null || !jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid JWT Token");
        }

        PreparedStatement pst = null;
        ResultSet res = null;

        try (Connection connection = this.dsDigitalSigner.getConnection()) {

            pst = connection.prepareStatement(SQLConstant.SELECT_USER_FILES);
            pst.setInt(1, Integer.parseInt(userId));
            res = pst.executeQuery();

            FileDTO file;
            while (res.next()) {
                file = new FileDTO();

                file.setId(res.getInt(1));
                file.setName(res.getString(2));
                file.setName(res.getString(3));
                file.setBytes(res.getString(4).getBytes());
                file.setIntegrityHash(res.getString(5));
                files.add(file);
            }

            if (!files.isEmpty()) {
                error.setErrorCode(Constant.ERROR_CODE_200);
                error.setErrorMessage(Constant.ERROR_MESSAGE_200);
                response.setError(error);
            }
            response.setListFiles(files);
        } catch (Exception e) {
            logger.log(SEVERE, Constant.END, Constant.LIST_FILES + e.getMessage());
        } finally {
            CerrarRecursosJDBC.closeResultSet(res);
            CerrarRecursosJDBC.closePreparedStatement(pst);
        }
        logger.log(INFO, Constant.END, Constant.LIST_FILES + response);
        return response;
    }

}
