package com.digital.signer.dto.files;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serial;
import java.io.Serializable;


@Setter
@Getter
@ToString
public class ShareUserSigned implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer idUser;

    private String email;
}





