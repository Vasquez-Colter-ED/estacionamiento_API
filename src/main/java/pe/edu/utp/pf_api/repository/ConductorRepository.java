package pe.edu.utp.pf_api.repository;

import pe.edu.utp.pf_api.model.Conductor;
import pe.edu.utp.pf_api.util.DataAccessMariaDB;
import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConductorRepository {

    public List<Conductor> findAllConductores() throws SQLException, NamingException {
        List<Conductor> conductores = new ArrayList<>();
        String query = "SELECT * FROM Conductor";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Conductor conductor = new Conductor();
                conductor.setIdConductor(rs.getInt("idConductor"));
                conductor.setNombre(rs.getString("nombre"));
                conductor.setDni(rs.getString("dni"));
                conductores.add(conductor);
            }
        }
        return conductores;
    }

    public void saveConductor(Conductor conductor) throws SQLException, NamingException {
        String query = "INSERT INTO Conductor (nombre, dni) VALUES (?, ?)";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, conductor.getNombre());
            stmt.setString(2, conductor.getDni());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    conductor.setIdConductor(generatedKeys.getInt(1));
                }
            }
        }
    }

    public boolean deleteConductor(int idConductor) throws SQLException, NamingException {
        String query = "DELETE FROM Conductor WHERE idConductor = ?";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idConductor);
            return stmt.executeUpdate() > 0;
        }
    }

    public Conductor findConductorByDni(String dni) throws SQLException, NamingException {
        String query = "SELECT * FROM Conductor WHERE dni = ?";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Conductor conductor = new Conductor();
                    conductor.setIdConductor(rs.getInt("idConductor"));
                    conductor.setNombre(rs.getString("nombre"));
                    conductor.setDni(rs.getString("dni"));
                    return conductor;
                }
            }
        }
        return null;
    }
}