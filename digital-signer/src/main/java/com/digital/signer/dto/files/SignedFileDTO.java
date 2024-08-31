package com.digital.signer.dto.files;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@ToString
public class SignedFileDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer idFile;

    private MultipartFile privateKeyFile;
}
