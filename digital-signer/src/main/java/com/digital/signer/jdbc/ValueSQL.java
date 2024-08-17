package com.digital.signer.jdbc;

import lombok.Data;

@Data
public class ValueSQL {

	private Object valor;

	private Integer tipoDato;

	private ValueSQL(Object valor, Integer tipoDato) {
		this.valor = valor;
		this.tipoDato = tipoDato;
	}

	public static ValueSQL get(Object valor, Integer tipoDato) {
		return new ValueSQL(valor, tipoDato);
	}

	public String toString() {
		if (this.valor != null) {
			return this.valor.toString();
		}
		return null;
	}
}
