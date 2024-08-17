package com.digital.signer.service;

import com.digital.signer.constant.Constant;
import com.digital.signer.constant.SQLConstant;
import com.digital.signer.dto.key.GenerateKeyDTO;
import com.digital.signer.dto.user.CreateUserRequestDTO;
import com.digital.signer.jdbc.UtilJDBC;
import com.digital.signer.jdbc.ValueSQL;
import com.digital.signer.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.Types;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

@Service
public class DigitalSignerService {

    private static final Logger logger = Logger.getLogger(DigitalSignerService.class.getName());

    private final DataSource dsDigitalSigner;

    @Autowired
    public DigitalSignerService(@Qualifier("digitalSignerDataSource")DataSource dsDigitalSigner) {
        this.dsDigitalSigner = dsDigitalSigner;
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

    public GenerateKeyDTO generateKeyPairForUser(Integer idUser) throws Exception {
        logger.log(INFO, Constant.START, Constant.GENERATE_KEY_PAIR + idUser);

        GenerateKeyDTO response = new GenerateKeyDTO();

        try (Connection connection = this.dsDigitalSigner.getConnection()) {
            KeyPair keyPair = Util.generateKeyPair("RSA",1024);
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            Long idKey = UtilJDBC.insertReturningID(connection,
                    SQLConstant.SAVE_KEY,
                    ValueSQL.get(Util.encodingPublicKeyBase64(publicKey.getEncoded()), Types.VARCHAR));

            UtilJDBC.insertUpdate(connection,
                    SQLConstant.SAVE_USER_KEY,
                    ValueSQL.get(idUser, Types.INTEGER),
                    ValueSQL.get(idKey.intValue(), Types.INTEGER));

            response.setKey(privateKey.getEncoded());
        } catch (Exception e) {
            throw new Exception(e);
        }
        logger.log(INFO, Constant.END, Constant.GENERATE_KEY_PAIR + response);
        return response;
    }
}
