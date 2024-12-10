package pe.edu.utp.pf_api.repository;

import pe.edu.utp.pf_api.model.Vehiculo;
import pe.edu.utp.pf_api.model.Conductor;
import pe.edu.utp.pf_api.util.DataAccessMariaDB;
import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoRepository {

    public List<Vehiculo> findAllVehiculos() throws SQLException, NamingException {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String query = "SELECT v.*, c.nombre as conductor_nombre, c.dni as conductor_dni " +
                "FROM Vehiculo v " +
                "LEFT JOIN Conductor c ON v.idConductor = c.idConductor";

        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setIdVehiculo(rs.getInt("idVehiculo"));
                vehiculo.setPlaca(rs.getString("placa"));
                vehiculo.setTipo(rs.getString("tipo"));
                vehiculo.setIdConductor(rs.getInt("idConductor"));

                // Crear objeto Conductor
                Conductor conductor = new Conductor();
                conductor.setIdConductor(rs.getInt("idConductor"));
                conductor.setNombre(rs.getString("conductor_nombre"));
                conductor.setDni(rs.getString("conductor_dni"));
                vehiculo.setConductor(conductor);

                vehiculos.add(vehiculo);
            }
        }
        return vehiculos;
    }

    public void saveVehiculo(Vehiculo vehiculo) throws SQLException, NamingException {
        String query = "INSERT INTO Vehiculo (placa, tipo, idConductor) VALUES (?, ?, ?)";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vehiculo.getPlaca());
            stmt.setString(2, vehiculo.getTipo());
            stmt.setInt(3, vehiculo.getIdConductor());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vehiculo.setIdVehiculo(generatedKeys.getInt(1));
                }
            }
        }
    }

    public boolean deleteVehiculo(int idVehiculo) throws SQLException, NamingException {
        String query = "DELETE FROM Vehiculo WHERE idVehiculo = ?";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idVehiculo);
            return stmt.executeUpdate() > 0;
        }
    }

    public Vehiculo findVehiculoByPlaca(String placa) throws SQLException, NamingException {
        String query = "SELECT v.*, c.nombre as conductor_nombre, c.dni as conductor_dni " +
                "FROM Vehiculo v " +
                "LEFT JOIN Conductor c ON v.idConductor = c.idConductor " +
                "WHERE v.placa = ?";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, placa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vehiculo vehiculo = new Vehiculo();
                    vehiculo.setIdVehiculo(rs.getInt("idVehiculo"));
                    vehiculo.setPlaca(rs.getString("placa"));
                    vehiculo.setTipo(rs.getString("tipo"));
                    vehiculo.setIdConductor(rs.getInt("idConductor"));

                    Conductor conductor = new Conductor();
                    conductor.setIdConductor(rs.getInt("idConductor"));
                    conductor.setNombre(rs.getString("conductor_nombre"));
                    conductor.setDni(rs.getString("conductor_dni"));
                    vehiculo.setConductor(conductor);

                    return vehiculo;
                }
            }
        }
        return null;
    }
}