package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.AlbumDAO;
import com.softly.fonoteca.Modelos.DAOs.CancionDAO;
import com.softly.fonoteca.Modelos.DAOs.GeneroDAO;
import com.softly.fonoteca.Modelos.DAOs.InterpreteDAO;
import com.softly.fonoteca.Modelos.DTOs.*;
import com.softly.fonoteca.Vistas.*;
import com.softly.fonoteca.utilities.ComponentValidation;
import com.softly.fonoteca.utilities.FormatDates;
import com.softly.fonoteca.utilities.SQLQuerys;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * * Controlador principal para la gestión de Canciones.
 * Implementa la lógica CRUD y la interacción entre Cancion (modelo), CancionesVista (vista) y CancionDAO (consultas).
 */
public class CancionController extends BaseController<Cancion, CancionesVista, CancionDAO> {

    // 🌟 1. DEFINICIÓN DE COLUMNAS A MOSTRAR 🌟
    // Se asume que el rawModel se construye con JOINS o que los nombres de las tablas relacionadas
    // (interpretes, generos) tienen las columnas 'nombre' y se puede mapear.
    // Usaremos columnas simples en el rawModel para el mapeo a la vista, y usaremos el método genérico
    // para mostrar las columnas requeridas: titulo, idInterprete, idGenero, idioma.
    private static final String TABLE_NAME = "canciones";
    private static final String[] DB_COLUMNS_TO_SHOW =
            {"titulo", "idInterpretePrincipal", "idGenero", "idioma"};

    private static final String[] DISPLAY_COLUMNS_HEADERS =
            {"Título", "Intérprete", "Género", "Idioma"};


    /**
     * Constructor del controlador de canciones.
     * Inicializa los componentes, carga los ComboBoxes y la tabla.
     *
     * @param modelo         Instancia del DTO Cancion.
     * @param vista          Instancia de la vista CancionesVista.
     * @param consultas      Instancia del DAO CancionDAO.
     * @param vistaPrincipal Vista padre (BaseView) para manejo de navegación.
     */
    public CancionController(Cancion modelo, CancionesVista vista, CancionDAO consultas, BaseView vistaPrincipal) {
        super(modelo, vista, consultas, vistaPrincipal);

        // Inicializar la tabla principal
        this.mainTable = vista.tablaCanciones;

        cargarDatosBD();
        cargarTablaCanciones();
        agregarListeners();
    }

    // ------------------------------------------
    // --- IMPLEMENTACIONES ABSTRACTAS/HEREDADAS---
    // ------------------------------------------

    /**
     * Obtiene el ID del DTO actual (modelo) para operaciones CRUD (modificar/eliminar).
     *
     * @return El ID de la canción.
     */
    @Override
    protected int getModelId() {
        return modelo.getIdCancion();
    }

    /**
     * Define y agrega los Listeners a todos los componentes de la vista.
     */
    @Override
    protected void agregarListeners() {
        // Listener de la tabla para cargar datos al seleccionar una fila
        this.vista.tablaCanciones.getSelectionModel().addListSelectionListener(this::cargarDetalleFilaSeleccionada);

        // Listeners CRUD
        this.vista.getBtnAgregar().addActionListener(e -> {
            registrar();
            cargarTablaCanciones();
        });
        this.vista.getBtnModificar().addActionListener(e -> {
            modificar();
            cargarTablaCanciones();
        });
        this.vista.getBtnEliminar().addActionListener(e -> {
            eliminar();
            cargarTablaCanciones();
        });
        this.vista.getBtnLimpiar().addActionListener(e -> clearViewFields());

        // Listeners de navegación (BaseController)
        this.vista.getBtnRegresarMenu().addActionListener(e -> regresarAlMenu());

        // Listeners a otras ventanas de administración
        this.vista.administarAlbumnesButton.addActionListener(e -> {
            Album modeloAlbum = new Album();
            AlbumVista vistaAlbum = new AlbumVista();
            AlbumDAO consultasAlbum = new AlbumDAO();
            AlbumController controllerAlbum = AlbumController.getInstance(modeloAlbum, vistaAlbum, consultasAlbum, this.vista);
            this.vista.setVisible(false);
        });

        this.vista.administrarGenerosButton.addActionListener(e -> {
            launchGeneros();
        });
        this.vista.administrarInterpretesButton.addActionListener(e-> {
            Interprete interprete = new Interprete();
            InterpretesVista interpretesVista = new InterpretesVista();
            InterpreteDAO interpreteDAO = new InterpreteDAO();
            InterpreteController interpreteController = InterpreteController.getInstance(interprete,interpretesVista,interpreteDAO,this.vista);
            interpreteController.iniciar();
            this.vista.setVisible(false);
        });


    }

    void launchGeneros() {
        Genero genero = new Genero();
        GenerosVista generosVista = new GenerosVista();
        GeneroDAO consultasGenero = new GeneroDAO();
        GenerosController generosController = GenerosController.getInstance(genero, generosVista, consultasGenero, vista);        System.out.println("DEBUG: Se creó el GenerosController. Ocultando vista actual.");
        this.vista.setVisible(false);
    }

    @Override
    public void iniciar() {
        this.vista.pack();
        this.vista.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.vista.setVisible(true);
        this.vista.setLocationRelativeTo(null);
    }

    /**
     * Recolecta los datos de los campos de la vista y los asigna al DTO (modelo).
     * Ejecuta la validación de campos antes de la asignación.
     *
     * @return true si los datos son válidos y asignados; false si hay errores de validación o formato.
     */
    @Override
    protected boolean collectDataFromView() {
        if (!validarDatos()) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Por favor, corrige los errores resaltados en el formulario.",
                    "⚠️ Error de Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        try {
            // Asignación de campos
            modelo.setTitulo(vista.txtTitulo.getText());
            modelo.setDuracion(vista.txtDuracion.getText());
            modelo.setBpm(Integer.parseInt(vista.txtBPM.getText()));
            modelo.setIdioma(vista.txtIdiomaCancion.getText());
            modelo.setFechaLanzamiento(FormatDates.getFormatDate(vista.txtFechaLanzamiento.getText()));

            // Asignación de ComboBoxes (IDs foráneos)
            ComboBoxItem albumSeleccionado = (ComboBoxItem) vista.cmbAlbum.getSelectedItem();
            modelo.setAlbum(albumSeleccionado.getId());
            ComboBoxItem interpreteSeleccionado = (ComboBoxItem) vista.cmbInterprete.getSelectedItem();
            modelo.setInterprete(interpreteSeleccionado.getId());
            ComboBoxItem generoSeleccionado = (ComboBoxItem) vista.cmbGenero.getSelectedItem();
            modelo.setGenero(generoSeleccionado.getId());

            modelo.setInstrumental(vista.esInstrumentalCheckBox.isSelected());

            return true;

        } catch (NumberFormatException e) {
            System.err.println("Error de conversión de números o IDs: " + e.getMessage());
            JOptionPane.showMessageDialog(vista, "Error interno de formato de datos (BPM o ComboBox IDs).", "Error Crítico", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Carga los datos de un DTO encontrado (cancionEncontrada) a los campos de la vista.
     *
     * @param cancionEncontrada DTO con los datos obtenidos de la BD.
     */
    @Override
    protected void loadDataToView(Cancion cancionEncontrada) {

        // A. Sincronizar el modelo heredado con el DTO encontrado
        this.modelo.setIdCancion(cancionEncontrada.getIdCancion());
        this.modelo.setTitulo(cancionEncontrada.getTitulo());
        this.modelo.setDuracion(cancionEncontrada.getDuracion());
        this.modelo.setBpm(cancionEncontrada.getBpm());
        this.modelo.setIdioma(cancionEncontrada.getIdioma());
        this.modelo.setFechaLanzamiento(cancionEncontrada.getFechaLanzamiento());
        this.modelo.setAlbum(cancionEncontrada.getAlbum());
        this.modelo.setGenero(cancionEncontrada.getGenero());
        this.modelo.setInterprete(cancionEncontrada.getInterprete());
        this.modelo.setInstrumental(cancionEncontrada.isInstrumental());


        // B. Mostrar los datos en los campos de la vista
        vista.txtIDCancion.setText(String.valueOf(cancionEncontrada.getIdCancion()));
        vista.txtTitulo.setText(cancionEncontrada.getTitulo());
        vista.txtDuracion.setText(cancionEncontrada.getDuracion());
        vista.txtBPM.setText(String.valueOf(cancionEncontrada.getBpm()));
        vista.txtIdiomaCancion.setText(cancionEncontrada.getIdioma());
        vista.txtFechaLanzamiento.setText(cancionEncontrada.getFechaLanzamiento().toString());
        vista.esInstrumentalCheckBox.setSelected(cancionEncontrada.isInstrumental());

        // Seleccionar ComboBoxes por ID
        SQLQuerys.setSelectedItemById(vista.cmbGenero, cancionEncontrada.getGenero());
        SQLQuerys.setSelectedItemById(vista.cmbAlbum, cancionEncontrada.getAlbum());
        SQLQuerys.setSelectedItemById(vista.cmbInterprete, cancionEncontrada.getInterprete());
    }

    /**
     * Limpia todos los campos de entrada y ComboBoxes de la vista.
     */
    @Override
    protected void clearViewFields() {
        modelo.setIdCancion(0);
        vista.txtIDCancion.setText(""); // Asumo que existe un txtIdCancion
        vista.txtTitulo.setText("");
        vista.txtDuracion.setText("");
        vista.txtBPM.setText("");
        vista.txtIdiomaCancion.setText("");
        vista.txtFechaLanzamiento.setText("");
        vista.cmbAlbum.setSelectedIndex(0);
        vista.cmbGenero.setSelectedIndex(0);
        vista.cmbInterprete.setSelectedIndex(0);
        vista.esInstrumentalCheckBox.setSelected(false);
        vista.tablaCanciones.clearSelection();
    }

    // ------------------------------------------
    // --- MÉTODOS LOCALES ESPECÍFICOS ---
    // ------------------------------------------

    /**
     * Carga los modelos de ComboBox (Álbum, Género, Intérprete) desde la BD.
     */
    private void cargarDatosBD() {
        // Carga de ComboBoxes (IDs foráneos)
        vista.cmbAlbum.setModel(SQLQuerys.consultarDatos("albumnes", "idAlbum", "titulo"));
        vista.cmbGenero.setModel(SQLQuerys.consultarDatos("generos", "idGenero", "nombre"));
        vista.cmbInterprete.setModel(SQLQuerys.consultarDatos("interpretes", "idInterprete", "nombre"));
    }

    /**
     * Carga la tabla de canciones con las columnas filtradas (título, interprete, género, idioma).
     */
    private void cargarTablaCanciones() {
        // Llama al método genérico del BaseController
        cargarTabla(TABLE_NAME, DB_COLUMNS_TO_SHOW, DISPLAY_COLUMNS_HEADERS);
    }

    /**
     * Lógica local para buscar una canción por su ID ingresado en el campo de búsqueda.
     */
    private void buscarCancion() {
        try {
            String idText = this.vista.getSearchText().trim();

            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Ingrese un ID de canción.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Integer.parseInt(idText);

            // Llama a la lógica de búsqueda, mensaje y carga de la clase base
            buscarRegistroPorId(id);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "El valor de búsqueda debe ser un ID numérico.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Error en buscarCancion(): " + e.getMessage());
            JOptionPane.showMessageDialog(vista, "Ocurrió un error al buscar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Valida todos los campos del formulario, resaltando errores y mostrando mensajes.
     *
     * @return true si todos los campos son válidos; false en caso contrario.
     */
    private boolean validarDatos() {
        java.util.LinkedHashMap<javax.swing.JComponent, String[]> validaciones = new java.util.LinkedHashMap<>();

        validaciones.put(vista.txtTitulo, new String[]{"required", "Título"});
        validaciones.put(vista.txtDuracion, new String[]{"required", "time", "Duración"});
        validaciones.put(vista.txtBPM, new String[]{"required", "BPM"});
        validaciones.put(vista.txtIdiomaCancion, new String[]{"required", "Idioma"});
        validaciones.put(vista.txtFechaLanzamiento, new String[]{"required", "localdate", "Fecha de Lanzamiento"});
        validaciones.put(vista.cmbAlbum, new String[]{"required", "Álbum"});
        validaciones.put(vista.cmbGenero, new String[]{"required", "Género"});
        validaciones.put(vista.cmbInterprete, new String[]{"required", "Intérprete"});

        boolean formularioValido = true;

        for (java.util.Map.Entry<javax.swing.JComponent, String[]> entry : validaciones.entrySet()) {
            javax.swing.JComponent componente = entry.getKey();
            String[] datos = entry.getValue();

            String nombreCampo = datos[datos.length - 1];
            String[] reglas = new String[datos.length - 1];
            System.arraycopy(datos, 0, reglas, 0, datos.length - 1);

            String reglaFallida = ComponentValidation.validate(componente, reglas);
            ComponentValidation.mostrarError(componente, reglaFallida, nombreCampo);

            if (reglaFallida != null) {
                formularioValido = false;
                componente.requestFocusInWindow();
                break;
            }
        }
        return formularioValido;
    }

    /**
     * Implementa la lógica de selección de fila usando el método genérico del BaseController
     * para cargar los campos de detalle de la canción seleccionada.
     */
    private void cargarDetalleFilaSeleccionada(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && vista.tablaCanciones.getSelectedRow() != -1 && this.rawModel != null) {

            int selectedRow = vista.tablaCanciones.getSelectedRow();

            // 1. Mapeo de componentes simples
            Map<String, Object> componentMappings = new HashMap<>();

            // Mapeo a JTextFields/JCheckBox
            componentMappings.put("titulo", vista.txtTitulo);
            componentMappings.put("duracion", vista.txtDuracion);
            componentMappings.put("tiempoBPM", vista.txtBPM);
            componentMappings.put("idioma", vista.txtIdiomaCancion);
            componentMappings.put("fechaLanzamiento", vista.txtFechaLanzamiento);
            componentMappings.put("esInstrumental", vista.esInstrumentalCheckBox);

            // Mapeo a ComboBoxes (IDs foráneos)
            componentMappings.put("idAlbumOriginal", vista.cmbAlbum);
            componentMappings.put("idGenero", vista.cmbGenero);
            componentMappings.put("idInterpretePrincipal", vista.cmbInterprete);


            // Usamos el método genérico para cargar los campos
            loadTableDetailsToView(e, componentMappings);

            try {
                // 2. Manejo del ID y otros campos especiales para el DTO

                // Obtener ID (necesario para CRUD y mostrar en vista.txtIdCancion)
                int col_id = rawModel.findColumn("idCancion");
                int idCancion = (int) rawModel.getValueAt(selectedRow, col_id);
                modelo.setIdCancion(idCancion);
                vista.txtIDCancion.setText(String.valueOf(idCancion));

                // Cargar fechas y otros valores al modelo que no están en el mapeo principal
                // (asumo que loadTableDetailsToView maneja los valores de texto/combo al modelo)

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al cargar datos de la canción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("❌ ERROR al cargar detalles de la fila: " + ex.getMessage());
            }
        }
    }
}