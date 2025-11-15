package com.softly.fonoteca.utilities;

import com.softly.fonoteca.Modelos.DTOs.ComboBoxItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

public class TablaUtils {

    /**
     * * @param tableName Nombre de la tabla de la BD.
     * @param displayColumns Array de nombres de columnas para el modelo final (vista).
     * @param table JTable de la vista donde se mostrarán los datos.
     * @param idMappings Mapa de mapeo de IDs (no se utiliza en la versión simplificada, pero se mantiene en la firma).
     * @return El DefaultTableModel crudo (con IDs).
     */
    public static DefaultTableModel cargarTablaGenerica(String tableName, String[] displayColumns, JTable table, Map<String, String> idMappings) {
        try {
            // 1. Obtener el modelo crudo con las columnas originales de la BD.
            DefaultTableModel rawModel = SQLQuerys.buildTableModel(tableName, new HashMap<>());

            if (rawModel.getRowCount() == 0) {
                // Si está vacío, establecer el modelo con las columnas de visualización.
                table.setModel(new DefaultTableModel(displayColumns, 0));
                return rawModel;
            }

            // Devolvemos el modelo crudo para que el controlador pueda usarlo en el mapeo de selección.
            return rawModel;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar la tabla genérica: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ Error en cargarTablaGenerica: " + e.getMessage());
            table.setModel(new DefaultTableModel(displayColumns, 0));
            return new DefaultTableModel(displayColumns, 0); // Devolver un modelo vacío
        }
    }

    /**
     * Método genérico para cargar los datos de la fila seleccionada en los componentes de la vista.
     * * @param selectedRow El índice de la fila seleccionada en el JTable.
     * @param rawModel El DefaultTableModel crudo (con IDs) obtenido de la BD.
     * @param componentMappings Mapa de mapeo: Clave=Nombre Columna BD (ej. "idUsuario"), Valor=Componente de la Vista (ej. JComboBox, JTextField).
     */
    public static void cargarDetalleGenerico(int selectedRow, DefaultTableModel rawModel, Map<String, Object> componentMappings) {
        if (selectedRow == -1 || rawModel == null) return;

        try {
            for (Map.Entry<String, Object> entry : componentMappings.entrySet()) {
                String columnName = entry.getKey();
                Object component = entry.getValue();

                int rawColIndex = rawModel.findColumn(columnName);
                if (rawColIndex == -1) {

                    System.err.println("Advertencia: Columna '" + columnName + "' no encontrada en el modelo crudo.");
                    continue;
                }

                Object rawValue = rawModel.getValueAt(selectedRow, rawColIndex);

                // Determinar el tipo de componente y cargar el valor
                if (component instanceof JTextField) {
                    ((JTextField) component).setText(rawValue != null ? rawValue.toString() : "");
                }else if(component instanceof JTextArea) {
                    ((JTextArea) component).setText(rawValue != null ? rawValue.toString() : "");
                } else if (component instanceof JComboBox) {
                    // Se requiere el cast para manejar el JComboBox con ComboBoxItem
                    @SuppressWarnings("unchecked")
                    JComboBox<ComboBoxItem> cmb = (JComboBox<ComboBoxItem>) component;
                    if (rawValue instanceof Integer) {
                        int id = (int) rawValue;
                        // Asegurar que SQLQuerys.setSelectedItemById exista y funcione
                        SQLQuerys.setSelectedItemById(cmb, id);
                    } else {
                        System.err.println("Error: El valor de la columna '" + columnName + "' no es un entero para JComboBox.");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ ERROR en cargarDetalleGenerico: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al cargar los detalles de la fila seleccionada: " + e.getMessage(), "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }
    }
}