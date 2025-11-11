package com.softly.fonoteca.utilities;

import com.softly.fonoteca.Modelos.DTOs.ComboBoxItem;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class SQLQuerys {

    /**
     * Construye una consulta SQL parametrizada de forma genérica.
     * * @param type El tipo de operación (INSERT, UPDATE, DELETE, etc.).
     *
     * @param table                El nombre de la tabla (ej: "usuarios").
     * @param columns              Un arreglo de Strings con los nombres de las columnas a usar.
     * @param primaryKeyColumnName El nombre de la columna de la clave primaria (solo para UPDATE/DELETE/SELECT_BY_ID).
     * @return La cadena SQL resultante, lista para ser usada en un PreparedStatement.
     */
    public static String buildQuery(OperationType type, String table, String[] columns, String primaryKeyColumnName) {

        // 1. Manejo del DELETE, SELECT_ALL, y SELECT_BY_ID
        // Estos no dependen de la lista completa de columnas.
        switch (type) {
            case SELECT_ALL:
                return "SELECT * FROM " + table;
            case SELECT_BY_ID:
                if (primaryKeyColumnName == null || primaryKeyColumnName.isEmpty()) {
                    throw new IllegalArgumentException("SELECT_BY_ID requiere primaryKeyColumnName.");
                }
                return "SELECT * FROM " + table + " WHERE " + primaryKeyColumnName + "=?";
            case DELETE:
                if (primaryKeyColumnName == null || primaryKeyColumnName.isEmpty()) {
                    throw new IllegalArgumentException("DELETE requiere primaryKeyColumnName.");
                }
                return "DELETE FROM " + table + " WHERE " + primaryKeyColumnName + "=?";
        }

        // 2. Manejo de INSERT y UPDATE (Requieren la lista de columnas)
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException("INSERT y UPDATE requieren la lista de columnas.");
        }

        String columnNames = String.join(", ", columns);
        String placeholders = String.join(", ", Arrays.stream(columns).map(col -> "?").toArray(String[]::new));

        switch (type) {
            case INSERT:
                // Ejemplo: INSERT INTO usuarios (col1, col2) VALUES (?, ?)
                return "INSERT INTO " + table + " (" + columnNames + ") VALUES (" + placeholders + ")";

            case UPDATE:
                if (primaryKeyColumnName == null || primaryKeyColumnName.isEmpty()) {
                    throw new IllegalArgumentException("UPDATE requiere primaryKeyColumnName.");
                }
                // Ejemplo: UPDATE usuarios SET col1=?, col2=? WHERE id=?
                String setClauses = String.join(", ", Arrays.stream(columns).map(col -> col + "=?").toArray(String[]::new));
                return "UPDATE " + table + " SET " + setClauses + " WHERE " + primaryKeyColumnName + "=?";

            default:
                throw new UnsupportedOperationException("Tipo de operación no implementado: " + type);
        }
    }

    public static DefaultComboBoxModel<ComboBoxItem> consultarDatos(String tabla, String columnaID, String columnaDisplay) {
        String sql = "SELECT " + columnaID + ", " + columnaDisplay + " FROM " + tabla;

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                DefaultComboBoxModel<ComboBoxItem> modelo = new DefaultComboBoxModel<>();
                modelo.addElement(new ComboBoxItem(0, "--- Seleccionar ---"));

                // 3. AÑADIR LOS RESULTADOS DE LA BASE DE DATOS
                while (rs.next()) {
                    int id = rs.getInt(columnaID);
                    String display = rs.getString(columnaDisplay);

                    // Asegúrate de no agregar registros nulos o inválidos si fuera el caso
                    if (id > 0) {
                        modelo.addElement(new ComboBoxItem(id, display));
                    }
                }
                return modelo;
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar la base de datos: " + e.getMessage());
            return new DefaultComboBoxModel<>();
        }
    }

    /**
     * Enum para definir los tipos de operaciones soportadas.
     */
    public enum OperationType {
        INSERT,
        UPDATE,
        DELETE,
        SELECT_ALL,
        SELECT_BY_ID
    }

    // Dentro de la clase public class SQLQuerys { ... }

    /**
     * Busca y establece la selección en un JComboBox cuyo modelo es de tipo ComboBoxItem,
     * basándose en el ID numérico de la clave foránea.
     *
     * @param comboBox El JComboBox a modificar.
     * @param targetId El ID (clave foránea) que se debe buscar en los ComboBoxItem.
     */
    public static void setSelectedItemById(JComboBox<ComboBoxItem> comboBox, int targetId) {
        // Obtenemos el modelo genérico que contiene los ComboBoxItem
        DefaultComboBoxModel<ComboBoxItem> model = (DefaultComboBoxModel<ComboBoxItem>) comboBox.getModel();

        // Iteramos sobre todos los elementos del modelo
        for (int i = 0; i < model.getSize(); i++) {
            ComboBoxItem item = model.getElementAt(i);

            // Comparamos el ID del ítem con el ID objetivo (targetId)
            if (item.getId() == targetId) {
                // Si encontramos el ID, establecemos el índice y terminamos
                comboBox.setSelectedIndex(i);
                return;
            }
        }
        // Opcional: Si no se encuentra, puedes dejarlo en el primer índice ("--- Seleccionar ---" si el ID es 0)
        // comboBox.setSelectedIndex(0);
    }
}
