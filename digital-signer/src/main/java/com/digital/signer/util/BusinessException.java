package com.digital.signer.util;

import java.io.Serial;

/**
 * Clase que identifica el tipo de exception de negocio
 */
public class BusinessException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructor de la Exception
     *
     * @param msj, es el mensaje de la excepción ocurrida
     */
    public BusinessException(String msj) {
        super(msj);
    }
}
