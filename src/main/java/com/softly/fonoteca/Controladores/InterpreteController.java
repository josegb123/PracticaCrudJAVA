package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.InterpreteDAO;
import com.softly.fonoteca.Modelos.DTOs.ComboBoxItem;
import com.softly.fonoteca.Modelos.DTOs.Interprete;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.InterpretesVista;
import com.softly.fonoteca.utilities.FormatDates;
import com.softly.fonoteca.utilities.SQLQuerys;

import javax.swing.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InterpreteController extends BaseController<Interprete, InterpretesVista, InterpreteDAO> {

    // 🌟 1. DEFINICIÓN DE COLUMNAS A MOSTRAR 🌟
    // Columnas de la BD que queremos obtener
    private static final String[] DB_COLUMNS_TO_SHOW =
            {"nombre", "tituloInterprete", "yearLanzamiento"};

    private static final String[] DISPLAY_COLUMNS_HEADERS =
            {"Nombre", "Título de interprete", "Año de lanzamiento"};


    public InterpreteController(Interprete modelo, InterpretesVista vista, InterpreteDAO consultas, BaseView vistaPrincipal) {
        super(modelo, vista, consultas, vistaPrincipal);

        // 2. Inicializar la tabla principal
        this.mainTable = vista.tablaInterpretes;

        // Cargar datos al inicio
        cargarTablaInterpretes();

        vista.cmbGenero.setModel(SQLQuerys.consultarDatos("generos", "idGenero", "nombre"));
        // Agregar listeners, incluyendo el de la tabla
        agregarListeners();
    }

    /**
     * Usa la implementación genérica del BaseController para cargar y filtrar la tabla.
     */
    private void cargarTablaInterpretes() {
        cargarTabla("interpretes", DB_COLUMNS_TO_SHOW, DISPLAY_COLUMNS_HEADERS);
    }


    @Override
    protected int getModelId() {
        // Obtenemos el ID del modelo cargado al seleccionar la fila
        return modelo.getIdInterprete();
    }

    @Override
    protected boolean collectDataFromView() {
        try {
            LocalDate fechaLanzamiento = FormatDates.getFormatDate(vista.txtYearLanzamiento.getText());
            LocalDate fechaRetiro = FormatDates.getFormatDate(vista.txtYearRetiro.getText());

            // Si es una modificación, el ID ya está en el modelo. Si es registro, el ID es 0.
            if (!vista.txtID.getText().isEmpty()) {
                modelo.setIdInterprete(Integer.parseInt(vista.txtID.getText()));
            } else {
                modelo.setIdInterprete(0);
            }

            modelo.setNombre(vista.txtNombre.getText()); // Corregido: usa txtNombre para nombre
            modelo.setYearLanzamiento(fechaLanzamiento);
            modelo.setYearRetiro(fechaRetiro);
            modelo.setTituloInterprete(vista.txtTitulo.getText());

            ComboBoxItem generoSeleccionado = (ComboBoxItem) vista.cmbGenero.getSelectedItem();
            modelo.setIdGeneroPrincipal(Objects.requireNonNull(generoSeleccionado).getId());

            return true;
        } catch (Exception e) {
            System.err.println(STR."Error al recolectar datos: \{e.getMessage()}");
            JOptionPane.showMessageDialog(vista, STR."Error interno de formato de datos: \{e.getMessage()}", "Error Crítico", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    protected void loadDataToView(Interprete interpreteEncontrado) {
        // Este método se usa si buscarRegistroPorId(id) fuera invocado, pero ahora usamos el listener de tabla.

        this.modelo.setIdInterprete(interpreteEncontrado.getIdInterprete());
        this.modelo.setTituloInterprete(interpreteEncontrado.getTituloInterprete());
        this.modelo.setYearLanzamiento(interpreteEncontrado.getYearLanzamiento());
        this.modelo.setYearRetiro(interpreteEncontrado.getYearRetiro());
        this.modelo.setIdGeneroPrincipal(interpreteEncontrado.getIdGeneroPrincipal());
        this.modelo.setNombre(interpreteEncontrado.getNombre());

        vista.txtTitulo.setText(interpreteEncontrado.getTituloInterprete());
        vista.txtNombre.setText(interpreteEncontrado.getNombre());
        vista.txtID.setText(String.valueOf(interpreteEncontrado.getIdInterprete()));
        vista.txtYearLanzamiento.setText(interpreteEncontrado.getYearLanzamiento().toString());
        vista.txtYearRetiro.setText(interpreteEncontrado.getYearRetiro().toString());

        SQLQuerys.setSelectedItemById(vista.cmbGenero, interpreteEncontrado.getIdGeneroPrincipal());
    }

    @Override
    protected void clearViewFields() {
        modelo.setIdInterprete(0); // Limpiar el ID del modelo
        vista.txtID.setText("");
        vista.txtNombre.setText("");
        vista.txtTitulo.setText("");
        vista.txtYearLanzamiento.setText("");
        vista.txtYearRetiro.setText("");
        vista.cmbGenero.setSelectedIndex(0);
        vista.txtSearch.setText("");
        vista.tablaInterpretes.clearSelection();
    }

    /**
     * Implementa la lógica de selección de fila usando el método genérico del BaseController.
     */
    private void cargarDetalleFilaSeleccionada(javax.swing.event.ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && vista.tablaInterpretes.getSelectedRow() != -1 && this.rawModel != null) {

            int selectedRow = vista.tablaInterpretes.getSelectedRow();

            // 1. Mapeo de componentes simples (para loadTableDetailsToView)
            Map<String, Object> componentMappings = new HashMap<>();

            componentMappings.put("nombre", vista.txtNombre);
            componentMappings.put("tituloInterprete", vista.txtTitulo);
            componentMappings.put("yearLanzamiento", vista.txtYearLanzamiento);
            componentMappings.put("yearRetiro", vista.txtYearRetiro);

            // Mapeamos el JComboBox con el ID foráneo del rawModel
            componentMappings.put("idGeneroPrincipal", vista.cmbGenero);

            // Usamos el método genérico para cargar los campos simples y el ComboBox
            loadTableDetailsToView(e, componentMappings);

            try {
                // 2. Manejo de campos especiales (ID y Fechas NULAS)

                // Obtener ID (necesario para CRUD y mostrar en txtID)
                int col_id = rawModel.findColumn("idInterprete");
                int idInterprete = (int) rawModel.getValueAt(selectedRow, col_id);
                modelo.setIdInterprete(idInterprete);
                vista.txtID.setText(String.valueOf(idInterprete));

                // Cargar Fechas al modelo (Manejo de NULOS)
                int col_fechaL = rawModel.findColumn("yearLanzamiento");
                int col_fechaR = rawModel.findColumn("yearRetiro");

                Object rawFechaL = rawModel.getValueAt(selectedRow, col_fechaL);
                Object rawFechaR = rawModel.getValueAt(selectedRow, col_fechaR);

                // Inicializar fechas locales
                LocalDate fechaLanzamiento = null;
                LocalDate fechaRetiro = null;

                // Procesar Fecha de Lanzamiento
                if (rawFechaL != null) {
                    // Cortamos la cadena para asegurar el formato YYYY-MM-DD
                    String fechaLanzamientoStr = rawFechaL.toString().substring(0, 10);
                    fechaLanzamiento = LocalDate.parse(fechaLanzamientoStr);
                }

                // Procesar Fecha de Retiro
                if (rawFechaR != null) {
                    // Cortamos la cadena para asegurar el formato YYYY-MM-DD
                    String fechaRetiroStr = rawFechaR.toString().substring(0, 10);
                    fechaRetiro = LocalDate.parse(fechaRetiroStr);
                }

                // Asignar al modelo DTO
                modelo.setYearLanzamiento(fechaLanzamiento);
                modelo.setYearRetiro(fechaRetiro);

                // Asignar a la vista (si es nulo, el campo se queda vacío)
                vista.txtYearLanzamiento.setText(fechaLanzamiento != null ? fechaLanzamiento.toString() : "");
                vista.txtYearRetiro.setText(fechaRetiro != null ? fechaRetiro.toString() : "");

                // NOTA: El JComboBox (idGeneroPrincipal) ya se cargó gracias a loadTableDetailsToView

            } catch (Exception ex) {
                // Si hay un error, el log mostrará el problema, pero el usuario verá un mensaje más amigable.
                JOptionPane.showMessageDialog(vista, "Error al cargar datos de fecha/ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("❌ ERROR al cargar detalles de la fila: " + ex.getMessage());
            }
        }
    }


    @Override
    protected void agregarListeners() {
        // Listener de la tabla para cargar datos
        vista.tablaInterpretes.getSelectionModel().addListSelectionListener(this::cargarDetalleFilaSeleccionada);

        // Listeners CRUD (Refrescar después de la operación)
        vista.agregarButton.addActionListener(_ -> {
            registrar();
            cargarTablaInterpretes();
        });
        vista.modificarButton.addActionListener(_ -> {
            modificar();
            cargarTablaInterpretes();
        });
        vista.eliminarButton.addActionListener(_ -> {
            eliminar();
            cargarTablaInterpretes();
        });

        vista.regresarButton.addActionListener(_ -> regresarAlMenu());
        vista.limpiarCamposButton.addActionListener(_ -> clearViewFields());
    }
}