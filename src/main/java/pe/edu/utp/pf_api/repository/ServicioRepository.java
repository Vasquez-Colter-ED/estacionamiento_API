package pe.edu.utp.pf_api.repository;

import pe.edu.utp.pf_api.model.Servicio;
import pe.edu.utp.pf_api.model.Vehiculo;
import pe.edu.utp.pf_api.model.Conductor;
import pe.edu.utp.pf_api.util.DataAccessMariaDB;
import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioRepository {

    public List<Servicio> findAllServicios() throws SQLException, NamingException {
        List<Servicio> servicios = new ArrayList<>();
        String query = "SELECT s.*, v.placa, v.tipo, " +
                "c.idConductor, c.nombre as conductor_nombre, c.dni as conductor_dni " +
                "FROM Servicio s " +
                "LEFT JOIN Vehiculo v ON s.idVehiculo = v.idVehiculo " +
                "LEFT JOIN Conductor c ON v.idConductor = c.idConductor";

        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Servicio servicio = new Servicio();
                servicio.setIdServicio(rs.getInt("idServicio"));
                servicio.setFechaHoraIngreso(rs.getTimestamp("fechaHoraIngreso").toLocalDateTime());
                servicio.setFechaHoraSalida(rs.getTimestamp("fechaHoraSalida").toLocalDateTime());
                servicio.setMontoCobro(rs.getBigDecimal("montoCobro"));
                servicio.setComentario(rs.getString("comentario"));
                servicio.setIdVehiculo(rs.getInt("idVehiculo"));
                servicio.setLavado(rs.getBoolean("lavado"));

                // Crear objeto Vehiculo con su Conductor
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
                servicio.setVehiculo(vehiculo);

                servicios.add(servicio);
            }
        }
        return servicios;
    }

    public void saveServicio(Servicio servicio) throws SQLException, NamingException {
        String query = "INSERT INTO Servicio (fechaHoraIngreso, fechaHoraSalida, montoCobro, " +
                "comentario, idVehiculo, lavado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(servicio.getFechaHoraIngreso()));
            stmt.setTimestamp(2, Timestamp.valueOf(servicio.getFechaHoraSalida()));
            stmt.setBigDecimal(3, servicio.getMontoCobro());
            stmt.setString(4, servicio.getComentario());
            stmt.setInt(5, servicio.getIdVehiculo());
            stmt.setBoolean(6, servicio.isLavado());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    servicio.setIdServicio(generatedKeys.getInt(1));
                }
            }
        }
    }

    public boolean deleteServicio(int idServicio) throws SQLException, NamingException {
        String query = "DELETE FROM Servicio WHERE idServicio = ?";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idServicio);
            return stmt.executeUpdate() > 0;
        }
    }
}