package com.softly.fonoteca.Modelos.DAOs;

import com.softly.fonoteca.Modelos.DTOs.Calificacion;
import com.softly.fonoteca.utilities.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class CalificacionDAO {


    public boolean vincular(Calificacion calificacion) {
        String sql = "INSERT INTO calificaciones(idUsuario, idCancion, " +
                "calificacion, comentario, fechaCalificacion, horaCalificacion) VALUES (?,?,?,?,?,?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, calificacion.getIdUsuario());
            ps.setInt(2, calificacion.getIdCancion());
            ps.setString(3, calificacion.getCalificacion());
            ps.setString(4, calificacion.getComentario());
            ps.setDate(5, java.sql.Date.valueOf(calificacion.getFechaCalificacion()));
            ps.setTime(6, java.sql.Time.valueOf(calificacion.getHoraCalificacion()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al vincular Cancion y usuario: " + e.getMessage());
            return false;
        }

    }

    public boolean desvincular(Calificacion calificacion) {
        String sql = "DELETE FROM calificaciones WHERE idCancion = ? AND idUsuario = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, calificacion.getIdCancion());
            ps.setInt(2, calificacion.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al desvincular Cancion y usuario: " + e.getMessage());
            return false;
        }
    }


    public boolean modificar(Calificacion calificacion) {
        String sql = "UPDATE calificaciones SET calificacion = ?, comentario = ?, fechaCalificacion = ?, horaCalificacion = ? " +
                "WHERE idUsuario = ? AND idCancion = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Parámetros de SET (datos nuevos)
            ps.setString(1, calificacion.getCalificacion());
            ps.setString(2, calificacion.getComentario());
            ps.setDate(3, java.sql.Date.valueOf(calificacion.getFechaCalificacion()));
            ps.setTime(4, java.sql.Time.valueOf(calificacion.getHoraCalificacion()));

            // Parámetros de WHERE (claves primarias compuestas)
            ps.setInt(5, calificacion.getIdUsuario());
            ps.setInt(6, calificacion.getIdCancion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al modificar la calificación: " + e.getMessage());
            return false;
        }
    }

    public Calificacion getCalificacion(Calificacion calificacion) {
        String sql = "SELECT * FROM calificaciones WHERE idCancion = ? AND idUsuario = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, calificacion.getIdUsuario());
            ps.setInt(2, calificacion.getIdCancion());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    calificacion.setIdUsuario(rs.getInt("idUsuario"));
                    calificacion.setIdCancion(rs.getInt("idCancion"));
                    calificacion.setCalificacion(rs.getString("calificacion"));
                    calificacion.setComentario(rs.getString("comentario"));
                    calificacion.setFechaCalificacion(rs.getObject("fechaCalificacion", LocalDate.class));
                    calificacion.setHoraCalificacion(rs.getObject("horaCalificacion", LocalTime.class));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener calificacion: " + e.getMessage());
        }

        return calificacion;

    }
}
