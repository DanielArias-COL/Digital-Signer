package com.digital.signer.dto.files;


import com.digital.signer.dto.transversal.ErrorDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@ToString
public class SignedFileResponseDTO implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    private ErrorDTO error;
}
