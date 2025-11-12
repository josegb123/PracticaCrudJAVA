package com.softly.fonoteca.Modelos.DAOs;

import com.softly.fonoteca.Modelos.DTOs.Calificacion;
import com.softly.fonoteca.utilities.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public class CalificacionDAO {

    /**
     * Inserta una nueva calificación en la tabla.
     * @param calificacion DTO con los datos a insertar.
     * @return true si la inserción fue exitosa.
     */
    public boolean vincular(Calificacion calificacion) {
        // En una tabla de relación N:M, "vincular" es sinónimo de INSERT
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
            System.err.println("❌ Error al vincular Cancion y usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una calificación existente usando la clave compuesta (idUsuario, idCancion).
     * @param calificacion DTO con las claves a eliminar.
     * @return true si la eliminación fue exitosa.
     */
    public boolean desvincular(Calificacion calificacion) {
        String sql = "DELETE FROM calificaciones WHERE idCancion = ? AND idUsuario = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, calificacion.getIdCancion());
            ps.setInt(2, calificacion.getIdUsuario());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al desvincular Cancion y usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifica los detalles de una calificación existente.
     * @param calificacion DTO con los nuevos datos y las claves.
     * @return true si la modificación fue exitosa.
     */
    public boolean modificar(Calificacion calificacion) {
        String sql = "UPDATE calificaciones SET calificacion = ?, comentario = ?, fechaCalificacion = ?, horaCalificacion = ? " +
                "WHERE idUsuario = ? AND idCancion = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Parámetros de SET
            ps.setString(1, calificacion.getCalificacion());
            ps.setString(2, calificacion.getComentario());
            ps.setDate(3, java.sql.Date.valueOf(calificacion.getFechaCalificacion()));
            ps.setTime(4, java.sql.Time.valueOf(calificacion.getHoraCalificacion()));

            // Parámetros de WHERE
            ps.setInt(5, calificacion.getIdUsuario());
            ps.setInt(6, calificacion.getIdCancion());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al modificar la calificación: " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------------------------------
    // MÉTODOS AÑADIDOS
    // ------------------------------------------------------------------------------------------

    /**
     * Verifica si ya existe una calificación para el par Usuario-Canción.
     * @param idUsuario ID del usuario.
     * @param idCancion ID de la canción.
     * @return true si ya existe una calificación, false en caso contrario.
     */
    public boolean existeCalificacion(int idUsuario, int idCancion) {
        String sql = "SELECT COUNT(*) FROM calificaciones WHERE idUsuario = ? AND idCancion = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idCancion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Si el conteo es mayor a 0, existe.
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de calificación: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene una calificación específica para el par Usuario-Canción.
     * @param calificacion DTO que contiene las claves de búsqueda (idUsuario, idCancion).
     * @return El objeto Calificacion si se encuentra, o un objeto vacío (con las claves) si no se encuentra o hay error.
     */
    public Calificacion getCalificacion(Calificacion calificacion) {
        String sql = "SELECT * FROM calificaciones WHERE idUsuario = ? AND idCancion = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Nota: Se invirtieron las posiciones para coincidir con la query (idUsuario primero)
            ps.setInt(1, calificacion.getIdUsuario());
            ps.setInt(2, calificacion.getIdCancion());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { // Usamos if en lugar de while, solo esperamos un resultado
                    calificacion.setCalificacion(rs.getString("calificacion"));
                    calificacion.setComentario(rs.getString("comentario"));
                    calificacion.setFechaCalificacion(rs.getObject("fechaCalificacion", LocalDate.class));
                    calificacion.setHoraCalificacion(rs.getObject("horaCalificacion", LocalTime.class));
                    // Las claves idUsuario/idCancion ya están en el DTO que se pasó, no es necesario re-setearlas.
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener calificacion: " + e.getMessage());
        }

        return calificacion;
    }

    /**
     * Obtiene una lista de todas las calificaciones registradas en la base de datos.
     * @return Una lista de objetos Calificacion. Puede estar vacía si no hay registros o si ocurre un error.
     */
    public List<Calificacion> getCalificaciones() {
        List<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM calificaciones";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Calificacion calificacion = new Calificacion();

                calificacion.setIdUsuario(rs.getInt("idUsuario"));
                calificacion.setIdCancion(rs.getInt("idCancion"));
                calificacion.setCalificacion(rs.getString("calificacion"));
                calificacion.setComentario(rs.getString("comentario"));
                calificacion.setFechaCalificacion(rs.getObject("fechaCalificacion", LocalDate.class));
                calificacion.setHoraCalificacion(rs.getObject("horaCalificacion", LocalTime.class));

                lista.add(calificacion);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener todas las calificaciones: " + e.getMessage());
        }

        return lista;
    }
}