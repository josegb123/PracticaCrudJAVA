package com.softly.fonoteca.Modelos.DTOs;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reproduccion {
    private int idUsuario;
    private int idCancion;
    private LocalDate fechaReproduccion;
    private LocalTime horaReproduccion;
    private int segundosReproducidos;

    public Reproduccion() {
    }

    public Reproduccion(int idUsuario, int idCancion, LocalDate fechaReproduccion, LocalTime horaReproduccion, int segundosReproducidos) {
        this.idUsuario = idUsuario;
        this.idCancion = idCancion;
        this.fechaReproduccion = fechaReproduccion;
        this.horaReproduccion = horaReproduccion;
        this.segundosReproducidos = segundosReproducidos;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(int idCancion) {
        this.idCancion = idCancion;
    }

    public LocalDate getFechaReproduccion() {
        return fechaReproduccion;
    }

    public void setFechaReproduccion(LocalDate fechaReproduccion) {
        this.fechaReproduccion = fechaReproduccion;
    }

    public LocalTime getHoraReproduccion() {
        return horaReproduccion;
    }

    public void setHoraReproduccion(LocalTime horaReproduccion) {
        this.horaReproduccion = horaReproduccion;
    }

    public int getSegundosReproducidos() {
        return segundosReproducidos;
    }

    public void setSegundosReproducidos(int segundosReproducidos) {
        this.segundosReproducidos = segundosReproducidos;
    }
}
