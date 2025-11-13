package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.GeneroDAO;
import com.softly.fonoteca.Modelos.DTOs.Genero;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.GenerosVista;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.HashMap;
import java.util.Map;

public class GenerosController extends BaseController<Genero, GenerosVista, GeneroDAO> {

    // 🌟 1. Campo estático para mantener la única instancia
    private static GenerosController instance;

    private static final String TABLE_NAME = "generos";

    // 🌟 2. Hacemos el constructor privado para prevenir la instanciación externa
    private GenerosController(Genero modelo, GenerosVista vista, GeneroDAO consultas, BaseView vistaPrincipal) {
        super(modelo, vista, consultas, vistaPrincipal);

        this.mainTable = vista.tablaGeneros;
        cargarTablaGeneros();
        agregarListeners();

        // 🌟 3. Llamamos a iniciar() aquí. Solo se ejecuta una vez cuando se crea la instancia.
        iniciar();
    }

    /**
     * 🌟 4. Método estático de acceso para obtener la única instancia (Singleton).
     * Si la instancia no existe, la crea; si ya existe, devuelve la existente.
     * @param modelo DTO de Género.
     * @param vista Vista de Géneros.
     * @param consultas DAO de Género.
     * @param vistaPrincipal Vista desde donde se llama.
     * @return La única instancia de GenerosController.
     */
    public static GenerosController getInstance(Genero modelo, GenerosVista vista, GeneroDAO consultas, BaseView vistaPrincipal) {
        if (instance == null) {
            instance = new GenerosController(modelo, vista, consultas, vistaPrincipal);
        }

        if (!instance.vista.isVisible()) {
            instance.vista.setVisible(true);
        }
        // Cuando ya existe, nos aseguramos de que la ventana anterior sea la vista principal actual
        instance.vistaPrincipal = vistaPrincipal;
        return instance;
    }

    /**
     * Carga la tabla de géneros usando el método genérico del BaseController.
     * Muestra todas las columnas de la tabla 'generos'.
     */
    private void cargarTablaGeneros() {
        // Al pasar null para las columnas a mostrar y cabeceras, el BaseController
        // mostrará TODAS las columnas que vengan en el rawModel (idGenero, nombre, descripcion).
        cargarTabla(TABLE_NAME, null, null);
    }

    /**
     * Obtiene el ID del modelo DTO que se cargó al seleccionar una fila.
     */
    @Override
    protected int getModelId() {
        // Ya no se usa el campo de búsqueda (getSearchText), sino el ID del modelo DTO.
        return modelo.getIdGenero();
    }

    @Override
    protected boolean collectDataFromView() throws Exception {
        try {
            // Manejo del ID: Si el campo está vacío, asumimos un nuevo registro (ID=0).
            if (!vista.txtIdGenero.getText().isEmpty()) {
                modelo.setIdGenero(Integer.parseInt(vista.txtIdGenero.getText()));
            } else {
                modelo.setIdGenero(0);
            }

            // Validación básica
            if (vista.txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El Nombre del Género es requerido.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            modelo.setNombre(vista.txtNombre.getText());
            modelo.setDescripcion(vista.txtDescripcion.getText());
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "El ID debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            System.err.println(STR."Error al recolectar datos: \{e.getMessage()}");
            JOptionPane.showMessageDialog(vista, STR."Error interno: \{e.getMessage()}", "Error Crítico", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    protected void loadDataToView(Genero generoEncontrado) {
        // Este método se usa si buscarRegistroPorId(id) fuera invocado.

        modelo.setIdGenero(generoEncontrado.getIdGenero());
        modelo.setNombre(generoEncontrado.getNombre());
        modelo.setDescripcion(generoEncontrado.getDescripcion());

        vista.txtIdGenero.setText(String.valueOf(generoEncontrado.getIdGenero()));
        vista.txtNombre.setText(generoEncontrado.getNombre());
        vista.txtDescripcion.setText(generoEncontrado.getDescripcion());
    }

    @Override
    protected void clearViewFields() {
        modelo.setIdGenero(0); // Limpiar el ID del modelo
        vista.txtIdGenero.setText("");
        vista.txtNombre.setText("");
        vista.txtDescripcion.setText("");
        vista.tablaGeneros.clearSelection();
    }

    /**
     * Implementa la lógica de selección de fila para cargar los campos.
     */
    private void cargarDetalleFilaSeleccionada(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && vista.tablaGeneros.getSelectedRow() != -1 && this.rawModel != null) {

            int selectedRow = vista.tablaGeneros.getSelectedRow();

            // 1. Mapeo de componentes (nombre de columna BD -> JTextField)
            Map<String, Object> componentMappings = new HashMap<>();
            componentMappings.put("nombre", vista.txtNombre);
            componentMappings.put("descripcion", vista.txtDescripcion);

            // Usamos el método genérico para cargar los campos
            loadTableDetailsToView(e, componentMappings);

            try {
                // 2. Manejo del ID (campo especial)
                int col_id = rawModel.findColumn("idGenero");
                int idGenero = (int) rawModel.getValueAt(selectedRow, col_id);

                // Cargar ID al modelo y a la vista
                modelo.setIdGenero(idGenero);
                vista.txtIdGenero.setText(String.valueOf(idGenero));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al cargar datos del género: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("❌ ERROR al cargar detalles de la fila: " + ex.getMessage());
            }
        }
    }

    @Override
    protected void agregarListeners() {
        // Listener de la tabla para cargar detalles al seleccionar una fila
        vista.tablaGeneros.getSelectionModel().addListSelectionListener(this::cargarDetalleFilaSeleccionada);

        // Listeners CRUD (Refrescar tabla después de la operación)
        vista.agregarButton.addActionListener(e -> {
            registrar();
            cargarTablaGeneros();
        });
        vista.eliminarButton.addActionListener(e -> {
            eliminar();
            cargarTablaGeneros();
        });
        vista.modificarButton.addActionListener(e -> {
            modificar();
            cargarTablaGeneros();
        });

        vista.limpiarCamposButton.addActionListener(e -> clearViewFields());
        vista.regresarButton.addActionListener(e -> regresarAlMenu());
    }

    @Override
    public void iniciar() {
        this.vista.pack();
        this.vista.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.vista.setVisible(true);
        this.vista.setLocationRelativeTo(null);
    }
}