package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.InterpreteDAO;
import com.softly.fonoteca.Modelos.DTOs.ComboBoxItem;
import com.softly.fonoteca.Modelos.DTOs.Interprete;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.InterpretesVista;
import com.softly.fonoteca.utilities.FormatDates;
import com.softly.fonoteca.utilities.SQLQuerys;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Controlador principal para la gestión de Intérpretes (Interprete).
 * Implementa el patrón Singleton para asegurar una única instancia del controlador.
 */
public class InterpreteController extends BaseController<Interprete, InterpretesVista, InterpreteDAO> {

    // Campo estático para mantener la única instancia
    private static InterpreteController instance;

    // 🌟 DEFINICIÓN DE COLUMNAS A MOSTRAR 🌟
    private static final String[] DB_COLUMNS_TO_SHOW =
            {"nombre", "tituloInterprete", "yearLanzamiento"};

    private static final String[] DISPLAY_COLUMNS_HEADERS =
            {"Nombre", "Título de interprete", "Año de lanzamiento"};


    /**
     * Constructor privado para la implementación del patrón Singleton.
     * @param modelo Instancia del DTO Interprete.
     * @param vista Instancia de la vista InterpretesVista.
     * @param consultas Instancia del DAO InterpreteDAO.
     * @param vistaPrincipal Vista padre (BaseView) para manejo de navegación.
     */
    private InterpreteController(Interprete modelo, InterpretesVista vista, InterpreteDAO consultas, BaseView vistaPrincipal) {
        super(modelo, vista, consultas, vistaPrincipal);

        // 1. Inicializar la tabla principal
        this.mainTable = vista.tablaInterpretes;

        // 2. Cargar datos de ComboBox y tabla
        vista.cmbGenero.setModel(SQLQuerys.consultarDatos("generos", "idGenero", "nombre"));
        cargarTablaInterpretes();

        // 3. Agregar listeners y iniciar
        agregarListeners();
        iniciar();
    }

    /**
     * Método estático de acceso para obtener la única instancia (Singleton).
     * Si la instancia no existe, la crea; si ya existe, devuelve la existente y la hace visible.
     * @param modelo DTO de Intérprete.
     * @param vista Vista de Intérprete.
     * @param consultas DAO de Intérprete.
     * @param vistaPrincipal Vista desde donde se llama.
     * @return La única instancia de InterpreteController.
     */
    public static InterpreteController getInstance(Interprete modelo, InterpretesVista vista, InterpreteDAO consultas, BaseView vistaPrincipal) {
        if (instance == null) {
            instance = new InterpreteController(modelo, vista, consultas, vistaPrincipal);
        }

        // Si la instancia ya existe, la hacemos visible si está oculta.
        if (!instance.vista.isVisible()) {
            instance.vista.setVisible(true);
        }

        // Actualizamos la vista principal por si cambiamos el flujo de navegación
        instance.vistaPrincipal = vistaPrincipal;

        return instance;
    }

    /**
     * Carga la tabla de intérpretes usando el método genérico del BaseController.
     */
    private void cargarTablaInterpretes() {
        cargarTabla("interpretes", DB_COLUMNS_TO_SHOW, DISPLAY_COLUMNS_HEADERS);
    }


    /**
     * Obtiene el ID del DTO actual (modelo) para operaciones CRUD (modificar/eliminar).
     * @return El ID del intérprete.
     */
    @Override
    protected int getModelId() {
        return modelo.getIdInterprete();
    }

    /**
     * Recolecta los datos de los campos de la vista y los asigna al DTO (modelo).
     * @return true si la recolección fue exitosa; false en caso contrario.
     */
    @Override
    protected boolean collectDataFromView() {
        try {
            LocalDate fechaLanzamiento = FormatDates.getFormatDate(vista.txtYearLanzamiento.getText());
            LocalDate fechaRetiro = FormatDates.getFormatDate(vista.txtYearRetiro.getText());

            // Asignación de ID (0 si es nuevo registro)
            if (!vista.txtID.getText().isEmpty()) {
                modelo.setIdInterprete(Integer.parseInt(vista.txtID.getText()));
            } else {
                modelo.setIdInterprete(0);
            }

            modelo.setNombre(vista.txtNombre.getText());
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

    /**
     * Carga los datos de un DTO de intérprete encontrado a los campos de la vista.
     * @param interpreteEncontrado DTO con los datos obtenidos de la BD.
     */
    @Override
    protected void loadDataToView(Interprete interpreteEncontrado) {
        // A. Sincronizar el modelo
        this.modelo.setIdInterprete(interpreteEncontrado.getIdInterprete());
        this.modelo.setTituloInterprete(interpreteEncontrado.getTituloInterprete());
        this.modelo.setYearLanzamiento(interpreteEncontrado.getYearLanzamiento());
        this.modelo.setYearRetiro(interpreteEncontrado.getYearRetiro());
        this.modelo.setIdGeneroPrincipal(interpreteEncontrado.getIdGeneroPrincipal());
        this.modelo.setNombre(interpreteEncontrado.getNombre());

        // B. Mostrar en la vista
        vista.txtTitulo.setText(interpreteEncontrado.getTituloInterprete());
        vista.txtNombre.setText(interpreteEncontrado.getNombre());
        vista.txtID.setText(String.valueOf(interpreteEncontrado.getIdInterprete()));
        vista.txtYearLanzamiento.setText(interpreteEncontrado.getYearLanzamiento() != null ? interpreteEncontrado.getYearLanzamiento().toString() : "");
        vista.txtYearRetiro.setText(interpreteEncontrado.getYearRetiro() != null ? interpreteEncontrado.getYearRetiro().toString() : "");

        SQLQuerys.setSelectedItemById(vista.cmbGenero, interpreteEncontrado.getIdGeneroPrincipal());
    }

    /**
     * Limpia todos los campos de entrada y ComboBox de la vista.
     */
    @Override
    protected void clearViewFields() {
        modelo.setIdInterprete(0);
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
     * Implementa la lógica de selección de fila para cargar los campos,
     * incluyendo el manejo de fechas NULAS.
     */
    private void cargarDetalleFilaSeleccionada(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && vista.tablaInterpretes.getSelectedRow() != -1 && this.rawModel != null) {

            int selectedRow = vista.tablaInterpretes.getSelectedRow();

            // 1. Mapeo de componentes simples
            Map<String, Object> componentMappings = new HashMap<>();
            componentMappings.put("nombre", vista.txtNombre);
            componentMappings.put("tituloInterprete", vista.txtTitulo);
            componentMappings.put("idGeneroPrincipal", vista.cmbGenero); // ComboBox

            loadTableDetailsToView(e, componentMappings);

            try {
                // 2. Manejo de campos especiales (ID y Fechas NULAS)

                // Obtener ID
                int col_id = rawModel.findColumn("idInterprete");
                int idInterprete = (int) rawModel.getValueAt(selectedRow, col_id);
                modelo.setIdInterprete(idInterprete);
                vista.txtID.setText(String.valueOf(idInterprete));

                // Cargar Fechas al modelo y vista (Manejo de NULOS)
                int col_fechaL = rawModel.findColumn("yearLanzamiento");
                int col_fechaR = rawModel.findColumn("yearRetiro");

                Object rawFechaL = rawModel.getValueAt(selectedRow, col_fechaL);
                Object rawFechaR = rawModel.getValueAt(selectedRow, col_fechaR);

                LocalDate fechaLanzamiento = null;
                LocalDate fechaRetiro = null;

                if (rawFechaL != null && !rawFechaL.toString().isEmpty()) {
                    String fechaLanzamientoStr = rawFechaL.toString().substring(0, 10);
                    fechaLanzamiento = LocalDate.parse(fechaLanzamientoStr);
                }

                if (rawFechaR != null && !rawFechaR.toString().isEmpty()) {
                    String fechaRetiroStr = rawFechaR.toString().substring(0, 10);
                    fechaRetiro = LocalDate.parse(fechaRetiroStr);
                }

                // Asignar al modelo DTO
                modelo.setYearLanzamiento(fechaLanzamiento);
                modelo.setYearRetiro(fechaRetiro);

                // Asignar a la vista (si es nulo, el campo se queda vacío)
                vista.txtYearLanzamiento.setText(fechaLanzamiento != null ? fechaLanzamiento.toString() : "");
                vista.txtYearRetiro.setText(fechaRetiro != null ? fechaRetiro.toString() : "");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al cargar datos de fecha/ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("❌ ERROR al cargar detalles de la fila: " + ex.getMessage());
            }
        }
    }


    /**
     * Define y agrega los Listeners a todos los componentes de la vista.
     */
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

        // Listeners Funcionales
        vista.regresarButton.addActionListener(_ -> regresarAlMenu());
        vista.limpiarCamposButton.addActionListener(_ -> clearViewFields());
    }

    /**
     * Hace visible la vista del controlador.
     * Configura el comportamiento de cierre para solo ocultar la ventana (JFrame.HIDE_ON_CLOSE).
     */
    @Override
    public void iniciar() {
        this.vista.pack();
        this.vista.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.vista.setVisible(true);
        this.vista.setLocationRelativeTo(null);
    }
}