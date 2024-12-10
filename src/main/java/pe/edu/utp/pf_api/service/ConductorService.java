package pe.edu.utp.pf_api.service;

import pe.edu.utp.pf_api.model.Conductor;
import pe.edu.utp.pf_api.repository.ConductorRepository;
import pe.edu.utp.pf_api.util.LogFile;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ConductorService {
    private final ConductorRepository conductorRepository = new ConductorRepository();

    public List<Conductor> findAllConductores() throws SQLException {
        try {
            LogFile.info("Obteniendo lista de conductores");
            return conductorRepository.findAllConductores();
        } catch (Exception e) {
            LogFile.error("Error al obtener conductores: " + e.getMessage());
            throw new SQLException("Error retrieving conductores", e);
        }
    }

    public void createConductor(Conductor conductor) throws IOException {
        try {
            LogFile.info("Creando nuevo conductor: " + conductor.getNombre());
            conductorRepository.saveConductor(conductor);
            LogFile.info("Conductor creado exitosamente");
        } catch (Exception e) {
            LogFile.error("Error al crear conductor: " + e.getMessage());
            throw new RuntimeException("Error creating conductor", e);
        }
    }

    public boolean deleteConductor(int idConductor) throws SQLException {
        try {
            LogFile.info("Eliminando conductor con ID: " + idConductor);
            boolean deleted = conductorRepository.deleteConductor(idConductor);
            if (deleted) {
                LogFile.info("Conductor eliminado exitosamente");
            } else {
                LogFile.info("No se encontró conductor con ID: " + idConductor);
            }
            return deleted;
        } catch (Exception e) {
            LogFile.error("Error al eliminar conductor: " + e.getMessage());
            throw new SQLException("Error deleting conductor", e);
        }
    }
}