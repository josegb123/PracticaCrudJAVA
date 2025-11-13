package com.softly.fonoteca.utilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections; // 🌟 Nuevo: Para ordenar
import java.util.Comparator; // 🌟 Nuevo: Para ordenar
import java.util.Enumeration;
import java.util.Vector;

public class UIShowColors extends JFrame {

    private JTable colorTable; // Hacemos la tabla accesible para el listener

    public UIShowColors() {
        setTitle("Colores del UIManager (L&F Actual: " + UIManager.getLookAndFeel().getName() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Obtener los datos (Keys y Colores)
        Vector<Vector<Object>> data = getColorData();
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Clave (Key)");
        columnNames.add("Muestra de Color");

        // 2. Crear un modelo de tabla no editable
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 3. Crear la tabla y el renderizador
        colorTable = new JTable(model);

        colorTable.getColumnModel().getColumn(1).setCellRenderer(new ColorRenderer());
        colorTable.getColumnModel().getColumn(1).setMaxWidth(80);
        colorTable.setRowHeight(20);

        // 4. Añadir el listener para el menú contextual (clic derecho)
        addCopyPopupMenu();

        // 5. Configurar la ventana
        add(new JScrollPane(colorTable));
        setSize(500, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- Lógica de Obtención de Datos ---

    /**
     * Obtiene todas las keys de color del UIManager y las ordena alfabéticamente.
     */
    private Vector<Vector<Object>> getColorData() {
        UIDefaults defaults = UIManager.getDefaults();
        Enumeration<Object> keys = defaults.keys();
        Vector<Vector<Object>> data = new Vector<>();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = defaults.get(key);

            if (value instanceof Color) {
                Vector<Object> row = new Vector<>();
                row.add(key.toString());
                row.add(value);
                data.add(row);
            }
        }

        // 🌟 ORDENAR ALFABÉTICAMENTE: Ordenamos por la primera columna (la Key)
        Collections.sort(data, new Comparator<Vector<Object>>() {
            @Override
            public int compare(Vector<Object> row1, Vector<Object> row2) {
                // Compara la clave (Object en el índice 0)
                return ((String) row1.get(0)).compareTo((String) row2.get(0));
            }
        });

        return data;
    }

    // --- Lógica de Copiar con Clic Derecho ---

    private void addCopyPopupMenu() {
        // 1. Crear el menú contextual (JPopupMenu)
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyItem = new JMenuItem("Copiar Clave");
        popupMenu.add(copyItem);

        // 2. Añadir Listener al item de copiar
        copyItem.addActionListener(e -> {
            int selectedRow = colorTable.getSelectedRow();
            if (selectedRow != -1) {
                // Obtener el valor de la Clave (columna 0) de la fila seleccionada
                Object keyToCopy = colorTable.getValueAt(selectedRow, 0);
                copyToClipboard(keyToCopy.toString());
            }
        });

        // 3. Añadir MouseListener a la tabla para mostrar el menú
        colorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                // Verificar si es un evento de clic derecho (trigger para el menú contextual)
                if (e.isPopupTrigger()) {
                    Point p = e.getPoint();
                    int row = colorTable.rowAtPoint(p);

                    // Seleccionar la fila donde se hizo clic (necesario para copiar el valor correcto)
                    if (row != -1) {
                        colorTable.setRowSelectionInterval(row, row);
                        popupMenu.show(colorTable, p.x, p.y);
                    }
                }
            }
        });
    }

    /**
     * Copia la cadena proporcionada al portapapeles del sistema.
     */
    private void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

    // --- Renderer ---

    /**
     * Renderer de tabla personalizado para dibujar un cuadrado de color.
     */
    private class ColorRenderer extends JLabel implements TableCellRenderer {

        public ColorRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Color) {
                this.setBackground((Color) value);
                this.setText("");
            } else {
                this.setBackground(table.getBackground());
                this.setText("N/A");
            }

            if (isSelected) {
                this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            } else {
                this.setBorder(null);
            }

            return this;
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(UIShowColors::new);
//    }
}