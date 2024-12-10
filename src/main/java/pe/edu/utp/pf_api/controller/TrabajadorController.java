package pe.edu.utp.pf_api.controller;

import pe.edu.utp.pf_api.model.Trabajador;
import pe.edu.utp.pf_api.service.TrabajadorService;
import pe.edu.utp.pf_api.util.LogFile;
import pe.edu.utp.pf_api.model.ErrorResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/trabajadores")
public class TrabajadorController {

    private final TrabajadorService trabajadorService = new TrabajadorService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTrabajadores() {
        try {
            List<Trabajador> trabajadores = trabajadorService.findAllTrabajadores();
            return Response.ok(trabajadores).build();
        } catch (Exception e) {
            LogFile.error("Error al obtener trabajadores: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al obtener trabajadores"))
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTrabajador(Trabajador trabajador) {
        try {
            // Validaciones
            if (trabajador.getNombres() == null || trabajador.getNombres().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "Nombres son requeridos"))
                        .build();
            }

            trabajadorService.createTrabajador(trabajador);
            return Response.status(Response.Status.CREATED).entity(trabajador).build();
        } catch (Exception e) {
            LogFile.error("Error al crear trabajador: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al crear trabajador"))
                    .build();
        }
    }

    @DELETE
    @Path("/{nombres}/{apellidos}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTrabajador(@PathParam("nombres") String nombres,
                                     @PathParam("apellidos") String apellidos) {
        try {
            boolean deleted = trabajadorService.deleteTrabajador(nombres, apellidos);
            if (deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Trabajador eliminado exitosamente");
                return Response.ok(response).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Error", "Trabajador no encontrado"))
                        .build();
            }
        } catch (Exception e) {
            LogFile.error("Error al eliminar trabajador: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al eliminar trabajador"))
                    .build();
        }
    }
}