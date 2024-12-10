package pe.edu.utp.pf_api.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class Servicio {
    private int idServicio;
    private LocalDateTime fechaHoraIngreso;
    private LocalDateTime fechaHoraSalida;
    private BigDecimal montoCobro;
    private String comentario;
    private int idVehiculo;
    private boolean lavado;
    private Vehiculo vehiculo; // Objeto para la relación

    // Getters and Setters
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public LocalDateTime getFechaHoraIngreso() { return fechaHoraIngreso; }
    public void setFechaHoraIngreso(LocalDateTime fechaHoraIngreso) {
        this.fechaHoraIngreso = fechaHoraIngreso;
    }

    public LocalDateTime getFechaHoraSalida() { return fechaHoraSalida; }
    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public BigDecimal getMontoCobro() { return montoCobro; }
    public void setMontoCobro(BigDecimal montoCobro) { this.montoCobro = montoCobro; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public boolean isLavado() { return lavado; }
    public void setLavado(boolean lavado) { this.lavado = lavado; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
}