package com.softly.fonoteca.Modelos.DAOs;

import com.softly.fonoteca.Modelos.DTOs.Reproduccion;
import com.softly.fonoteca.utilities.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReproduccionDAO extends BaseDAO<Reproduccion> {

    // -------------------------------------------------------------------------
    // --- MÉTODOS DE LÓGICA DE NEGOCIO (UPSERT) ---
    // -------------------------------------------------------------------------

    /**
     * Intenta insertar un registro de reproducción. Si el registro ya existe (código de error 1062, clave duplicada),
     * llama automáticamente a {@link #modificar(Reproduccion)} para actualizarlo.
     *
     * @param reproduccion DTO con los datos de la reproducción.
     * @return true si la operación (INSERT o UPDATE) fue exitosa.
     */
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
            // ERROR: 1062 - Clave primaria duplicada (se asume clave compuesta: idUsuario, idCancion)
            if (e.getErrorCode() == 1062) {
                System.out.println("DEBUG: El registro ya existe, intentando modificar...");
                return modificar(reproduccion); // Llama al método de actualización
            }
            System.err.println("❌ Error al vincular Cancion y usuario en reproducciones: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina el registro de la reproducción basándose en la clave compuesta (idUsuario y idCancion).
     *
     * @param reproduccion DTO que contiene las claves a eliminar.
     * @return true si se eliminó el registro.
     */
    public boolean desvincular(Reproduccion reproduccion) {
        String sql = "DELETE FROM reproducciones WHERE idCancion = ? AND idUsuario = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reproduccion.getIdCancion());
            ps.setInt(2, reproduccion.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al desvincular Cancion y usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza la fecha, hora y segundos de una reproducción existente.
     *
     * @param reproduccion DTO con los nuevos datos.
     * @return true si la actualización fue exitosa.
     */
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
            System.err.println("❌ Error al modificar la reproduccion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene una reproducción específica basándose en la clave compuesta (idUsuario y idCancion).
     *
     * @param reproduccion DTO que contiene las claves a buscar.
     * @return El DTO de Reproduccion rellenado si se encuentra, o null.
     */
    public Reproduccion getReproduccion(Reproduccion reproduccion) {
        String sql = "SELECT * FROM reproducciones WHERE idCancion = ? AND idUsuario = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reproduccion.getIdCancion());
            ps.setInt(2, reproduccion.getIdUsuario());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Mapeamos el resultado en el DTO recibido o en uno nuevo si lo prefieres
                    return mapFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener reproduccion: " + e.getMessage());
        }

        return null;
    }

    // -------------------------------------------------------------------------
    // --- IMPLEMENTACIÓN DE MÉTODOS ABSTRACTOS DE BASEDAO ---
    // -------------------------------------------------------------------------

    @Override
    protected String getTableName() {
        return "reproducciones";
    }

    /**
     * ⚠️ NO APLICABLE: Retorna vacío ya que esta tabla usa una clave compuesta,
     * y los métodos de BaseDAO están optimizados para claves primarias simples (INT).
     */
    @Override
    protected String getPrimaryKeyColumnName() {
        return "";
    }

    @Override
    protected String[] getAllColumns() {
        return new String[]{"idUsuario", "idCancion", "fechaReproduccion", "horaReproduccion", "segundosReproducidos"};
    }

    /**
     * ⚠️ NO APLICABLE: Retorna 0 ya que esta tabla usa una clave compuesta,
     * y no tiene una clave primaria INT simple.
     */
    @Override
    protected int getIdFromDto(Reproduccion dto) {
        return 0;
    }

    /**
     * ⚠️ NO APLICABLE: Este DAO usa métodos específicos (vincular, modificar)
     * para manejar la clave compuesta.
     */
    @Override
    protected void mapToStatement(PreparedStatement ps, Reproduccion dto) throws SQLException {
        // No se implementa, ya que las consultas (INSERT, UPDATE) usan lógica específica en este DAO.
    }

    /**
     * Mapea un ResultSet a un objeto Reproduccion.
     *
     * @param rs ResultSet con los datos de la fila.
     * @return Objeto Reproduccion mapeado.
     * @throws SQLException Si ocurre un error al leer el ResultSet.
     */
    @Override
    protected Reproduccion mapFromResultSet(ResultSet rs) throws SQLException {
        Reproduccion reproduccion = new Reproduccion();
        reproduccion.setIdUsuario(rs.getInt("idUsuario"));
        reproduccion.setIdCancion(rs.getInt("idCancion"));

        // Uso de rs.getObject(..., Clase.class) para manejar las conversiones de fecha/hora modernas
        reproduccion.setFechaReproduccion(rs.getObject("fechaReproduccion", LocalDate.class));
        reproduccion.setHoraReproduccion(rs.getObject("horaReproduccion", LocalTime.class));
        reproduccion.setSegundosReproducidos(rs.getInt("segundosReproducidos"));

        return reproduccion;
    }
}