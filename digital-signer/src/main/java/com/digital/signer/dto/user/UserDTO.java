package com.digital.signer.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@ToString
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String email;
}
