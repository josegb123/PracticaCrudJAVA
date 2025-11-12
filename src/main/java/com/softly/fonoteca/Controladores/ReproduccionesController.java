package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.ReproduccionDAO;
import com.softly.fonoteca.Modelos.DTOs.ComboBoxItem;
import com.softly.fonoteca.Modelos.DTOs.Reproduccion;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.ReproduccionesVista;
import com.softly.fonoteca.utilities.SQLQuerys;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Objects;

/**
 * Controlador para la visualización y gestión (CRUD) de las reproducciones registradas.
 * Se encarga de cargar los datos de la tabla, convertir las claves foráneas (IDs)
 * a sus respectivos nombres para la visualización, y manejar las operaciones de la vista.
 */
public class ReproduccionesController {

    private final Reproduccion modelo;
    private final ReproduccionesVista vista;
    private final ReproduccionDAO consultas;
    private final BaseView vistaPrincipal;

    /**
     * Modelo de tabla con los datos crudos (contiene IDs) obtenido directamente de la BD.
     * Es crucial para obtener los IDs al seleccionar una fila y ejecutar el CRUD.
     */
    private DefaultTableModel rawModel;

    /**
     * Constructor del controlador de Reproducciones.
     */
    public ReproduccionesController(Reproduccion modelo, ReproduccionesVista vista, ReproduccionDAO consultas, BaseView vistaPrincipal) {
        this.modelo = modelo;
        this.vista = vista;
        this.consultas = consultas;
        this.vistaPrincipal = vistaPrincipal;

        cargarTablaReproducciones();
        // Inicializar ComboBoxes con datos
        vista.cmbCanciones.setModel(SQLQuerys.consultarDatos("canciones", "idCancion", "titulo"));
        vista.cmbUsuarios.setModel(SQLQuerys.consultarDatos("usuarios", "idUsuario", "nombres"));

        agregarListeners();
    }

    // ------------------------------------------------------------------------------------------
    // LÓGICA DE CARGA Y TRANSFORMACIÓN DE DATOS
    // ------------------------------------------------------------------------------------------

    /**
     * Carga todos los registros de 'reproducciones', convierte IDs a nombres y asigna
     * el modelo final (sin IDs crudos) al JTable de la vista.
     */
    private void cargarTablaReproducciones() {
        try {
            // 1. Obtener el modelo crudo con los IDs originales de la BD.
            this.rawModel = SQLQuerys.buildTableModel("reproducciones", new HashMap<>());

            // 2. Convertir IDs a nombres para el modelo de la vista.
            DefaultTableModel finalModel = convertirIDsANombres(this.rawModel);

            // 3. Asignar el modelo final a la tabla.
            vista.tablaReproducciones.setModel(finalModel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, STR."Error al cargar la tabla de reproducciones: \{e.getMessage()}", "Error de BD", JOptionPane.ERROR_MESSAGE);
            System.err.println(STR."❌ ERROR FATAL en cargarTablaReproducciones: \{e.getMessage()}");
        }
    }

    /**
     * Recorre el modelo de tabla (con IDs), consulta la BD para obtener los nombres
     * de usuarios y canciones, y genera un nuevo modelo listo para la vista.
     *
     * @param model El DefaultTableModel que contiene los IDs de la BD.
     * @return Un nuevo DefaultTableModel con los IDs reemplazados por nombres/títulos.
     */
    private DefaultTableModel convertirIDsANombres(DefaultTableModel model) {
        if (model.getRowCount() == 0) return model;

        String[] newColumnNames = {"Usuario", "Canción", "Fecha", "Hora", "Segundos"};
        DefaultTableModel newModel = new DefaultTableModel(newColumnNames, 0);

        int col_idUsuario = model.findColumn("idUsuario");
        int col_idCancion = model.findColumn("idCancion");
        int col_fecha = model.findColumn("fechaReproduccion");
        int col_hora = model.findColumn("HoraReproduccion") != -1 ? model.findColumn("HoraReproduccion") : model.findColumn("horaReproduccion");
        int col_segundos = model.findColumn("segundosReproducidos");

        if (col_idUsuario == -1 || col_idCancion == -1 || col_fecha == -1 || col_hora == -1 || col_segundos == -1) {
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
                        model.getValueAt(i, col_fecha),
                        model.getValueAt(i, col_hora),
                        model.getValueAt(i, col_segundos)
                };

                newModel.addRow(newRow);
            } catch (ClassCastException e) {
                System.err.println(STR."❌ ERROR: Fallo de conversión de tipos en la fila \{i}. Asegúrese que los IDs son INT.");
            }
        }

        return newModel;
    }

    // ------------------------------------------------------------------------------------------
    // LÓGICA CRUD
    // ------------------------------------------------------------------------------------------

    /**
     * Guarda una nueva reproducción usando la lógica de UPSERT (insertar o modificar).
     */
    private void agregarReproduccion() {
        if (!validarCampos() || !mapearVistaAModelo()) return;

        if (consultas.vincular(modelo)) {
            JOptionPane.showMessageDialog(vista, "Reproducción guardada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaReproducciones();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al guardar la reproducción.", "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Modifica el registro de la reproducción actualmente seleccionado/mostrado.
     */
    private void modificarReproduccion() {
        if (!validarCampos() || !mapearVistaAModelo()) return;

        if (consultas.modificar(modelo)) {
            JOptionPane.showMessageDialog(vista, "Reproducción modificada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaReproducciones();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al modificar la reproducción.", "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina el registro de la reproducción actualmente seleccionado.
     */
    private void eliminarReproduccion() {
        if (!mapearVistaAModelo()) return;

        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro de eliminar la reproducción?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (consultas.desvincular(modelo)) {
                JOptionPane.showMessageDialog(vista, "Reproducción eliminada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTablaReproducciones();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar la reproducción.", "Error de BD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Valida que los campos requeridos tengan datos y estén en el formato correcto.
     *
     * @return true si la validación es exitosa.
     */
    private boolean validarCampos() {
        try {
            // Limpiar y parsear Segundos
            String segundosStr = vista.txtSegundosReproduccidos.getText().replaceAll("[^0-9]", "").trim();
            Integer.parseInt(segundosStr);

            // Validar formato de fecha y hora
            LocalDate.parse(vista.txtFechaReproduccion.getText());
            LocalTime.parse(vista.txtHoraReproduccion.getText());

            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Formato de Segundos inválido.", "Error de Formato", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(vista, "Formato de Fecha/Hora inválido (debe ser YYYY-MM-DD y HH:MM:SS).", "Error de Formato", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    /**
     * Mapea los datos de los componentes de la vista al DTO del modelo (this.modelo).
     *
     * @return true si el mapeo fue exitoso.
     */
    private boolean mapearVistaAModelo() {
        try {

            // Obtener IDs de ComboBoxes
            int idUsuario = ((ComboBoxItem) Objects.requireNonNull(vista.cmbUsuarios.getSelectedItem())).getId();
            int idCancion = ((ComboBoxItem) Objects.requireNonNull(vista.cmbCanciones.getSelectedItem())).getId();

            modelo.setIdUsuario(idUsuario);
            modelo.setIdCancion(idCancion);

            // Cargar datos, limpiando el formato de segundos
            String segundosStr = vista.txtSegundosReproduccidos.getText().replaceAll("[^0-9]", "").trim();
            int segundos = Integer.parseInt(segundosStr);

            modelo.setFechaReproduccion(LocalDate.parse(vista.txtFechaReproduccion.getText()));
            modelo.setHoraReproduccion(LocalTime.parse(vista.txtHoraReproduccion.getText()));
            modelo.setSegundosReproducidos(segundos);

            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Faltan datos de ID o el formato de fecha/hora/segundos es incorrecto.", "Error de Datos", JOptionPane.ERROR_MESSAGE);
            System.err.println(STR."❌ ERROR al mapear vista a modelo: \{e.getMessage()}");
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
        vista.tablaReproducciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarDetalleFilaSeleccionada();
            }
        });

        // Listeners de botones CRUD
        vista.agregarButton.addActionListener(_ -> agregarReproduccion());
        vista.modificarButton.addActionListener(_ -> modificarReproduccion());
        vista.eliminarButton.addActionListener(_ -> eliminarReproduccion());

        // Listener para el botón de regresar.
        vista.regresarButton.addActionListener(_ -> cerrarVista());
    }

    /**
     * Carga el detalle de la reproducción seleccionada en los campos de texto y Combos.
     * Es crucial cargar los IDs originales en el modelo DTO (this.modelo) para el CRUD.
     */
    private void cargarDetalleFilaSeleccionada() {
        int selectedRow = vista.tablaReproducciones.getSelectedRow();

        if (selectedRow != -1 && this.rawModel != null) {
            try {
                // Obtener los IDs y datos del modelo ORIGINAL (rawModel)
                int idUsuario = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("idUsuario"));
                int idCancion = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("idCancion"));

                // Cargar los IDs al DTO del controlador.
                modelo.setIdUsuario(idUsuario);
                modelo.setIdCancion(idCancion);

                String fecha = rawModel.getValueAt(selectedRow, rawModel.findColumn("fechaReproduccion")).toString();

                int col_hora_raw = rawModel.findColumn("HoraReproduccion");
                if (col_hora_raw == -1) col_hora_raw = rawModel.findColumn("horaReproduccion");
                String hora = rawModel.getValueAt(selectedRow, col_hora_raw).toString();

                int segundos = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("segundosReproducidos"));

                // Asignar al modelo y mostrar detalles en los componentes de la vista
                SQLQuerys.setSelectedItemById(vista.cmbUsuarios, idUsuario);
                SQLQuerys.setSelectedItemById(vista.cmbCanciones, idCancion);
                vista.txtFechaReproduccion.setText(fecha);
                vista.txtHoraReproduccion.setText(hora);
                vista.txtSegundosReproduccidos.setText(String.valueOf(segundos));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(vista, STR."Error al cargar detalles de la fila: \{e.getMessage()}", "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println(STR."❌ ERROR en cargarDetalleFilaSeleccionada: \{e.getMessage()}");
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
        this.vista.setDefaultCloseOperation(ReproduccionesVista.EXIT_ON_CLOSE);
        this.vista.setVisible(true);
    }
}