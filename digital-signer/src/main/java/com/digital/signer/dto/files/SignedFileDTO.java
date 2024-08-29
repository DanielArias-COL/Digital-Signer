package com.digital.signer.dto.files;

import com.digital.signer.dto.transversal.ErrorDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@ToString
public class SignedFileDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer idFile;

    private MultipartFile privateKey;

    private ErrorDTO error;

}
