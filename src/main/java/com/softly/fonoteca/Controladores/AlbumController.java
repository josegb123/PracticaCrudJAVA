package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.AlbumDAO;
import com.softly.fonoteca.Modelos.DTOs.Album;
import com.softly.fonoteca.Modelos.DTOs.ComboBoxItem;
import com.softly.fonoteca.Vistas.AlbumVista;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.utilities.FormatDates;
import com.softly.fonoteca.utilities.SQLQuerys;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador principal para la gestión de Álbumes (Album).
 * Implementa el patrón Singleton para asegurar una única instancia del controlador.
 */
public class AlbumController extends BaseController<Album, AlbumVista, AlbumDAO> {

    // Campo estático para mantener la única instancia
    private static AlbumController instance;

    private static final String TABLE_NAME = "albumnes";
    private static final String[] DB_COLUMNS_TO_SHOW =
            {"titulo", "selloDiscografico", "fechaLanzamiento", "idGeneroPrincipal"};

    private static final String[] DISPLAY_COLUMNS_HEADERS =
            {"Título", "Sello", "Lanzamiento", "Género ID"};


    /**
     * Constructor privado para la implementación del patrón Singleton.
     * @param modelo Instancia del DTO Album.
     * @param vista Instancia de la vista AlbumVista.
     * @param consultas Instancia del DAO AlbumDAO.
     * @param vistaPrincipal Vista padre (BaseView) para manejo de navegación.
     */
    private AlbumController(Album modelo, AlbumVista vista, AlbumDAO consultas, BaseView vistaPrincipal) {
        super(modelo, vista, consultas, vistaPrincipal);

        // 1. Inicializar la tabla principal
        this.mainTable = vista.tablaAlbumnes; // Usando el nombre de tabla que indicaste

        // 2. Inicialización de ComboBoxes, tabla y Listeners
        vista.cmbGenero.setModel(SQLQuerys.consultarDatos("generos", "idGenero", "nombre"));
        cargarTablaAlbumnes(); // Cargar datos iniciales
        agregarListeners();

        // 3. El controlador se inicia inmediatamente al ser creado.
        iniciar();
    }

    /**
     * Método estático de acceso para obtener la única instancia (Singleton).
     * Si la instancia no existe, la crea; si ya existe, devuelve la existente y la hace visible.
     * @param modelo DTO de Álbum.
     * @param vista Vista de Álbum.
     * @param consultas DAO de Álbum.
     * @param vistaPrincipal Vista desde donde se llama.
     * @return La única instancia de AlbumController.
     */
    public static AlbumController getInstance(Album modelo, AlbumVista vista, AlbumDAO consultas, BaseView vistaPrincipal) {
        if (instance == null) {
            instance = new AlbumController(modelo, vista, consultas, vistaPrincipal);
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
     * Carga la tabla de álbumes usando el método genérico del BaseController.
     */
    private void cargarTablaAlbumnes() {
        cargarTabla(TABLE_NAME, DB_COLUMNS_TO_SHOW, DISPLAY_COLUMNS_HEADERS);
    }

    /**
     * Obtiene el ID del DTO actual (modelo) para operaciones CRUD (modificar/eliminar).
     * @return El ID del álbum.
     */
    @Override
    protected int getModelId() {
        return modelo.getIdAlbum(); // Usamos el ID del modelo cargado, no el campo de búsqueda
    }

    /**
     * Recolecta los datos de los campos de la vista y los asigna al DTO (modelo).
     * @return true si la recolección fue exitosa; false en caso contrario.
     */
    @Override
    protected boolean collectDataFromView() {
        try {
            LocalDate fechaLanzamiento = FormatDates.getFormatDate(vista.txtFecha.getText());

            // Asignación de ID (0 si es nuevo registro)
            if (!vista.txtIdAlbum.getText().isEmpty()) {
                modelo.setIdAlbum(Integer.parseInt(vista.txtIdAlbum.getText()));
            } else {
                modelo.setIdAlbum(0);
            }

            modelo.setTitulo(vista.txtTitulo.getText());
            modelo.setSelloDiscografico(vista.txtSello.getText());
            modelo.setFechaLanzamiento(fechaLanzamiento);

            ComboBoxItem generoSeleccionado = (ComboBoxItem) vista.cmbGenero.getSelectedItem();
            modelo.setIdGeneroPrincipal(generoSeleccionado != null ? generoSeleccionado.getId() : 0);

            return true;
        } catch (Exception e) {
            System.err.println("Error al recolectar datos: " + e.getMessage());
            JOptionPane.showMessageDialog(vista, "Error interno al recolectar datos: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Carga los datos de un DTO de álbum encontrado a los campos de la vista.
     * @param albumEncontrado DTO con los datos obtenidos de la BD.
     */
    @Override
    protected void loadDataToView(Album albumEncontrado) {

        // A. Sincronizar el modelo heredado con el DTO encontrado
        this.modelo.setIdAlbum(albumEncontrado.getIdAlbum());
        this.modelo.setTitulo(albumEncontrado.getTitulo());
        this.modelo.setSelloDiscografico(albumEncontrado.getSelloDiscografico());
        this.modelo.setFechaLanzamiento(albumEncontrado.getFechaLanzamiento());
        this.modelo.setIdGeneroPrincipal(albumEncontrado.getIdGeneroPrincipal());


        // B. Mostrar los datos en los campos de la vista
        vista.txtTitulo.setText(albumEncontrado.getTitulo());
        vista.txtSello.setText(albumEncontrado.getSelloDiscografico());
        vista.txtIdAlbum.setText(String.valueOf(albumEncontrado.getIdAlbum()));
        vista.txtFecha.setText(albumEncontrado.getFechaLanzamiento().toString());

        // Seleccionar Género por ID
        SQLQuerys.setSelectedItemById(vista.cmbGenero, albumEncontrado.getIdGeneroPrincipal());
    }

    /**
     * Limpia todos los campos de entrada y ComboBox de la vista.
     */
    @Override
    protected void clearViewFields() {
        modelo.setIdAlbum(0); // Limpiar el ID del modelo

        vista.txtTitulo.setText("");
        vista.txtSello.setText("");
        vista.txtIdAlbum.setText("");
        vista.txtFecha.setText("");
        vista.cmbGenero.setSelectedIndex(0);
        vista.tablaAlbumnes.clearSelection(); // Limpiar selección de tabla
    }

    /**
     * Implementa la lógica de selección de fila para cargar los campos de detalle del álbum.
     */
    private void cargarDetalleFilaSeleccionada(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && vista.tablaAlbumnes.getSelectedRow() != -1 && this.rawModel != null) {

            int selectedRow = vista.tablaAlbumnes.getSelectedRow();

            // 1. Mapeo de componentes (nombre de columna BD -> Componente de la vista)
            Map<String, Object> componentMappings = new HashMap<>();
            componentMappings.put("titulo", vista.txtTitulo);
            componentMappings.put("selloDiscografico", vista.txtSello);
            componentMappings.put("fechaLanzamiento", vista.txtFecha);
            componentMappings.put("idGeneroPrincipal", vista.cmbGenero); // ComboBox

            // Usamos el método genérico para cargar los campos
            loadTableDetailsToView(e, componentMappings);

            try {
                // 2. Manejo del ID (campo especial)
                int col_id = rawModel.findColumn("idAlbum");
                int idAlbum = (int) rawModel.getValueAt(selectedRow, col_id);

                // Cargar ID al modelo y a la vista
                modelo.setIdAlbum(idAlbum);
                vista.txtIdAlbum.setText(String.valueOf(idAlbum));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al cargar datos del álbum: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("❌ ERROR al cargar detalles de la fila: " + ex.getMessage());
            }
        }
    }

    /**
     * Define y agrega los Listeners a todos los componentes de la vista.
     */
    @Override
    protected void agregarListeners() {
        // Listener de la tabla para cargar detalles al seleccionar una fila
        vista.tablaAlbumnes.getSelectionModel().addListSelectionListener(this::cargarDetalleFilaSeleccionada);

        // Listeners CRUD (Refrescar tabla después de la operación)
        vista.agregarButton.addActionListener(_ -> {
            registrar();
            cargarTablaAlbumnes();
        });
        vista.modificarButton.addActionListener(_ -> {
            modificar();
            cargarTablaAlbumnes();
        });
        vista.eliminarButton.addActionListener(_ -> {
            eliminar();
            cargarTablaAlbumnes();
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