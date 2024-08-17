package com.digital.signer.common;

import java.io.Serial;
import java.io.Serializable;

public class MensajeRespuestaHTTP implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String mensaje;

    public MensajeRespuestaHTTP(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}