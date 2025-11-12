package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.CalificacionDAO;
import com.softly.fonoteca.Modelos.DTOs.ComboBoxItem;
import com.softly.fonoteca.Modelos.DTOs.Calificacion;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.CalificacionesVista;
import com.softly.fonoteca.utilities.SQLQuerys;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

/**
 * Controlador para la visualización y gestión (CRUD) de las calificaciones registradas.
 * Maneja la conversión de IDs a nombres para la visualización y las operaciones CRUD.
 */
public class CalificacionesController {

    private final Calificacion modelo;
    private final CalificacionesVista vista;
    private final CalificacionDAO consultas;
    private final BaseView vistaPrincipal;

    /** Modelo de tabla con los datos crudos (contiene IDs) de la BD. */
    private DefaultTableModel rawModel;

    /**
     * Constructor del controlador de Calificaciones.
     */
    public CalificacionesController(Calificacion modelo, CalificacionesVista vista, CalificacionDAO consultas, BaseView vistaPrincipal) {
        this.modelo = modelo;
        this.vista = vista;
        this.consultas = consultas;
        this.vistaPrincipal = vistaPrincipal;

        cargarTablaCalificaciones();

        // Inicializar ComboBoxes con datos (asumiendo nombres de componentes en la vista)
        vista.cmbCanciones.setModel(SQLQuerys.consultarDatos("canciones", "idCancion", "titulo"));
        vista.cmbUsuarios.setModel(SQLQuerys.consultarDatos("usuarios", "idUsuario", "nombres"));

        agregarListeners();
    }

    // ------------------------------------------------------------------------------------------
    // LÓGICA DE CARGA Y TRANSFORMACIÓN DE DATOS
    // ------------------------------------------------------------------------------------------

    /**
     * Carga todos los registros de 'calificaciones', convierte IDs a nombres y asigna
     * el modelo final al JTable de la vista.
     */
    private void cargarTablaCalificaciones() {
        try {
            // 1. Obtener el modelo crudo con los IDs originales de la BD.
            // Nota: El HashMap vacío indica que no se requiere mapeo de nombres de columna aquí.
            this.rawModel = SQLQuerys.buildTableModel("calificaciones", new HashMap<>());

            // 2. Convertir IDs a nombres para el modelo de la vista.
            DefaultTableModel finalModel = convertirIDsANombres(this.rawModel);

            // 3. Asignar el modelo final a la tabla.
            vista.tablaCalificaciones.setModel(finalModel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar la tabla de calificaciones: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ ERROR FATAL en cargarTablaCalificaciones: " + e.getMessage());
        }
    }

    /**
     * Convierte los IDs de Usuario y Canción en nombres/títulos legibles para la vista.
     */
    private DefaultTableModel convertirIDsANombres(DefaultTableModel model) {
        if (model.getRowCount() == 0) return model;

        String[] newColumnNames = {"Usuario", "Canción", "Calificación", "Comentario", "Fecha", "Hora"};
        DefaultTableModel newModel = new DefaultTableModel(newColumnNames, 0);

        int col_idUsuario = model.findColumn("idUsuario");
        int col_idCancion = model.findColumn("idCancion");
        int col_calificacion = model.findColumn("calificacion");
        int col_comentario = model.findColumn("comentario");
        int col_fecha = model.findColumn("fechaCalificacion");
        int col_hora = model.findColumn("horaCalificacion");

        if (col_idUsuario == -1 || col_idCancion == -1 || col_calificacion == -1 || col_comentario == -1 || col_fecha == -1 || col_hora == -1) {
            System.err.println("❌ ERROR: Una columna clave de BD no fue encontrada para mapeo.");
            JOptionPane.showMessageDialog(vista, "ERROR: Fallo interno de mapeo de columnas.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
            return model;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                int idUsuario = (int) model.getValueAt(i, col_idUsuario);
                int idCancion = (int) model.getValueAt(i, col_idCancion);

                String nombreUsuario = SQLQuerys.getDisplayValueById("usuarios", "idUsuario", idUsuario, "nombres");
                String nombreCancion = SQLQuerys.getDisplayValueById("canciones", "idCancion", idCancion, "titulo");

                Object[] newRow = new Object[]{
                        nombreUsuario != null ? nombreUsuario : "ID Invalido",
                        nombreCancion != null ? nombreCancion : "ID Invalido",
                        model.getValueAt(i, col_calificacion),
                        model.getValueAt(i, col_comentario),
                        model.getValueAt(i, col_fecha),
                        model.getValueAt(i, col_hora)
                };

                newModel.addRow(newRow);
            } catch (ClassCastException e) {
                System.err.println("❌ ERROR: Fallo de conversión de tipos en la fila " + i + ". IDs no son INT?");
            }
        }
        return newModel;
    }

    // ------------------------------------------------------------------------------------------
    // LÓGICA CRUD
    // ------------------------------------------------------------------------------------------

    /**
     * Intenta agregar una nueva calificación. Si ya existe, lanza un mensaje de error
     * e invita a usar Modificar.
     */
    private void agregarCalificacion() {
        if (!validarCampos() || !mapearVistaAModelo()) return;

        if (consultas.existeCalificacion(modelo.getIdUsuario(), modelo.getIdCancion())) {
            JOptionPane.showMessageDialog(vista, "Esta canción ya fue calificada por este usuario. Use Modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (consultas.vincular(modelo)) {
            JOptionPane.showMessageDialog(vista, "Calificación agregada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaCalificaciones();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al guardar la calificación.", "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Modifica el registro de la calificación actualmente seleccionado.
     */
    private void modificarCalificacion() {
        if (!validarCampos() || !mapearVistaAModelo()) return;

        // Si no existe, no se puede modificar, debe usarse Agregar
        if (!consultas.existeCalificacion(modelo.getIdUsuario(), modelo.getIdCancion())) {
            JOptionPane.showMessageDialog(vista, "No existe una calificación previa para modificar. Use Agregar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (consultas.modificar(modelo)) {
            JOptionPane.showMessageDialog(vista, "Calificación modificada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaCalificaciones();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al modificar la calificación.", "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina el registro de la calificación actualmente seleccionado.
     */
    private void eliminarCalificacion() {
        if (!mapearVistaAModelo()) return;

        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro de eliminar esta calificación?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (consultas.desvincular(modelo)) {
                JOptionPane.showMessageDialog(vista, "Calificación eliminada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTablaCalificaciones();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar la calificación.", "Error de BD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Valida que los campos requeridos tengan datos y estén en el formato correcto.
     */
    private boolean validarCampos() {
        try {
            // Validación de Calificación (asumiendo que es un número o un ComboBox)
            // Si es un campo de texto simple:
            // Integer.parseInt(vista.txtCalificacion.getText().trim());

            // Validación de Fecha y Hora
            LocalDate.parse(vista.txtFechaCalificacion.getText());
            LocalTime.parse(vista.txtHoraCalificacion.getText());

            // Validación de campos vacíos
            if (vista.txtCalificacion.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("La calificación no puede estar vacía.");
            }
            // Comentario puede ser opcional, por lo que no lo validamos como requerido.

            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Formato de Calificación inválido.", "Error de Formato", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(vista, "Formato de Fecha/Hora inválido (debe ser YYYY-MM-DD y HH:MM:SS).", "Error de Formato", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Validación Requerida", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    /**
     * Mapea los datos de los componentes de la vista al DTO del modelo (this.modelo).
     */
    private boolean mapearVistaAModelo() {
        try {
            // Uso de la lógica segura vista anteriormente
            Object itemUsuario = vista.cmbUsuarios.getSelectedItem();
            Object itemCancion = vista.cmbCanciones.getSelectedItem();

            if (!(itemUsuario instanceof ComboBoxItem) || !(itemCancion instanceof ComboBoxItem)) {
                throw new Exception("Debe seleccionar un Usuario y una Canción válidos.");
            }

            int idUsuario = ((ComboBoxItem) itemUsuario).getId();
            int idCancion = ((ComboBoxItem) itemCancion).getId();

            if (idUsuario <= 0 || idCancion <= 0) {
                throw new Exception("Debe seleccionar un elemento válido (ID > 0).");
            }

            // Carga de IDs
            modelo.setIdUsuario(idUsuario);
            modelo.setIdCancion(idCancion);

            // Carga de Datos
            modelo.setCalificacion(vista.txtCalificacion.getText().trim());
            modelo.setComentario(vista.txtComentarios.getText());
            modelo.setFechaCalificacion(LocalDate.parse(vista.txtFechaCalificacion.getText()));
            modelo.setHoraCalificacion(LocalTime.parse(vista.txtHoraCalificacion.getText()));

            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Faltan datos clave de ID o la selección es incorrecta.", "Error de Datos", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ ERROR al mapear vista a modelo: " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------------------------------
    // MANEJO DE EVENTOS Y VISTA
    // ------------------------------------------------------------------------------------------

    /**
     * Configura y añade todos los listeners necesarios a los componentes de la vista.
     */
    private void agregarListeners() {
        // Listener para la selección de fila en la tabla.
        vista.tablaCalificaciones.getSelectionModel().addListSelectionListener(this::handleTableSelection);

        // Listeners de botones CRUD
        vista.agregarButton.addActionListener(e -> agregarCalificacion());
        vista.modificarButton.addActionListener(e -> modificarCalificacion());
        vista.eliminarButton.addActionListener(e -> eliminarCalificacion());

        // Listener para el botón de regresar.
        vista.regresarButton.addActionListener(e -> cerrarVista());
    }

    /**
     * Maneja el evento de selección de fila de la tabla.
     */
    private void handleTableSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            cargarDetalleFilaSeleccionada();
        }
    }

    /**
     * Carga el detalle de la calificación seleccionada en los campos de la vista.
     */
    private void cargarDetalleFilaSeleccionada() {
        int selectedRow = vista.tablaCalificaciones.getSelectedRow();

        if (selectedRow != -1 && this.rawModel != null) {
            try {
                // Obtener los IDs y datos del modelo ORIGINAL (rawModel)
                int idUsuario = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("idUsuario"));
                int idCancion = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("idCancion"));

                // Cargar los IDs al DTO del controlador para el CRUD
                modelo.setIdUsuario(idUsuario);
                modelo.setIdCancion(idCancion);

                String calificacionStr = rawModel.getValueAt(selectedRow, rawModel.findColumn("calificacion")).toString();
                String comentario = rawModel.getValueAt(selectedRow, rawModel.findColumn("comentario")).toString();
                String fecha = rawModel.getValueAt(selectedRow, rawModel.findColumn("fechaCalificacion")).toString();
                String hora = rawModel.getValueAt(selectedRow, rawModel.findColumn("horaCalificacion")).toString();

                // Mostrar detalles en los componentes de la vista
                SQLQuerys.setSelectedItemById(vista.cmbUsuarios, idUsuario);
                SQLQuerys.setSelectedItemById(vista.cmbCanciones, idCancion);
                vista.txtCalificacion.setText(calificacionStr);
                vista.txtComentarios.setText(comentario);
                vista.txtFechaCalificacion.setText(fecha);
                vista.txtHoraCalificacion.setText(hora);

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
     * Configura y hace visible la ventana de Calificaciones.
     */
    public void iniciar() {
        this.vista.pack();
        this.vista.setDefaultCloseOperation(CalificacionesVista.EXIT_ON_CLOSE);
        this.vista.setVisible(true);
    }
}