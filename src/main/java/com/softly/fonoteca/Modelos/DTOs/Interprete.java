package com.softly.fonoteca.Modelos.DTOs;

import java.time.LocalDate;

public class Interprete {
    private int idInterprete;
    private String nombre;
    private LocalDate yearLanzamiento;
    private LocalDate yearRetiro;
    private String tituloInterprete;
    private int idGeneroPrincipal;

    public Interprete() {
    }

    public Interprete(int idInterprete, String nombre, LocalDate yearLanzamiento, LocalDate yearRetiro, String tituloInterprete, int idGeneroPrincipal) {
        this.idInterprete = idInterprete;
        this.nombre = nombre;
        this.yearLanzamiento = yearLanzamiento;
        this.yearRetiro = yearRetiro;
        this.tituloInterprete = tituloInterprete;
        this.idGeneroPrincipal = idGeneroPrincipal;
    }

    public int getIdInterprete() {
        return idInterprete;
    }

    public void setIdInterprete(int idInterprete) {
        this.idInterprete = idInterprete;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getYearLanzamiento() {
        return yearLanzamiento;
    }

    public void setYearLanzamiento(LocalDate yearLanzamiento) {
        this.yearLanzamiento = yearLanzamiento;
    }

    public LocalDate getYearRetiro() {
        return yearRetiro;
    }

    public void setYearRetiro(LocalDate yearRetiro) {
        this.yearRetiro = yearRetiro;
    }

    public String getTituloInterprete() {
        return tituloInterprete;
    }

    public void setTituloInterprete(String tituloInterprete) {
        this.tituloInterprete = tituloInterprete;
    }

    public int getIdGeneroPrincipal() {
        return idGeneroPrincipal;
    }

    public void setIdGeneroPrincipal(int idGeneroPrincipal) {
        this.idGeneroPrincipal = idGeneroPrincipal;
    }
}
