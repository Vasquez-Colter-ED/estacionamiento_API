package pe.edu.utp.pf_api.service;

import pe.edu.utp.pf_api.model.Vehiculo;
import pe.edu.utp.pf_api.repository.VehiculoRepository;
import pe.edu.utp.pf_api.util.LogFile;
import java.sql.SQLException;
import java.util.List;

public class VehiculoService {
    private final VehiculoRepository vehiculoRepository = new VehiculoRepository();

    public List<Vehiculo> findAllVehiculos() throws SQLException {
        try {
            LogFile.info("Obteniendo lista de vehículos");
            return vehiculoRepository.findAllVehiculos();
        } catch (Exception e) {
            LogFile.error("Error al obtener vehículos: " + e.getMessage());
            throw new SQLException("Error retrieving vehiculos", e);
        }
    }

    public void createVehiculo(Vehiculo vehiculo) throws SQLException {
        try {
            LogFile.info("Creando nuevo vehículo con placa: " + vehiculo.getPlaca());
            vehiculoRepository.saveVehiculo(vehiculo);
        } catch (Exception e) {
            LogFile.error("Error al crear vehículo: " + e.getMessage());
            throw new SQLException("Error creating vehiculo", e);
        }
    }

    public boolean deleteVehiculo(int idVehiculo) throws SQLException {
        try {
            LogFile.info("Eliminando vehículo con ID: " + idVehiculo);
            return vehiculoRepository.deleteVehiculo(idVehiculo);
        } catch (Exception e) {
            LogFile.error("Error al eliminar vehículo: " + e.getMessage());
            throw new SQLException("Error deleting vehiculo", e);
        }
    }
}