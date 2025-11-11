package com.softly.fonoteca.Modelos.DTOs;

import java.time.LocalDate;
import java.time.LocalTime;

public class Calificacion {
    private int idUsuario;
    private int idCancion;
    private String calificacion;
    private String comentario;
    private LocalDate fechaCalificacion;
    private LocalTime horaCalificacion;

    public Calificacion() {
    }

    public Calificacion(int idUsuario, int idCancion, String calificacion, String comentario, LocalDate fechaCalificacion, LocalTime horaCalificacion) {
        this.idUsuario = idUsuario;
        this.idCancion = idCancion;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fechaCalificacion = fechaCalificacion;
        this.horaCalificacion = horaCalificacion;
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

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDate getFechaCalificacion() {
        return fechaCalificacion;
    }

    public void setFechaCalificacion(LocalDate fechaCalificacion) {
        this.fechaCalificacion = fechaCalificacion;
    }

    public LocalTime getHoraCalificacion() {
        return horaCalificacion;
    }

    public void setHoraCalificacion(LocalTime horaCalificacion) {
        this.horaCalificacion = horaCalificacion;
    }
}
