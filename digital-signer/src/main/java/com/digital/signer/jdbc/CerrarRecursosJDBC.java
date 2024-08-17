package com.digital.signer.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class CerrarRecursosJDBC {

	public static void closePreparedStatement(PreparedStatement pst) throws Exception {
		if (pst != null) {
			pst.close();
		}
	}

	public static void closeResultSet(ResultSet res) throws Exception {
		if (res != null) {
			res.close();
		}
	}

	public static void closeStatement(Statement stm) throws Exception {
		if (stm != null) {
			stm.close();
		}
	}

	/**
	 * Metodo que permite cerrar el recurso Connection SQL
	 */
	public static void closeConnection(Connection connection) throws Exception {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
	}
}
