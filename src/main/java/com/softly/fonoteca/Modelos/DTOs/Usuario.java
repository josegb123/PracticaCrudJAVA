
package com.softly.fonoteca.Modelos.DTOs;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Usuario {
    private int id;
    private String email;
    private String hashedPassword;
    private String nombres;
    private String apellidos;
    private String sexo;
    private LocalDate fechaNacimiento;
    private String paisNacimiento;
    private String paisResidencia;
    private String idioma;
    private LocalDateTime fechaRegistro;

    public Usuario() {
    }

    public Usuario(int id, String email, String hashedPassword, String nombres, String apellidos, String sexo,
                   LocalDate fechaNacimiento, String paisNacimiento, String paisResidencia, String idioma,
                   LocalDateTime fechaRegistro) {
        this.id = id;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.paisNacimiento = paisNacimiento;
        this.paisResidencia = paisResidencia;
        this.idioma = idioma;
        this.fechaRegistro = fechaRegistro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getPaisNacimiento() {
        return paisNacimiento;
    }

    public void setPaisNacimiento(String paisNacimiento) {
        this.paisNacimiento = paisNacimiento;
    }

    public String getPaisResidencia() {
        return paisResidencia;
    }

    public void setPaisResidencia(String paisResidencia) {
        this.paisResidencia = paisResidencia;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

}
