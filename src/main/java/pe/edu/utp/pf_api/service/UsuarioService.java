package pe.edu.utp.pf_api.service;

import pe.edu.utp.pf_api.model.Usuario;
import pe.edu.utp.pf_api.repository.UsuarioRepository;
import pe.edu.utp.pf_api.exceptions.UserAlreadyExists;
import pe.edu.utp.pf_api.util.DataAccessMariaDB;
import pe.edu.utp.pf_api.util.LogFile;
import javax.naming.NamingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que maneja la lógica de negocio relacionada con los usuarios.
 * Proporciona métodos para crear, autenticar, listar y eliminar usuarios.
 */
public class UsuarioService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    /**
     * Crea un nuevo usuario en el sistema.
     * La contraseña se encripta usando MD5 antes de almacenarla.
     *
     * @param usuario Objeto Usuario con los datos del nuevo usuario
     * @throws IOException Si ocurre un error al escribir en los logs
     * @throws UserAlreadyExists Si ya existe un usuario con el mismo email
     * @throws RuntimeException Si ocurre un error en la base de datos o en la configuración
     *
     * Proceso:
     * 1. Encripta la contraseña
     * 2. Verifica si el usuario ya existe
     * 3. Si no existe, guarda el nuevo usuario
     */
    public void createUser(Usuario usuario) throws IOException {
        try {
            LogFile.info("Verificando si existe usuario con email: " + usuario.getEmail());

            // Encriptar la contraseña antes de buscar el usuario
            String encryptedPassword = AuthService.md5(usuario.getPwd());
            usuario.setPwd(encryptedPassword); // Actualizar la contraseña encriptada en el objeto

            Usuario existingUser = usuarioRepository.findUserByEmail(usuario.getEmail(), encryptedPassword);

            if (existingUser != null) {
                LogFile.info("Usuario ya existe con email: " + usuario.getEmail());
                throw new UserAlreadyExists("User with this email already exists.");
            }

            LogFile.info("Procediendo a guardar nuevo usuario: " + usuario.getEmail());
            // No necesitamos volver a encriptar la contraseña aquí porque ya está encriptada
            usuarioRepository.saveUser(usuario);
            LogFile.info("Usuario guardado exitosamente: " + usuario.getEmail());

        } catch (SQLException e) {
            LogFile.error("Error SQL: " + e.getMessage());
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        } catch (NamingException e) {
            LogFile.error("Error de Naming: " + e.getMessage());
            throw new RuntimeException("Error with database configuration: " + e.getMessage(), e);
        }
    }

    /**
     * Valida las credenciales de un usuario.
     *
     * @param email Email del usuario
     * @param pwd Contraseña sin encriptar
     * @return true si las credenciales son válidas, false en caso contrario
     * @throws IOException Si ocurre un error al escribir en los logs
     *
     * Proceso:
     * 1. Encripta la contraseña proporcionada
     * 2. Busca el usuario por email y contraseña encriptada
     * 3. Retorna true si encuentra coincidencia
     */
    public boolean isValidUser(String email, String pwd) throws IOException {
        try {
            LogFile.info("Verificando credenciales para email: " + email);
            String encryptedPassword = AuthService.md5(pwd);
            LogFile.info("Password encriptado generado");

            Usuario usuario = usuarioRepository.findUserByEmail(email, encryptedPassword);

            if (usuario == null) {
                LogFile.info("No se encontró usuario con el email: " + email);
                return false;
            }

            LogFile.info("Usuario encontrado y validado correctamente");
            return true;
        } catch (SQLException e) {
            LogFile.error("Error en base de datos al validar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene la lista de todos los usuarios registrados.
     *
     * @return Lista de objetos Usuario
     * @throws SQLException Si ocurre un error en la base de datos
     *
     * Proceso:
     * 1. Ejecuta consulta SELECT en la tabla Usuario
     * 2. Mapea los resultados a objetos Usuario
     * 3. Retorna la lista de usuarios
     */
    public List<Usuario> findAllUsers() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT * FROM Usuario";  // Cambiado de 'usuarios' a 'Usuario'
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setLogin(rs.getString("login"));
                usuario.setFullname(rs.getString("fullname"));
                usuario.setEmail(rs.getString("email"));
                usuarios.add(usuario);
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return usuarios;
    }

    /**
     * Elimina un usuario por su login.
     *
     * @param login Login del usuario a eliminar
     * @return true si el usuario fue eliminado, false si no se encontró
     * @throws SQLException Si ocurre un error en la base de datos
     *
     * Proceso:
     * 1. Ejecuta DELETE en la tabla Usuario con el login especificado
     * 2. Retorna true si se eliminó algún registro
     */
    public boolean deleteUser(String login) throws SQLException {
        LogFile.info("Intentando eliminar usuario con login: " + login);

        String query = "DELETE FROM Usuario WHERE login = ?";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, login);
            LogFile.info("Ejecutando query de eliminación");

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                LogFile.info("Usuario eliminado correctamente de la base de datos");
                return true;
            } else {
                LogFile.info("No se encontró usuario con login: " + login);
                return false;
            }
        } catch (SQLException e) {
            LogFile.error("Error SQL al eliminar usuario: " + e.getMessage());
            throw e;
        } catch (NamingException e) {
            LogFile.error("Error de conexión al eliminar usuario: " + e.getMessage());
            throw new SQLException("Error de conexión", e);
        }
    }

}
