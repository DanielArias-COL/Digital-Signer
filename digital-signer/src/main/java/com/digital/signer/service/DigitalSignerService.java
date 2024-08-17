package com.digital.signer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.logging.Logger;

@Service
public class DigitalSignerService {

    private static final Logger logger = Logger.getLogger(DigitalSignerService.class.getName());

    private final DataSource dsDigitalSigner;

    @Autowired
    public DigitalSignerService(@Qualifier("workerDataSource")DataSource dsDigitalSigner) {
        this.dsDigitalSigner = dsDigitalSigner;
    }
}
