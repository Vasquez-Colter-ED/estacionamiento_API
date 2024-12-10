package pe.edu.utp.pf_api.model;

public class Conductor {
    private int idConductor;
    private String nombre;
    private String dni;

    // Getters and Setters
    public int getIdConductor() { return idConductor; }
    public void setIdConductor(int idConductor) { this.idConductor = idConductor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
}