package pe.edu.utp.pf_api.controller;

import pe.edu.utp.pf_api.model.Vehiculo;
import pe.edu.utp.pf_api.service.VehiculoService;
import pe.edu.utp.pf_api.util.LogFile;
import pe.edu.utp.pf_api.model.ErrorResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService = new VehiculoService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllVehiculos() {
        try {
            List<Vehiculo> vehiculos = vehiculoService.findAllVehiculos();
            return Response.ok(vehiculos).build();
        } catch (Exception e) {
            LogFile.error("Error al obtener vehículos: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al obtener vehículos"))
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVehiculo(Vehiculo vehiculo) {
        try {
            if (vehiculo.getPlaca() == null || vehiculo.getPlaca().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "Placa es requerida"))
                        .build();
            }

            if (vehiculo.getIdConductor() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Error de validación", "ID de conductor inválido"))
                        .build();
            }

            vehiculoService.createVehiculo(vehiculo);
            return Response.status(Response.Status.CREATED).entity(vehiculo).build();
        } catch (Exception e) {
            LogFile.error("Error al crear vehículo: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al crear vehículo"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteVehiculo(@PathParam("id") int id) {
        try {
            boolean deleted = vehiculoService.deleteVehiculo(id);
            if (deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Vehículo eliminado exitosamente");
                return Response.ok(response).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Error", "Vehículo no encontrado"))
                        .build();
            }
        } catch (Exception e) {
            LogFile.error("Error al eliminar vehículo: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", "Error al eliminar vehículo"))
                    .build();
        }
    }
}