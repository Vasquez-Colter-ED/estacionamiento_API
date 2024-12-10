package pe.edu.utp.pf_api.repository;

import pe.edu.utp.pf_api.model.Usuario;
import pe.edu.utp.pf_api.util.DataAccessMariaDB;

import javax.naming.NamingException;
import java.sql.*;

public class UsuarioRepository {

    public Usuario findUserByEmail(String email, String pwd) throws SQLException {
        String query = "CALL pr_checkUser(?, ?)";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, pwd);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setLogin(rs.getString("login"));
                usuario.setFullname(rs.getString("fullname"));
                usuario.setEmail(rs.getString("email"));
                return usuario;
            }
            return null;
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUser(Usuario usuario) throws SQLException, NamingException {
        String query = "CALL AddUsuario(?, ?, ?, ?)";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getFullname());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getPwd());
            stmt.executeUpdate();
        }
    }
}
