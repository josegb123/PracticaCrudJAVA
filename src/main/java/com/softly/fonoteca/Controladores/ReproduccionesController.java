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
 * Maneja la lógica de la vista y las operaciones CRUD.
 */
public class ReproduccionesController {

    private final Reproduccion modelo;
    private final ReproduccionesVista vista;
    private final ReproduccionDAO consultas;
    private final BaseView vistaPrincipal;
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

        // Listener de prueba eliminado, ya que no era parte de la lógica central
        // vista.txtFechaReproduccion.addDateChangeListener(dateChangeEvent -> { ... });
    }

    // --- LÓGICA DE CARGA Y TRANSFORMACIÓN DE DATOS ---

    private void cargarTablaReproducciones() {
        try {
            this.rawModel = SQLQuerys.buildTableModel("reproducciones", new HashMap<>());
            DefaultTableModel finalModel = convertirIDsANombres(this.rawModel);
            vista.tablaReproducciones.setModel(finalModel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, STR."Error al cargar la tabla de reproducciones: \{e.getMessage()}", "Error de BD", JOptionPane.ERROR_MESSAGE);
            System.err.println(STR."❌ ERROR FATAL en cargarTablaReproducciones: \{e.getMessage()}");
        }
    }

    private DefaultTableModel convertirIDsANombres(DefaultTableModel model) {
        if (model.getRowCount() == 0) return model;

        String[] newColumnNames = {"Usuario", "Canción", "Fecha", "Hora", "Segundos"};
        DefaultTableModel newModel = new DefaultTableModel(newColumnNames, 0);
        // ... (Lógica de mapeo de columnas y bucle sigue igual) ...
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

    // --- LÓGICA CRUD ---

    private void agregarReproduccion() {
        if (!validarCampos() || !mapearVistaAModelo()) return;
        // ... (Lógica CRUD sigue igual) ...
        if (consultas.vincular(modelo)) {
            JOptionPane.showMessageDialog(vista, "Reproducción guardada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaReproducciones();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al guardar la reproducción.", "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarReproduccion() {
        if (!validarCampos() || !mapearVistaAModelo()) return;
        // ... (Lógica CRUD sigue igual) ...
        if (consultas.modificar(modelo)) {
            JOptionPane.showMessageDialog(vista, "Reproducción modificada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaReproducciones();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al modificar la reproducción.", "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarReproduccion() {
        if (!mapearVistaAModelo()) return;
        // ... (Lógica CRUD sigue igual) ...
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
     * Valida la presencia de datos y los formatos numéricos.
     * La validación de formato de Fecha/Hora se puede simplificar confiando en la librería.
     */
    private boolean validarCampos() {
        try {
            // Validación de Segundos
            String segundosStr = vista.txtSegundosReproduccidos.getText().replaceAll("[^0-9]", "").trim();
            Integer.parseInt(segundosStr);

            // Validación crucial de la librería: Los campos deben tener un valor.
            if (vista.txtFechaReproduccion.getDate() == null) {
                JOptionPane.showMessageDialog(vista, "Debe seleccionar una Fecha válida.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (vista.txtHoraReproduccion.getTime() == null) {
                JOptionPane.showMessageDialog(vista, "Debe seleccionar una Hora válida.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Formato de Segundos inválido.", "Error de Formato", JOptionPane.WARNING_MESSAGE);
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
            // Obtener IDs de ComboBoxes (simplificación de NPE)
            int idUsuario = ((ComboBoxItem) Objects.requireNonNull(vista.cmbUsuarios.getSelectedItem(), "Usuario no seleccionado")).getId();
            int idCancion = ((ComboBoxItem) Objects.requireNonNull(vista.cmbCanciones.getSelectedItem(), "Canción no seleccionada")).getId();

            modelo.setIdUsuario(idUsuario);
            modelo.setIdCancion(idCancion);

            // Cargar datos, limpiando el formato de segundos
            String segundosStr = vista.txtSegundosReproduccidos.getText().replaceAll("[^0-9]", "").trim();
            int segundos = Integer.parseInt(segundosStr);

            // 🌟 SIMPLIFICACIÓN: Usar los métodos getDate() y getTime() directamente
            // La librería garantiza que si llegamos aquí y pasa validarCampos(), los valores no son null.
            modelo.setFechaReproduccion(vista.txtFechaReproduccion.getDate());
            modelo.setHoraReproduccion(vista.txtHoraReproduccion.getTime());
            modelo.setSegundosReproducidos(segundos);

            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, STR."Error al mapear datos: \{e.getMessage()}", "Error de Datos", JOptionPane.ERROR_MESSAGE);
            System.err.println(STR."❌ ERROR al mapear vista a modelo: \{e.getMessage()}");
            return false;
        }
    }


    // --- MANEJO DE EVENTOS Y VISTA ---

    private void agregarListeners() {
        // ... (Listeners CRUD siguen igual) ...
        vista.tablaReproducciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarDetalleFilaSeleccionada();
            }
        });

        vista.agregarButton.addActionListener(_ -> agregarReproduccion());
        vista.modificarButton.addActionListener(_ -> modificarReproduccion());
        vista.eliminarButton.addActionListener(_ -> eliminarReproduccion());
        vista.regresarButton.addActionListener(_ -> cerrarVista());
    }

    /**
     * Carga el detalle de la reproducción seleccionada en los campos de texto y Combos.
     */
    private void cargarDetalleFilaSeleccionada() {
        int selectedRow = vista.tablaReproducciones.getSelectedRow();

        if (selectedRow != -1 && this.rawModel != null) {
            try {
                int idUsuario = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("idUsuario"));
                int idCancion = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("idCancion"));

                modelo.setIdUsuario(idUsuario);
                modelo.setIdCancion(idCancion);

                String fechaStr = rawModel.getValueAt(selectedRow, rawModel.findColumn("fechaReproduccion")).toString();

                int col_hora_raw = rawModel.findColumn("HoraReproduccion");
                if (col_hora_raw == -1) col_hora_raw = rawModel.findColumn("horaReproduccion");
                String horaStr = rawModel.getValueAt(selectedRow, col_hora_raw).toString();

                int segundos = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("segundosReproducidos"));

                SQLQuerys.setSelectedItemById(vista.cmbUsuarios, idUsuario);
                SQLQuerys.setSelectedItemById(vista.cmbCanciones, idCancion);

                // 🌟 SIMPLIFICACIÓN: Usar los métodos setDate() y setTime() directamente en los componentes
                vista.txtFechaReproduccion.setDate(LocalDate.parse(fechaStr));
                vista.txtHoraReproduccion.setTime(LocalTime.parse(horaStr));

                vista.txtSegundosReproduccidos.setText(String.valueOf(segundos));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(vista, STR."Error al cargar detalles de la fila: \{e.getMessage()}", "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println(STR."❌ ERROR en cargarDetalleFilaSeleccionada: \{e.getMessage()}");
            }
        }
    }

    private void cerrarVista() {
        this.vista.setVisible(false);
        if (this.vistaPrincipal != null) {
            this.vistaPrincipal.setVisible(true);
        }
    }

    public void iniciar() {
        this.vista.pack();
        this.vista.setDefaultCloseOperation(ReproduccionesVista.EXIT_ON_CLOSE);
        this.vista.setVisible(true);
    }
}