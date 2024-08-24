package com.digital.signer.dto.user;

import com.digital.signer.dto.transversal.ErrorDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@ToString
public class SingInResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private ErrorDTO error;

    private String jwt;
}
