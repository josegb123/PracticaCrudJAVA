package com.softly.fonoteca.Modelos.DTOs;

import java.time.LocalDate;

public class Album {
    private int idAlbum;
    private String titulo;
    private String selloDiscografico;
    private LocalDate fechaLanzamiento;
    private int idGeneroPrincipal;

    public Album(int idAlbum, String titulo, String selloDiscografico, LocalDate fechaLanzamiento, int idGeneroPrincipal) {
        this.idAlbum = idAlbum;
        this.titulo = titulo;
        this.selloDiscografico = selloDiscografico;
        this.fechaLanzamiento = fechaLanzamiento;
        this.idGeneroPrincipal = idGeneroPrincipal;
    }

    public Album() {
    }

    public int getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(int idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSelloDiscografico() {
        return selloDiscografico;
    }

    public void setSelloDiscografico(String selloDiscografico) {
        this.selloDiscografico = selloDiscografico;
    }

    public LocalDate getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public int getIdGeneroPrincipal() {
        return idGeneroPrincipal;
    }

    public void setIdGeneroPrincipal(int idGeneroPrincipal) {
        this.idGeneroPrincipal = idGeneroPrincipal;
    }

    @Override
    public String toString() {
        return "Album{" +
                "titulo='" + titulo + '\'' +
                ", selloDiscografico='" + selloDiscografico + '\'' +
                ", fechaLanzamiento=" + fechaLanzamiento +
                ", idGeneroPrincipal=" + idGeneroPrincipal +
                '}';
    }
}
