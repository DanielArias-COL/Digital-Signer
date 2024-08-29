package com.digital.signer.service;

import com.digital.signer.constant.Constant;
import com.digital.signer.constant.SQLConstant;
import com.digital.signer.dto.files.*;
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
import org.springframework.web.multipart.MultipartFile;

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
                file.setBytes(res.getString(3).getBytes());
                file.setIntegrityHash(res.getString(4));
                file.setDigitalSigned(res.getString(6));
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
        //logger.log(INFO, Constant.END, Constant.LIST_FILES + response);
        return response;
    }

    public SaveFilesResponseDTO saveFiles(HttpServletRequest request, MultipartFile[] files) {
        logger.log(INFO, Constant.START, Constant.SAVE_FILES);

        SaveFilesResponseDTO response = new SaveFilesResponseDTO();
        ErrorDTO error = new ErrorDTO();
        error.setErrorCode(Constant.ERROR_CODE_403);
        error.setErrorMessage(Constant.ERROR_MESSAGE_403);
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

        AddFileDTO fileInsert;
        for (MultipartFile file : files) {
            fileInsert = new AddFileDTO();
            fileInsert.setName(file.getOriginalFilename());
            try {
                fileInsert.setBytes(file.getBytes());
                fileInsert.setIntegrityHash(Util.getHash(file.getBytes(), "SHA-256"));
                addFiles(fileInsert, Integer.valueOf(userId));
                error.setErrorCode(Constant.ERROR_CODE_200);
                error.setErrorMessage(Constant.ERROR_MESSAGE_200);
                response.setError(error);
            } catch (Exception e) {
                logger.log(SEVERE, Constant.END, Constant.SAVE_FILES + e.getMessage());
            }
        }

        logger.log(INFO, Constant.END, Constant.SAVE_FILES + response);
        return response;
    }

    private void addFiles(AddFileDTO file, Integer userId) throws Exception {
        logger.log(INFO, Constant.START, Constant.ADD_FILES);
        try (Connection connection = this.dsDigitalSigner.getConnection()) {
            UtilJDBC.insertUpdate(connection,
                    SQLConstant.ADD_FILE,
                    ValueSQL.get(file.getName(), Types.VARCHAR),
                    ValueSQL.get(file.getBytes(), Types.BLOB),
                    ValueSQL.get(file.getIntegrityHash(), Types.VARCHAR),
                    ValueSQL.get(userId, Types.INTEGER));
        }
        logger.log(INFO, Constant.END, Constant.ADD_FILES );
    }

    public SignedFileResponseDTO signedFile(HttpServletRequest request, SignedFileDTO signedFileDTO) {

        logger.log(INFO, Constant.START, Constant.SIGNED_FILE);

        SignedFileResponseDTO response = new SignedFileResponseDTO();
        ErrorDTO error = new ErrorDTO();
        error.setErrorCode(Constant.ERROR_CODE_403);
        error.setErrorMessage(Constant.ERROR_MESSAGE_403);
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

        try (Connection connection = this.dsDigitalSigner.getConnection()) {

            if(UtilJDBC.exists(connection, SQLConstant.EXIST_FILE,
                    ValueSQL.get(signedFileDTO.getIdFile(), Types.INTEGER))){

                UtilJDBC.insertUpdate(connection,SQLConstant.USER_DIGITAL_SIGNED,
                        ValueSQL.get("firmado", Types.VARCHAR),
                        ValueSQL.get(signedFileDTO.getIdFile(), Types.INTEGER));

                error.setErrorCode(Constant.ERROR_CODE_200);
                error.setErrorMessage(Constant.ERROR_MESSAGE_200);
                response.setError(error);
            }


        } catch (Exception e) {
            logger.log(SEVERE, Constant.END, Constant.SING_IN + e.getMessage());
        }
        logger.log(INFO, Constant.END, Constant.SIGNED_FILE + response);
        return response;
    }

}
