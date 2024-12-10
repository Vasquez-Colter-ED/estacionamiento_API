package pe.edu.utp.pf_api.controller;

import pe.edu.utp.pf_api.model.Conductor;
import pe.edu.utp.pf_api.service.ConductorService;
import pe.edu.utp.pf_api.util.LogFile;
import pe.edu.utp.pf_api.model.ErrorResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/conductores")
public class ConductorController {

    private final ConductorService conductorService = new ConductorService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllConductores() {
        try {
            LogFile.info("Obteniendo lista de conductores");
            List<Conductor> conductores = conductorService.findAllConductores();
            return Response.ok(conductores).build();
        } catch (Exception e) {
            LogFile.error("Error al obtener conductores: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al obtener conductores"))
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createConductor(Conductor conductor) {
        try {
            if (conductor.getNombre() == null || conductor.getNombre().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "Nombre es requerido"))
                        .build();
            }

            if (conductor.getDni() == null || conductor.getDni().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "DNI es requerido"))
                        .build();
            }

            conductorService.createConductor(conductor);
            return Response.status(Response.Status.CREATED).entity(conductor).build();
        } catch (Exception e) {
            LogFile.error("Error al crear conductor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al crear conductor"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteConductor(@PathParam("id") int id) {
        try {
            boolean deleted = conductorService.deleteConductor(id);
            if (deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Conductor eliminado exitosamente");
                return Response.ok(response).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Error", "Conductor no encontrado"))
                        .build();
            }
        } catch (Exception e) {
            LogFile.error("Error al eliminar conductor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al eliminar conductor"))
                    .build();
        }
    }
}