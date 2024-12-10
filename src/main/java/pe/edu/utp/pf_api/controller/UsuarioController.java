package pe.edu.utp.pf_api.controller;

import pe.edu.utp.pf_api.model.Usuario;
import pe.edu.utp.pf_api.service.UsuarioService;
import pe.edu.utp.pf_api.exceptions.UserAlreadyExists;
import pe.edu.utp.pf_api.util.LogFile;
import pe.edu.utp.pf_api.model.ErrorResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de usuarios.
 * Proporciona endpoints para operaciones CRUD de usuarios y autenticación.
 *
 * Base URL: /usuarios
 */
@Path("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService = new UsuarioService();

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param usuario Objeto Usuario con los datos del nuevo usuario
     * @return Response con estado:
     *         201 (CREATED) si el usuario se creó exitosamente
     *         400 (BAD_REQUEST) si faltan datos requeridos
     *         409 (CONFLICT) si el usuario ya existe
     *         500 (INTERNAL_SERVER_ERROR) si ocurre un error en el servidor
     *
     * Ejemplo de uso:
     * POST /usuarios/register
     * Content-Type: application/json
     * {
     *     "login": "usuario1",
     *     "fullname": "Usuario Ejemplo",
     *     "email": "usuario@ejemplo.com",
     *     "pwd": "contraseña123"
     * }
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(Usuario usuario) {
        try {
            LogFile.info("Iniciando registro de usuario: " + usuario.getEmail());

            // Validar datos requeridos
            if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
                LogFile.error("Error: Login es requerido");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "Login es requerido"))
                        .build();
            }

            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                LogFile.error("Error: Email es requerido");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "Email es requerido"))
                        .build();
            }

            usuarioService.createUser(usuario);
            LogFile.info("Usuario registrado exitosamente: " + usuario.getEmail());
            return Response.status(Response.Status.CREATED).entity(usuario).build();

        } catch (UserAlreadyExists e) {
            LogFile.error("Error: Usuario ya existe - " + e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("Usuario ya existe", e.getMessage()))
                    .build();
        } catch (Exception e) {
            LogFile.error("Error grave en registerUser: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al procesar la solicitud"))
                    .build();
        }
    }

    /**
     * Autentica un usuario en el sistema.
     *
     * @param usuario Objeto Usuario con email y contraseña
     * @return Response con estado:
     *         200 (OK) si la autenticación es exitosa
     *         400 (BAD_REQUEST) si faltan credenciales
     *         401 (UNAUTHORIZED) si las credenciales son inválidas
     *         500 (INTERNAL_SERVER_ERROR) si ocurre un error en el servidor
     *
     * Ejemplo de uso:
     * POST /usuarios/login
     * Content-Type: application/json
     * {
     *     "email": "usuario@ejemplo.com",
     *     "pwd": "contraseña123"
     * }
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(Usuario usuario) {
        try {
            LogFile.info("Iniciando intento de login para usuario: " + usuario.getEmail());

            // Validar datos requeridos
            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                LogFile.error("Error: Email es requerido para login");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "Email es requerido"))
                        .build();
            }

            if (usuario.getPwd() == null || usuario.getPwd().trim().isEmpty()) {
                LogFile.error("Error: Password es requerido para login");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "Password es requerido"))
                        .build();
            }

            boolean isValid = usuarioService.isValidUser(usuario.getEmail(), usuario.getPwd());

            if (!isValid) {
                LogFile.error("Error: Credenciales inválidas para usuario: " + usuario.getEmail());
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Error de autenticación", "Credenciales inválidas"))
                        .build();
            }

            LogFile.info("Login exitoso para usuario: " + usuario.getEmail());

            // Crear objeto de respuesta
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("status", "success");
            successResponse.put("message", "Login exitoso");

            return Response.ok(successResponse).build();

        } catch (Exception e) {
            LogFile.error("Error grave en loginUser: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al procesar la solicitud"))
                    .build();
        }
    }

    /**
     * Obtiene la lista de todos los usuarios registrados.
     *
     * @return Response con estado:
     *         200 (OK) con la lista de usuarios
     *         500 (INTERNAL_SERVER_ERROR) si ocurre un error en el servidor
     *
     * Ejemplo de uso:
     * GET /usuarios
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        try {
            LogFile.info("Iniciando obtención de usuarios");
            List<Usuario> usuarios = usuarioService.findAllUsers();
            LogFile.info("Usuarios obtenidos exitosamente: " + usuarios.size());
            return Response.ok(usuarios).build();
        } catch (Exception e) {
            LogFile.error("Error grave en getAllUsers: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al obtener usuarios"))
                    .build();
        }
    }

    /**
     * Elimina un usuario del sistema por su login.
     *
     * @param login Identificador único del usuario a eliminar
     * @return Response con estado:
     *         200 (OK) si el usuario fue eliminado exitosamente
     *         400 (BAD_REQUEST) si el login es nulo o vacío
     *         404 (NOT_FOUND) si el usuario no existe
     *         500 (INTERNAL_SERVER_ERROR) si ocurre un error en el servidor
     *
     * Ejemplo de uso:
     * DELETE /usuarios/{login}
     */
    @DELETE
    @Path("/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("login") String login) {
        try {
            LogFile.info("Iniciando eliminación de usuario con login: " + login);

            // Validar que el login no esté vacío
            if (login == null || login.trim().isEmpty()) {
                LogFile.error("Error: Login es requerido para eliminar usuario");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "Login es requerido"))
                        .build();
            }

            boolean deleted = usuarioService.deleteUser(login);

            if (deleted) {
                LogFile.info("Usuario eliminado exitosamente: " + login);
                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("status", "success");
                successResponse.put("message", "Usuario eliminado exitosamente");
                return Response.ok(successResponse).build();
            } else {
                LogFile.error("Error: Usuario no encontrado: " + login);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Error", "Usuario no encontrado"))
                        .build();
            }
        } catch (Exception e) {
            LogFile.error("Error grave en deleteUser: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al eliminar usuario"))
                    .build();
        }
    }

}

// Clase para respuestas exitosas
class SuccessResponse {
    private String status;
    private String message;

    public SuccessResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // getters y setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

