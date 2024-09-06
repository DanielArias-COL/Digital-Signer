package com.digital.signer.dto.files;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@ToString
public class FileDTO implements  Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private byte [] bytes;
    private String integrityHash;
    private String digitalSigned;
    private String emailUserSource;
}
