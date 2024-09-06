package com.digital.signer.service;

import com.digital.signer.constant.Constant;
import com.digital.signer.constant.SQLConstant;
import com.digital.signer.dto.files.*;
import com.digital.signer.dto.key.GenerateKeyDTO;
import com.digital.signer.dto.transversal.ErrorDTO;
import com.digital.signer.dto.user.*;
import com.digital.signer.jdbc.CerrarRecursosJDBC;
import com.digital.signer.jdbc.UtilJDBC;
import com.digital.signer.jdbc.ValueSQL;
import com.digital.signer.util.Base64;
import com.digital.signer.util.JwtUtil;
import com.digital.signer.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
                    ValueSQL.get(user.getPassword(), Types.VARCHAR),
                    ValueSQL.get(user.getEmail(), Types.VARCHAR));
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
                    ValueSQL.get(Integer.parseInt(userId), Types.INTEGER),
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

    public SingInResponseDTO googleSingIn(GoogleSingInRequestDTO request) throws Exception {
        logger.log(INFO, Constant.START, Constant.GOOGLE_SING_IN + request);

        SingInResponseDTO response = new SingInResponseDTO();
        ErrorDTO error = new ErrorDTO();
        error.setErrorCode(Constant.ERROR_CODE_401);
        error.setErrorMessage(Constant.ERROR_MESSAGE_401);
        response.setError(error);

        PreparedStatement pst = null;
        ResultSet res = null;
        try (Connection connection = this.dsDigitalSigner.getConnection()) {

            pst = connection.prepareStatement(SQLConstant.SELECT_USER_GOOGLE_INFO);
            pst.setString(1, request.getEmail());

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
            logger.log(SEVERE, Constant.END, Constant.GOOGLE_SING_IN + e.getMessage());
        } finally {
            CerrarRecursosJDBC.closeResultSet(res);
            CerrarRecursosJDBC.closePreparedStatement(pst);
        }
        logger.log(INFO, Constant.END, Constant.GOOGLE_SING_IN + response);
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
        logger.log(INFO, Constant.END, Constant.LIST_FILES + response.getError());
        return response;
    }

    public ListFilesDTO listMySharesFile(HttpServletRequest request) throws Exception {
        logger.log(INFO, Constant.START, Constant.LIST_SHARE_FILES);

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

            pst = connection.prepareStatement(SQLConstant.SELECT_SHARE_FILES);
            pst.setInt(1, Integer.parseInt(userId));
            res = pst.executeQuery();

            FileDTO file;
            while (res.next()) {
                file = new FileDTO();

                file.setId(res.getInt(1));
                file.setName(res.getString(2));
                file.setBytes(res.getString(3).getBytes());
                file.setIntegrityHash(res.getString(4));
                file.setEmailUserSource(getUserEmail(res.getInt(5)));
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
            logger.log(SEVERE, Constant.END, Constant.LIST_SHARE_FILES + e.getMessage());
        } finally {
            CerrarRecursosJDBC.closeResultSet(res);
            CerrarRecursosJDBC.closePreparedStatement(pst);
        }
        logger.log(INFO, Constant.END, Constant.LIST_SHARE_FILES + response.getError());
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
                    ValueSQL.get(signedFileDTO.getIdFile(), Types.INTEGER))) {

                UtilJDBC.insertUpdate(connection,SQLConstant.USER_DIGITAL_SIGNED,
                        ValueSQL.get(getSignedHash(signedFileDTO.getIdFile(), signedFileDTO.getPrivateKeyFile().getBytes()), Types.VARCHAR),
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

    public VerifyFileResponseDTO verifyFile(HttpServletRequest request, VerifyFileRequestDTO verifyFileRequestDTO) throws Exception {

        logger.log(INFO, Constant.START, Constant.VERIFY_FILE);

        VerifyFileResponseDTO response = new VerifyFileResponseDTO();
        ErrorDTO error = new ErrorDTO();
        error.setErrorCode(Constant.ERROR_CODE_405);
        error.setErrorMessage(Constant.ERROR_MESSAGE_405);
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

            pst = connection.prepareStatement(SQLConstant.SELECT_CONFIRM_FILE);
            pst.setInt(1, Integer.parseInt(userId));
            pst.setInt(2, verifyFileRequestDTO.getIdFile());

            res = pst.executeQuery();

            if (res.next()) {
                String integrityHash = res.getString(1);
                String digitalSigned = res.getString(2);
                byte[] fileBytes = res.getBytes(3);
                String publicKey = res.getString(4);

                String integrityConfirmHash = Util.getHash(fileBytes, "SHA-256");

                if (!integrityConfirmHash.equals(integrityHash)) {
                    error.setErrorCode(Constant.ERROR_CODE_406);
                    error.setErrorMessage(Constant.ERROR_MESSAGE_406);
                    response.setError(error);
                    return response;
                }

                if (Util.isNull(digitalSigned)) {
                    error.setErrorCode(Constant.ERROR_CODE_407);
                    error.setErrorMessage(Constant.ERROR_MESSAGE_407);
                    response.setError(error);
                    return response;
                }

                try {
                    String decodeSignedHash = decodeSingFile(digitalSigned, publicKey);

                    if (!Util.isNull(decodeSignedHash)
                            && decodeSignedHash.equals(integrityHash)) {
                        error.setErrorCode(Constant.ERROR_CODE_200);
                        error.setErrorMessage(Constant.ERROR_MESSAGE_200);
                        response.setError(error);
                        return response;
                    }
                } catch (Exception e) {
                    error.setErrorCode(Constant.ERROR_CODE_408);
                    error.setErrorMessage(Constant.ERROR_MESSAGE_408);
                    response.setError(error);
                    return response;
                }

            }

        } catch (Exception e) {
            logger.log(SEVERE, Constant.END, Constant.VERIFY_FILE + e.getMessage());
        } finally {
            CerrarRecursosJDBC.closeResultSet(res);
            CerrarRecursosJDBC.closePreparedStatement(pst);
            logger.log(INFO, Constant.END, Constant.VERIFY_FILE + response);
        }
        return response;
    }

    public ShareFileResponseDTO shareFile(HttpServletRequest request, ShareFileRequestDTO shareFileRequestDTO) {

        logger.log(INFO, Constant.START, Constant.SHARE_FILE);

        ShareFileResponseDTO response = new ShareFileResponseDTO();
        ErrorDTO error = new ErrorDTO();
        error.setErrorCode(Constant.ERROR_CODE_409);
        error.setErrorMessage(Constant.ERROR_MESSAGE_409);
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
            UtilJDBC.insertUpdate(connection,SQLConstant.SAVE_SHARE_FILE,
                    ValueSQL.get(userId, Types.INTEGER),
                    ValueSQL.get(shareFileRequestDTO.getIdUserTarget(), Types.INTEGER),
                    ValueSQL.get(shareFileRequestDTO.getIdFile(), Types.INTEGER));
        } catch (Exception e) {
            logger.log(SEVERE, Constant.END, Constant.SHARE_FILE + e.getMessage());
        }
        logger.log(INFO, Constant.END, Constant.SHARE_FILE + response);
        return response;
    }

    public ListShareUsersResponseDTO listShareUsers(HttpServletRequest request) throws Exception {

        logger.log(INFO, Constant.START, Constant.LIST_SHARE_USERS);

        ListShareUsersResponseDTO response = new ListShareUsersResponseDTO();
        ErrorDTO error = new ErrorDTO();
        error.setErrorCode(Constant.ERROR_CODE_410);
        error.setErrorMessage(Constant.ERROR_MESSAGE_410);
        response.setError(error);
        List<UserDTO> users = new ArrayList<>();

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

            pst = connection.prepareStatement(SQLConstant.SELECT_SHARE_USERS);
            res = pst.executeQuery();

            UserDTO user;
            while (res.next()) {
                int idUser = res.getInt(1);

                if (Integer.parseInt(userId) != idUser) {
                    user = new UserDTO();
                    user.setId(idUser);
                    user.setEmail(res.getString(2));
                    users.add(user);
                }
            }
            error.setErrorCode(Constant.ERROR_CODE_200);
            error.setErrorMessage(Constant.ERROR_MESSAGE_200);
            response.setError(error);
            response.setUsers(users);
        } catch (Exception e) {
            logger.log(SEVERE, Constant.END, Constant.LIST_SHARE_USERS + e.getMessage());
        } finally {
            CerrarRecursosJDBC.closeResultSet(res);
            CerrarRecursosJDBC.closePreparedStatement(pst);
            logger.log(INFO, Constant.END, Constant.LIST_SHARE_USERS + response);
        }
        return response;
    }

    private String getSignedHash(Integer idFile, byte[] bytes) throws Exception {

        PreparedStatement pst = null;
        ResultSet res = null;

        String privateKeyBytes = Util.decodeKeyDto(bytes);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.decode(privateKeyBytes));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);

        byte[] byteEncrypted = null;
        try (Connection connection = this.dsDigitalSigner.getConnection()) {

            pst = connection.prepareStatement(SQLConstant.SELECT_FILE);
            pst.setInt(1, idFile);
            res = pst.executeQuery();

            if(res.next()){
                String hash = res.getString(1);

                byte[] hashBytes = Base64.decode(hash);
                byteEncrypted = Util.encrypBlockByte(hashBytes, privateKey);
            }

        } catch (Exception e) {
            logger.log(SEVERE, Constant.END, Constant.LIST_FILES + e.getMessage());
        } finally {
            CerrarRecursosJDBC.closeResultSet(res);
            CerrarRecursosJDBC.closePreparedStatement(pst);
        }
        return Base64.encode(byteEncrypted);
    }

    private String decodeSingFile(String singHash, String publicKeyStr) throws Exception {

        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(publicKeyStr));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);

        byte[] hashBytes = Base64.decode(singHash);

        byte[] byteEncrypted = Util.decrypBlockByte(hashBytes, publicKey);
        return Base64.encode(byteEncrypted);
    }

    private String getUserEmail(Integer userId) throws Exception {

        PreparedStatement pst = null;
        ResultSet res = null;

        try (Connection connection = this.dsDigitalSigner.getConnection()) {

            pst = connection.prepareStatement(SQLConstant.SELECT_USER_EMAIL);
            pst.setInt(1, userId);
            res = pst.executeQuery();

            if(res.next()){
                return res.getString(1);
            }

        } catch (Exception e) {
            logger.log(SEVERE, Constant.END, Constant.LIST_SHARE_FILES + e.getMessage());
        } finally {
            CerrarRecursosJDBC.closeResultSet(res);
            CerrarRecursosJDBC.closePreparedStatement(pst);
        }
        return null;
    }
}
