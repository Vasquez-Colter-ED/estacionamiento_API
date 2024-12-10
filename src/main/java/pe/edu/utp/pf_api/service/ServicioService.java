package pe.edu.utp.pf_api.service;

import pe.edu.utp.pf_api.model.Conductor;
import pe.edu.utp.pf_api.model.Servicio;
import pe.edu.utp.pf_api.model.Vehiculo;
import pe.edu.utp.pf_api.repository.ServicioRepository;
import pe.edu.utp.pf_api.util.LogFile;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ServicioService {
    private final ServicioRepository servicioRepository = new ServicioRepository();
    private final ConductorService conductorService = new ConductorService();
    private final VehiculoService vehiculoService = new VehiculoService();

    public List<Servicio> findAllServicios() throws SQLException {
        try {
            LogFile.info("Obteniendo lista de servicios");
            return servicioRepository.findAllServicios();
        } catch (Exception e) {
            LogFile.error("Error al obtener servicios: " + e.getMessage());
            throw new SQLException("Error retrieving servicios", e);
        }
    }

    public void createServicio(Servicio servicio) throws SQLException {
        try {
            LogFile.info("Creando nuevo servicio para vehículo ID: " + servicio.getIdVehiculo());
            servicioRepository.saveServicio(servicio);
            LogFile.info("Servicio creado exitosamente con ID: " + servicio.getIdServicio());
        } catch (Exception e) {
            LogFile.error("Error al crear servicio: " + e.getMessage());
            throw new SQLException("Error creating servicio", e);
        }
    }

    public void createServicioCompleto(Servicio servicio) throws SQLException {
        try {
            // Validaciones iniciales
            if (servicio == null) {
                throw new SQLException("El servicio no puede ser null");
            }
            if (servicio.getVehiculo() == null) {
                throw new SQLException("El vehículo no puede ser null");
            }
            if (servicio.getVehiculo().getConductor() == null) {
                throw new SQLException("El conductor no puede ser null");
            }
            if (servicio.getVehiculo().getConductor().getDni() == null) {
                throw new SQLException("El DNI del conductor no puede ser null");
            }
            if (servicio.getVehiculo().getPlaca() == null) {
                throw new SQLException("La placa del vehículo no puede ser null");
            }

            // Forzar la fecha de ingreso al momento actual
            servicio.setFechaHoraIngreso(LocalDateTime.now());
            
            // Validar que la fecha de salida sea posterior a la de ingreso
            if (servicio.getFechaHoraSalida().isBefore(servicio.getFechaHoraIngreso())) {
                throw new SQLException("La fecha de salida no puede ser anterior a la fecha de ingreso");
            }

            LogFile.info("Iniciando creación de servicio completo");

            // 1. Verificar/Crear Conductor
            final Conductor conductorOriginal = servicio.getVehiculo().getConductor();
            List<Conductor> conductoresExistentes = conductorService.findAllConductores();
            Conductor conductorFinal = conductoresExistentes.stream()
                    .filter(c -> c != null && c.getDni() != null && 
                            conductorOriginal != null && conductorOriginal.getDni() != null && 
                            c.getDni().equals(conductorOriginal.getDni()))
                    .findFirst()
                    .orElse(null);

            if (conductorFinal == null) {
                LogFile.info("Creando nuevo conductor con DNI: " + conductorOriginal.getDni());
                conductorService.createConductor(conductorOriginal);
                conductorFinal = conductorOriginal;
            }

            // 2. Verificar/Crear Vehículo
            final Vehiculo vehiculoOriginal = servicio.getVehiculo();
            vehiculoOriginal.setIdConductor(conductorFinal.getIdConductor());
            
            List<Vehiculo> vehiculosExistentes = vehiculoService.findAllVehiculos();
            Vehiculo vehiculoFinal = vehiculosExistentes.stream()
                    .filter(v -> v.getPlaca().equals(vehiculoOriginal.getPlaca()))
                    .findFirst()
                    .orElse(null);

            if (vehiculoFinal == null) {
                LogFile.info("Creando nuevo vehículo con placa: " + vehiculoOriginal.getPlaca());
                vehiculoService.createVehiculo(vehiculoOriginal);
                vehiculoFinal = vehiculoOriginal;
            }

            // 3. Crear Servicio
            servicio.setIdVehiculo(vehiculoFinal.getIdVehiculo());
            servicioRepository.saveServicio(servicio);

            // 4. Actualizar objetos relacionados para la respuesta
            servicio.setVehiculo(vehiculoFinal);
            vehiculoFinal.setConductor(conductorFinal);

            LogFile.info("Servicio creado exitosamente con ID: " + servicio.getIdServicio());
        } catch (Exception e) {
            LogFile.error("Error al crear servicio completo: " + e.getMessage());
            throw new SQLException("Error creating servicio completo", e);
        }
    }

    public boolean deleteServicio(int idServicio) throws SQLException {
        try {
            LogFile.info("Eliminando servicio con ID: " + idServicio);
            boolean deleted = servicioRepository.deleteServicio(idServicio);
            if (deleted) {
                LogFile.info("Servicio eliminado exitosamente");
            } else {
                LogFile.info("No se encontró servicio con ID: " + idServicio);
            }
            return deleted;
        } catch (Exception e) {
            LogFile.error("Error al eliminar servicio: " + e.getMessage());
            throw new SQLException("Error deleting servicio", e);
        }
    }
}