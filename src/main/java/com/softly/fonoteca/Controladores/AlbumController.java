package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.AlbumDAO;
import com.softly.fonoteca.Modelos.DTOs.Album;
import com.softly.fonoteca.Modelos.DTOs.ComboBoxItem;
import com.softly.fonoteca.Vistas.AlbumVista;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.utilities.FormatDates;
import com.softly.fonoteca.utilities.SQLQuerys;

import javax.swing.*;
import java.time.LocalDate;

/**
 * Controlador principal para la gestión de Álbumes (Album).
 * Implementa el patrón Singleton para asegurar una única instancia del controlador.
 */
public class AlbumController extends BaseController<Album, AlbumVista, AlbumDAO> {

    // Campo estático para mantener la única instancia
    private static AlbumController instance;

    /**
     * Constructor privado para la implementación del patrón Singleton.
     * @param modelo Instancia del DTO Album.
     * @param vista Instancia de la vista AlbumVista.
     * @param consultas Instancia del DAO AlbumDAO.
     * @param vistaPrincipal Vista padre (BaseView) para manejo de navegación.
     */
    private AlbumController(Album modelo, AlbumVista vista, AlbumDAO consultas, BaseView vistaPrincipal) {
        super(modelo, vista, consultas, vistaPrincipal);

        // Inicialización de ComboBoxes y Listeners
        vista.cmbGenero.setModel(SQLQuerys.consultarDatos("generos", "idGenero", "nombre"));
        agregarListeners();

        // El controlador se inicia inmediatamente al ser creado.
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
     * Obtiene el ID del modelo para operaciones de búsqueda, modificación o eliminación.
     * Se asume que el ID de búsqueda está en vista.txtSearch y debe ser numérico.
     * @return El ID del álbum en formato entero.
     */
    @Override
    protected int getModelId() {
        try {
            return Integer.parseInt(vista.txtSearch.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "El ID de búsqueda debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
    }

    /**
     * Recolecta los datos de los campos de la vista y los asigna al DTO (modelo).
     * Ejecuta validaciones simples antes de la asignación.
     * @return true si la recolección fue exitosa; false en caso contrario.
     */
    @Override
    protected boolean collectDataFromView() {
        try {
            LocalDate fechaLanzamiento = FormatDates.getFormatDate(vista.txtFecha.getText());

            modelo.setTitulo(vista.txtTitulo.getText());
            modelo.setSelloDiscografico(vista.txtSello.getText());
            modelo.setFechaLanzamiento(fechaLanzamiento);
            ComboBoxItem generoSeleccionado = (ComboBoxItem) vista.cmbGenero.getSelectedItem();
            modelo.setIdGeneroPrincipal(generoSeleccionado.getId());
            return true;
        } catch (Exception e) {
            System.err.println(STR."Error al recolectar datos: \{e.getMessage()}");
            JOptionPane.showMessageDialog(vista, STR."Error interno al recolectar datos: \{e.getMessage()}", "Error Crítico", JOptionPane.ERROR_MESSAGE);
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
        // Se asume la existencia de txtIdAlbum en la vista
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
    }

    /**
     * Define y agrega los Listeners a todos los componentes de la vista.
     */
    @Override
    protected void agregarListeners() {
        // Listeners CRUD
        vista.agregarButton.addActionListener(e -> registrar());
        vista.modificarButton.addActionListener(e -> modificar());
        vista.eliminarButton.addActionListener(e -> eliminar());

        // Listeners Funcionales
        vista.regresarButton.addActionListener(e -> regresarAlMenu());
        vista.buscarButton.addActionListener(e -> buscarRegistroPorId(getModelId()));
        vista.limpiarCamposButton.addActionListener(e -> clearViewFields());
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