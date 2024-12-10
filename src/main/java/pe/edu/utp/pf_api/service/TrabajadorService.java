package pe.edu.utp.pf_api.service;

import pe.edu.utp.pf_api.model.Trabajador;
import pe.edu.utp.pf_api.repository.TrabajadorRepository;
import pe.edu.utp.pf_api.util.LogFile;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TrabajadorService {
    private final TrabajadorRepository trabajadorRepository = new TrabajadorRepository();

    public List<Trabajador> findAllTrabajadores() throws SQLException {
        try {
            LogFile.info("Obteniendo lista de trabajadores");
            return trabajadorRepository.findAllTrabajadores();
        } catch (Exception e) {
            LogFile.error("Error al obtener trabajadores: " + e.getMessage());
            throw new SQLException("Error retrieving trabajadores", e);
        }
    }

    public void createTrabajador(Trabajador trabajador) throws IOException {
        try {
            LogFile.info("Creando nuevo trabajador: " + trabajador.getNombres() + " " + trabajador.getApellidos());
            trabajadorRepository.saveTrabajador(trabajador);
            LogFile.info("Trabajador creado exitosamente");
        } catch (Exception e) {
            LogFile.error("Error al crear trabajador: " + e.getMessage());
            throw new RuntimeException("Error creating trabajador", e);
        }
    }

    public boolean deleteTrabajador(String nombres, String apellidos) throws SQLException {
        try {
            LogFile.info("Eliminando trabajador: " + nombres + " " + apellidos);
            boolean deleted = trabajadorRepository.deleteTrabajador(nombres, apellidos);
            if (deleted) {
                LogFile.info("Trabajador eliminado exitosamente");
            } else {
                LogFile.info("No se encontró trabajador con nombres: " + nombres + " " + apellidos);
            }
            return deleted;
        } catch (Exception e) {
            LogFile.error("Error al eliminar trabajador: " + e.getMessage());
            throw new SQLException("Error deleting trabajador", e);
        }
    }
}