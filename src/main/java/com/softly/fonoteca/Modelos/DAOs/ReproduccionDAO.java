package com.softly.fonoteca.Modelos.DAOs;

import com.softly.fonoteca.Modelos.DTOs.Reproduccion;
import com.softly.fonoteca.utilities.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReproduccionDAO {

    // ReproduccionDAO.java (vincular corregido a UPSERT)
    public boolean vincular(Reproduccion reproduccion) {
        String sql = "INSERT INTO reproducciones(idUsuario, idCancion, " +
                "fechaReproduccion, horaReproduccion, segundosReproducidos) VALUES (?,?,?,?,?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reproduccion.getIdUsuario());
            ps.setInt(2, reproduccion.getIdCancion());
            ps.setDate(3, java.sql.Date.valueOf(reproduccion.getFechaReproduccion()));
            ps.setTime(4, java.sql.Time.valueOf(reproduccion.getHoraReproduccion()));
            ps.setInt(5, reproduccion.getSegundosReproducidos());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // ERROR: 1062 - Clave primaria duplicada
            // Si ya existe la clave, modificamos el registro.
            if (e.getErrorCode() == 1062) {
                System.out.println("El registro ya existe, intentando modificar...");
                return modificar(reproduccion); // Llama al método de actualización
            }
            System.err.println("Error al vincular Cancion y usuario en reproducciones: " + e.getMessage());
            return false;
        }
    }

    public boolean desvincular(Reproduccion reproduccion) {
        String sql = "DELETE FROM reproducciones WHERE idCancion = ? AND idUsuario = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reproduccion.getIdCancion());
            ps.setInt(2, reproduccion.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al desvincular Cancion y usuario: " + e.getMessage());
            return false;
        }
    }

    // ReproduccionDAO.java (modificar corregido)
    public boolean modificar(Reproduccion reproduccion) {
        String sql = "UPDATE reproducciones SET fechaReproduccion = ?, horaReproduccion = ?, segundosReproducidos = ? " +
                "WHERE idUsuario = ? AND idCancion = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Parámetros SET (1, 2, 3)
            ps.setDate(1, java.sql.Date.valueOf(reproduccion.getFechaReproduccion()));
            ps.setTime(2, java.sql.Time.valueOf(reproduccion.getHoraReproduccion()));
            ps.setInt(3, reproduccion.getSegundosReproducidos());

            // Parámetros WHERE (4, 5)
            ps.setInt(4, reproduccion.getIdUsuario());
            ps.setInt(5, reproduccion.getIdCancion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al modificar la reproduccion: " + e.getMessage());
            return false;
        }
    }
    // ReproduccionDAO.java (getReproduccion corregido)
    public Reproduccion getReproduccion(Reproduccion reproduccion) {
        String sql = "SELECT * FROM reproducciones WHERE idCancion = ? AND idUsuario = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // CORRECCIÓN: 1=idCancion, 2=idUsuario
            ps.setInt(1, reproduccion.getIdCancion());
            ps.setInt(2, reproduccion.getIdUsuario());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { // Usamos if porque solo puede haber un registro
                    // No necesitas crear un nuevo objeto, puedes rellenar el que viene:
                    reproduccion.setIdUsuario(rs.getInt("idUsuario"));
                    reproduccion.setIdCancion(rs.getInt("idCancion"));
                    reproduccion.setFechaReproduccion(rs.getObject("fechaReproduccion", LocalDate.class));
                    reproduccion.setHoraReproduccion(rs.getObject("horaReproduccion", LocalTime.class));
                    reproduccion.setSegundosReproducidos(rs.getInt("segundosReproducidos"));
                    return reproduccion;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reproduccion: " + e.getMessage());
        }

        return null; // Retorna null si no se encuentra o hay error
    }
}
