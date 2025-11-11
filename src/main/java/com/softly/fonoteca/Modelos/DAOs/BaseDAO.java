package com.softly.fonoteca.Modelos.DAOs;

import com.softly.fonoteca.utilities.ConexionDB;
import com.softly.fonoteca.utilities.SQLQuerys;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Plantilla Base para todos los Data Access Objects (DAOs).
 * Proporciona métodos CRUD genéricos y maneja la conexión a la DB.
 *
 * @param <T> El DTO (Modelo) específico (ej: Cancion, Genero, Album).
 */
public abstract class BaseDAO<T> {

    /**
     * El nombre de la tabla en la base de datos (ej: "usuarios").
     */
    protected abstract String getTableName();

    /**
     * El nombre de la columna de la clave primaria (ej: "idUsuario").
     */
    protected abstract String getPrimaryKeyColumnName();

    /**
     * Un arreglo de Strings con TODAS las columnas usadas en INSERT/UPDATE.
     */
    protected abstract String[] getAllColumns();

    /**
     * Obtiene el ID del DTO (para UPDATE/DELETE).
     */
    protected abstract int getIdFromDto(T dto);


    // --- Métodos Abstractos (Deben ser implementados por la clase hija) ---

    /**
     * Define la consulta SQL para INSERTAR un nuevo registro.
     *
     * @return La cadena SQL.
     */
    protected String getSqlInsert() {
        return SQLQuerys.buildQuery(SQLQuerys.OperationType.INSERT, getTableName(), getAllColumns(), null);
    }

    /**
     * Define la consulta SQL para ACTUALIZAR un registro existente.
     *
     * @return La cadena SQL.
     */
    protected String getSqlUpdate() {
        return SQLQuerys.buildQuery(SQLQuerys.OperationType.UPDATE, getTableName(), getAllColumns(), getPrimaryKeyColumnName());
    }

    /**
     * Define la consulta SQL para ELIMINAR un registro por ID.
     *
     * @return La cadena SQL.
     */
    protected String getSqlDelete() {
        return SQLQuerys.buildQuery(SQLQuerys.OperationType.DELETE, getTableName(), null, getPrimaryKeyColumnName());
    }

    /**
     * Define la consulta SQL para BUSCAR un registro por ID o una lista.
     *
     * @return La cadena SQL.
     */
    protected String getSqlSelectAll() {
        return SQLQuerys.buildQuery(SQLQuerys.OperationType.SELECT_ALL, getTableName(), null, null);
    }

    /**
     * Define la consulta SQL para BUSCAR un registro por su clave primaria (ID).
     *
     * @return La cadena SQL.
     */
    protected String getSqlSelectById() {
        return SQLQuerys.buildQuery(SQLQuerys.OperationType.SELECT_BY_ID, getTableName(), null, getPrimaryKeyColumnName());
    }

    /**
     * Mapea los atributos del DTO a los parámetros del PreparedStatement (para INSERT/UPDATE).
     * El orden de los índices debe coincidir con los placeholders (?) en el SQL.
     *
     * @param ps  El PreparedStatement a configurar.
     * @param dto El objeto DTO con los datos.
     * @throws SQLException Si ocurre un error de SQL.
     */
    protected abstract void mapToStatement(PreparedStatement ps, T dto) throws SQLException;

    /**
     * Mapea los resultados de la consulta (ResultSet) a un objeto DTO.
     *
     * @param rs El ResultSet con los datos.
     * @return El objeto DTO llenado con los datos de la fila actual.
     * @throws SQLException Si ocurre un error de SQL.
     */
    protected abstract T mapFromResultSet(ResultSet rs) throws SQLException;

    // --- Lógica CRUD Base (Métodos Comunes) ---

    /**
     * Registra un nuevo DTO en la base de datos.
     */
    public boolean registrar(T dto) {
        String sql = getSqlInsert();
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            mapToStatement(ps, dto); // Mapeo definido por la clase hija

            // Si el DTO tiene un ID, se asume que es un INSERT que genera ID.
            // Si se usa un método auxiliar para asignar el ID, puedes manejarlo aquí.

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar " + dto.getClass().getSimpleName());
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Modifica un DTO existente en la base de datos.
     * Requiere que el DTO tenga el ID establecido (la clave primaria debe ser el último ? en getSqlUpdate).
     */
    public boolean modificar(T dto) {
        String sql = getSqlUpdate();
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // 1. Mapear los campos SET (Parámetros 1 a N)
            mapToStatement(ps, dto);

            // 2. Mapear el ID para la cláusula WHERE (Parámetro N + 1)
            int id = getIdFromDto(dto);
            int numSetParams = getAllColumns().length; // N (10 en el caso de Usuario)

            // El ID siempre es el último parámetro
            ps.setInt(numSetParams + 1, id);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar " + dto.getClass().getSimpleName());
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un registro por su ID.
     */
    public boolean eliminar(int id) {
        String sql = getSqlDelete();
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar el registro con ID: " + id);
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca un registro por su ID (clave primaria).
     */
    public T buscarPorId(int id) {
        String sql = getSqlSelectById();
        T dto = null;

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = mapFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar registro por ID: " + id);
            e.printStackTrace();
        }
        return dto;
    }

    /**
     * Obtiene todos los registros.
     */
    public List<T> obtenerTodos() {
        String sql = getSqlSelectAll();
        List<T> lista = new ArrayList<>();

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener todos los registros.");
            e.printStackTrace();
        }
        return lista;
    }
}