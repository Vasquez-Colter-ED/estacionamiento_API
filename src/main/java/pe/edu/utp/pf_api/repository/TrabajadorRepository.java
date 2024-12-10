package pe.edu.utp.pf_api.repository;

import pe.edu.utp.pf_api.model.Trabajador;
import pe.edu.utp.pf_api.util.DataAccessMariaDB;
import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrabajadorRepository {

    public List<Trabajador> findAllTrabajadores() throws SQLException, NamingException {
        List<Trabajador> trabajadores = new ArrayList<>();
        String query = "SELECT * FROM Trabajador";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Trabajador trabajador = new Trabajador();
                trabajador.setIdTrabajador(rs.getInt("idTrabajador"));
                trabajador.setNombres(rs.getString("nombres"));
                trabajador.setApellidos(rs.getString("apellidos"));
                trabajador.setEdad(rs.getInt("edad"));
                trabajador.setDni(rs.getString("dni"));
                trabajador.setTelefono(rs.getString("telefono"));
                trabajador.setEmail(rs.getString("email"));
                trabajador.setTurno(rs.getString("turno"));
                trabajadores.add(trabajador);
            }
        }
        return trabajadores;
    }

    public void saveTrabajador(Trabajador trabajador) throws SQLException, NamingException {
        String query = "INSERT INTO Trabajador (nombres, apellidos, edad, dni, telefono, email, turno) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataAccessMariaDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, trabajador.getNombres());
            stmt.setString(2, trabajador.getApellidos());
            stmt.setInt(3, trabajador.getEdad());
            stmt.setString(4, trabajador.getDni());
            stmt.setString(5, trabajador.getTelefono());
            stmt.setString(6, trabajador.getEmail());
            stmt.setString(7, trabajador.getTurno());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    trabajador.setIdTrabajador(generatedKeys.getInt(1));
                }
            }
        }
    }

    public boolean deleteTrabajador(String nombres, String apellidos) throws SQLException, NamingException {
        String query = "CALL eliminarTrabajador(?, ?)";
        try (Connection conn = DataAccessMariaDB.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setString(1, nombres);
            stmt.setString(2, apellidos);
            return stmt.executeUpdate() > 0;
        }
    }
}