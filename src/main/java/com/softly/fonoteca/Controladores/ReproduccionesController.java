package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.ReproduccionDAO;
import com.softly.fonoteca.Modelos.DTOs.Reproduccion;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.ReproduccionesVista;
import com.softly.fonoteca.utilities.SQLQuerys;

import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 🎵 Controlador para la visualización detallada de las reproducciones registradas.
 * Se encarga de cargar los datos de la tabla 'reproducciones' en un JTable,
 * convirtiendo las claves foráneas (IDs) a sus respectivos nombres (títulos/usuarios)
 * para la visualización.
 */
public class ReproduccionesController {

    // --- DEPENDENCIAS Y MODELOS ---
    private final Reproduccion modelo;
    private final ReproduccionesVista vista;
    private final ReproduccionDAO consultas;
    private final BaseView vistaPrincipal;

    /**
     * Modelo de tabla con los datos crudos obtenidos directamente de la BD (contiene IDs).
     * Se mantiene para poder obtener los IDs al seleccionar una fila.
     */
    private DefaultTableModel rawModel;

    /**
     * Constructor del controlador de Reproducciones.
     *
     * @param modelo El DTO de Reproduccion (modelo de datos).
     * @param vista La vista de la tabla de reproducciones.
     * @param consultas La capa DAO para interactuar con la tabla de Reproducciones.
     * @param vistaPrincipal La vista padre a la que se debe regresar.
     */
    public ReproduccionesController(Reproduccion modelo, ReproduccionesVista vista, ReproduccionDAO consultas, BaseView vistaPrincipal) {
        this.modelo = modelo;
        this.vista = vista;
        this.consultas = consultas;
        this.vistaPrincipal = vistaPrincipal;

        cargarTablaReproducciones();
        agregarListeners();
    }

    // ------------------------------------------------------------------------------------------
    // --- LÓGICA DE CARGA Y TRANSFORMACIÓN DE DATOS ---
    // ------------------------------------------------------------------------------------------

    /**
     * Carga todos los registros de la tabla 'reproducciones', realiza la conversión
     * de IDs a nombres y asigna el modelo final al JTable de la vista.
     */
    private void cargarTablaReproducciones() {
        try {
            Map<String, String> columnMapping = new HashMap<>();

            // 1. OBTENER EL MODELO CRÍTICO CON LOS IDs ORIGINALES de la BD.
            this.rawModel = SQLQuerys.buildTableModel("reproducciones", columnMapping);

            // 2. CONVERTIR IDs A NOMBRES usando el rawModel para el modelo de la vista.
            DefaultTableModel finalModel = convertirIDsANombres(this.rawModel);

            // 3. Asignar el modelo final a la tabla.
            vista.tablaReproducciones.setModel(finalModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar la tabla de reproducciones: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ ERROR FATAL en cargarTablaReproducciones: " + e.getMessage());
        }
    }

    /**
     * Recorre el modelo de tabla (con IDs), consulta la BD para obtener los nombres
     * de usuarios y canciones, y genera un nuevo modelo listo para la vista.
     *
     * @param model El DefaultTableModel retornado por SQLQuerys.buildTableModel() (contiene IDs).
     * @return Un nuevo DefaultTableModel con los IDs reemplazados por nombres/títulos.
     */
    private DefaultTableModel convertirIDsANombres(DefaultTableModel model) {
        if (model.getRowCount() == 0) return model;

        // 1. Definición de los nuevos encabezados de columna para la vista
        String[] newColumnNames = {"Usuario", "Canción", "Fecha", "Hora", "Segundos"};

        // 2. Creación del nuevo modelo con los encabezados y cero filas iniciales (0)
        DefaultTableModel newModel = new DefaultTableModel(newColumnNames, 0);

        // 💡 NOTA: La línea anterior (new DefaultTableModel(newColumnNames, 0)) ya establece los encabezados.
        // Opcional: Si el constructor no fuera suficiente (o para mayor claridad), usaríamos:
        // newModel.setColumnIdentifiers(newColumnNames);

        // Mapeo de índices del rawModel: Necesario para asegurar la posición de la columna
        int col_idUsuario = model.findColumn("idUsuario");
        int col_idCancion = model.findColumn("idCancion");
        int col_fecha = model.findColumn("fechaReproduccion");

        // Manejar el Case-Sensitivity de la columna de hora
        int col_hora = model.findColumn("HoraReproduccion");
        if (col_hora == -1) col_hora = model.findColumn("horaReproduccion");

        int col_segundos = model.findColumn("segundosReproducidos");

        // --- Validación de Mapeo ---
        if (col_idUsuario == -1 || col_idCancion == -1 || col_fecha == -1 || col_hora == -1 || col_segundos == -1) {
            System.err.println("❌ ERROR: Una columna clave de BD no fue encontrada.");
            JOptionPane.showMessageDialog(vista, "ERROR: Fallo interno de mapeo de columnas.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
            return model;
        }

        // --- Iteración y Conversión ---
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                int idUsuario = (int) model.getValueAt(i, col_idUsuario);
                int idCancion = (int) model.getValueAt(i, col_idCancion);

                // Consulta a la BD para obtener el valor de visualización
                String nombreUsuario = SQLQuerys.getDisplayValueById("usuarios", "idUsuario", idUsuario, "nombres");
                String nombreCancion = SQLQuerys.getDisplayValueById("canciones", "idCancion", idCancion, "titulo");

                Object[] newRow = new Object[]{
                        nombreUsuario != null ? nombreUsuario : "ID Invalido",
                        nombreCancion != null ? nombreCancion : "ID Invalido",
                        model.getValueAt(i, col_fecha),
                        model.getValueAt(i, col_hora),
                        model.getValueAt(i, col_segundos)
                };

                newModel.addRow(newRow);
            } catch (ClassCastException e) {
                System.err.println("❌ ERROR: Fallo de conversión de tipos en la fila " + i + ". ¿Los IDs no son INT?");
            }
        }

        return newModel;
    }
    // ------------------------------------------------------------------------------------------
    // --- MANEJO DE EVENTOS Y VISTA ---
    // ------------------------------------------------------------------------------------------

    /**
     * Configura y añade todos los listeners necesarios a los componentes de la vista.
     */
    private void agregarListeners() {
        // Listener para la selección de fila en la tabla.
        vista.tablaReproducciones.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Se ejecuta solo una vez al finalizar el ajuste de la selección
                if (!e.getValueIsAdjusting()) {
                    cargarDetalleFilaSeleccionada();
                }
            }
        });

        // Listener para el botón de regresar.
        vista.regresarButton.addActionListener(e -> cerrarVista());
    }

    /**
     * Carga el detalle de la reproducción seleccionada en los campos de texto.
     * Es CRÍTICO que esta función use el 'rawModel' para buscar los IDs originales.
     */
    private void cargarDetalleFilaSeleccionada() {
        int selectedRow = vista.tablaReproducciones.getSelectedRow();

        // Se verifica que haya una fila seleccionada y que el modelo crudo exista.
        if (selectedRow != -1 && this.rawModel != null) {

            try {
                // Obtener los IDs y datos del modelo ORIGINAL (rawModel)
                // Se usa findColumn() para obtener el índice de forma segura, evitando errores por orden.
                int idUsuario = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("idUsuario"));
                int idCancion = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("idCancion"));

                String fecha = rawModel.getValueAt(selectedRow, rawModel.findColumn("fechaReproduccion")).toString();

                // Lógica de manejo de Case-Sensitivity de HoraReproduccion
                int col_hora_raw = rawModel.findColumn("HoraReproduccion");
                if (col_hora_raw == -1) col_hora_raw = rawModel.findColumn("horaReproduccion");
                String hora = rawModel.getValueAt(selectedRow, col_hora_raw).toString();

                int segundos = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("segundosReproducidos"));

                // Obtener los nombres ya convertidos del modelo de la vista (columna 0 y 1)
                String nombreUsuario = (String) vista.tablaReproducciones.getValueAt(selectedRow, 0);
                String nombreCancion = (String) vista.tablaReproducciones.getValueAt(selectedRow, 1);

                // Mostrar detalles en los componentes de la vista
                vista.txtNombreUsuario.setText(nombreUsuario != null ? nombreUsuario : "Usuario no encontrado");
                vista.txtNombreCancion.setText(nombreCancion != null ? nombreCancion : "Canción no encontrada");
                vista.txtFechaReproduccion.setText(STR."\{fecha}");
                vista.txtHoraReproduccion.setText(STR."\{hora}");
                vista.txtSegundosReproduccidos.setText(String.valueOf(segundos) + " seg");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(vista, "Error al cargar detalles de la fila: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("❌ ERROR en cargarDetalleFilaSeleccionada: " + e.getMessage());
            }
        }
    }

    /**
     * Oculta la vista actual y muestra la vista principal.
     */
    private void cerrarVista() {
        this.vista.setVisible(false);
        if (this.vistaPrincipal != null) {
            this.vistaPrincipal.setVisible(true);
        }
    }

    /**
     * Configura y hace visible la ventana de Reproducciones.
     */
    public void iniciar() {
        this.vista.pack();

        // Se recomienda usar DISPOSE_ON_CLOSE aquí, y manejar la lógica de
        // regreso con un WindowListener si se quiere abrir la vista principal al cerrar con 'X'.
        // Si no se usa WindowListener, el botón 'regresar' es el punto de control.
        this.vista.setDefaultCloseOperation(ReproduccionesVista.DISPOSE_ON_CLOSE);
        this.vista.setVisible(true);
    }
}