package pe.edu.utp.pf_api.controller;

import pe.edu.utp.pf_api.model.Servicio;
import pe.edu.utp.pf_api.service.ServicioService;
import pe.edu.utp.pf_api.util.LogFile;
import pe.edu.utp.pf_api.model.ErrorResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/servicios")
public class ServicioController {

    private final ServicioService servicioService = new ServicioService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllServicios() {
        try {
            List<Servicio> servicios = servicioService.findAllServicios();
            return Response.ok(servicios).build();
        } catch (Exception e) {
            LogFile.error("Error al obtener servicios: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al obtener servicios"))
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createServicio(Servicio servicio) {
        try {
            // Validaciones básicas
            if (servicio.getFechaHoraSalida() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "Fecha de salida requerida"))
                        .build();
            }

            if (servicio.getMontoCobro() == null || servicio.getMontoCobro().doubleValue() < 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "Monto de cobro inválido"))
                        .build();
            }

            if (servicio.getVehiculo() == null ||
                    servicio.getVehiculo().getPlaca() == null ||
                    servicio.getVehiculo().getPlaca().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "Datos del vehículo requeridos"))
                        .build();
            }

            if (servicio.getVehiculo().getConductor() == null ||
                    servicio.getVehiculo().getConductor().getDni() == null ||
                    servicio.getVehiculo().getConductor().getDni().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "Datos del conductor requeridos"))
                        .build();
            }

            servicioService.createServicioCompleto(servicio);
            return Response.status(Response.Status.CREATED).entity(servicio).build();
        } catch (Exception e) {
            LogFile.error("Error al crear servicio: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al crear servicio"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteServicio(@PathParam("id") int id) {
        try {
            boolean deleted = servicioService.deleteServicio(id);
            if (deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Servicio eliminado exitosamente");
                return Response.ok(response).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Error", "Servicio no encontrado"))
                        .build();
            }
        } catch (Exception e) {
            LogFile.error("Error al eliminar servicio: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al eliminar servicio"))
                    .build();
        }
    }
}