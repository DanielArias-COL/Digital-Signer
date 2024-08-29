package com.digital.signer.jdbc;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;

public class UtilJDBC {

    private static UtilJDBC instance;

    private UtilJDBC() {
    }

    public static UtilJDBC getInstance() {
        if (instance == null) {
            instance = new UtilJDBC();
        }
        return instance;
    }

    public static int insertUpdate(Connection con, String dml, ValueSQL... valores) throws Exception {
        PreparedStatement pst = null;
        try {
            // se establece el PreparedStatement
            pst = con.prepareStatement(dml);

            // se recorre cada valor para configurarlo en el PreparedStatement
            int posicion = 1;
            for (ValueSQL valueSQL : valores) {

                // se valida si se debe configurar NULL en el PreparedStatement
                if (valueSQL.getValor() != null) {
                    setValorNotNull(pst, valueSQL, posicion);
                } else {
                    pst.setNull(posicion, valueSQL.getTipoDato());
                }
                posicion++;
            }

            // se ejecuta la inserción
            return pst.executeUpdate();
        } finally {
            CerrarRecursosJDBC.closePreparedStatement(pst);
        }
    }

    private static void setValorNotNull(PreparedStatement pst, ValueSQL valor, int posicion) throws Exception {
        switch (valor.getTipoDato()) {
            case Types.VARCHAR -> pst.setString(posicion, (String) valor.getValor());
            case Types.INTEGER -> pst.setInt(posicion, (Integer) valor.getValor());
            case Types.BIGINT -> pst.setLong(posicion, (Long) valor.getValor());
            case Types.DATE -> pst.setDate(posicion, new java.sql.Date(((Date) valor.getValor()).getTime()));
            case Types.TIMESTAMP ->
                    pst.setTimestamp(posicion, new Timestamp(((Date) valor.getValor()).getTime()));
            case Types.BLOB -> pst.setBytes(posicion, (byte[]) valor.getValor());
            case Types.DECIMAL -> pst.setBigDecimal(posicion, (new BigDecimal((String) valor.getValor())));
            case Types.BOOLEAN -> pst.setBoolean(posicion, (Boolean) valor.getValor());
            case Types.FLOAT -> pst.setBigDecimal(posicion, (BigDecimal) valor.getValor());

        }
    }

    public static Long insertReturningID(Connection con, String dml, ValueSQL... valores) throws Exception {
        PreparedStatement pst = null;
        ResultSet res = null;
        try {
            // se establece el PreparedStatement
            pst = con.prepareStatement(dml);

            // se recorre cada valor para configurarlo en el PreparedStatement
            int posicion = 1;
            for (ValueSQL valueSQL : valores) {

                // se valida si se debe configurar NULL en el PreparedStatement
                if (valueSQL.getValor() != null) {
                    setValorNotNull(pst, valueSQL, posicion);
                } else {
                    pst.setNull(posicion, valueSQL.getTipoDato());
                }
                posicion++;
            }

            // se ejecuta la inserción
            res = pst.executeQuery();

            // se obtiene el identificador unico de la insercion
            if (res.next()) {
                return res.getLong(1);
            }
            return new Long(0);
        } finally {
            CerrarRecursosJDBC.closePreparedStatement(pst);
            CerrarRecursosJDBC.closeResultSet(res);
        }
    }

    public static boolean exists(Connection con, String sql, ValueSQL... where) throws Exception {
        PreparedStatement pst = null;
        ResultSet res = null;
        try {
            pst = con.prepareStatement(sql);

            if (where != null && where.length > 0) {
                int posicion = 1;
                for (ValueSQL valor : where) {
                    setValorNotNull(pst, valor, posicion);
                    posicion++;
                }
            }

            res = pst.executeQuery();
            if (res.next()) {
                return res.getBoolean(1);
            }
            return false;
        } finally {
            CerrarRecursosJDBC.closeResultSet(res);
            CerrarRecursosJDBC.closePreparedStatement(pst);
        }
    }
}
