package com.digital.signer.dto.key;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@ToString
public class GenerateKeyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private byte[] key;
}
