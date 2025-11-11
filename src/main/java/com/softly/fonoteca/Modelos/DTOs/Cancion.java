package com.softly.fonoteca.Modelos.DTOs;

import java.time.LocalDate;

public class Cancion {
    private int idCancion;
    private String titulo;
    private String duracion;
    private int bpm;
    private String idioma;
    private LocalDate fechaLanzamiento;
    private int album;
    private int genero;
    private int interprete;
    private boolean instrumental;

    public Cancion() {
    }

    public Cancion(int idCancion, String titulo, String duracion, int bpm, String idioma, LocalDate fechaLanzamiento, int album, int genero, int interprete, boolean instrumental) {
        this.idCancion = idCancion;
        this.titulo = titulo;
        this.duracion = duracion;
        this.bpm = bpm;
        this.idioma = idioma;
        this.fechaLanzamiento = fechaLanzamiento;
        this.album = album;
        this.genero = genero;
        this.interprete = interprete;
        this.instrumental = instrumental;
    }

    public int getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(int idCancion) {
        this.idCancion = idCancion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public LocalDate getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public int getAlbum() {
        return album;
    }

    public void setAlbum(int album) {
        this.album = album;
    }

    public int getGenero() {
        return genero;
    }

    public void setGenero(int genero) {
        this.genero = genero;
    }

    public int getInterprete() {
        return interprete;
    }

    public void setInterprete(int interprete) {
        this.interprete = interprete;
    }

    public boolean isInstrumental() {
        return instrumental;
    }

    public void setInstrumental(boolean instrumental) {
        this.instrumental = instrumental;
    }

    @Override
    public String toString() {
        return "Cancion{" +
                "idCancion=" + idCancion +
                ", titulo='" + titulo + '\'' +
                ", duracion=" + duracion +
                ", bpm=" + bpm +
                ", idioma='" + idioma + '\'' +
                ", fechaLanzamiento=" + fechaLanzamiento +
                ", album=" + album +
                ", genero=" + genero +
                ", interprete=" + interprete +
                ", instrumental=" + instrumental +
                '}';
    }
}
