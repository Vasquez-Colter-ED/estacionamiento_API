package pe.edu.utp.pf_api.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class DataAccessMariaDB {

    private static final String DATASOURCE_NAME = "java:/MariaDB_DWI";  // Nombre del DataSource configurado

    public static Connection getConnection() throws SQLException, NamingException {
        InitialContext ctx = null;
        try {
            ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(DATASOURCE_NAME);
            if (ds == null) {
                throw new NamingException("DataSource no encontrado: " + DATASOURCE_NAME);
            }
            Connection conn = ds.getConnection();
            if (conn == null) {
                throw new SQLException("No se pudo obtener la conexión del DataSource");
            }
            return conn;
        } catch (NamingException e) {
            System.err.println("Error al buscar el DataSource: " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("Error al obtener la conexión: " + e.getMessage());
            throw e;
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    System.err.println("Error al cerrar el contexto: " + e.getMessage());
                }
            }
        }
    }
}
