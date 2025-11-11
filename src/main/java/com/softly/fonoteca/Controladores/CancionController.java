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

// 1. HERENCIA: Hereda de BaseController con las restricciones de tipo correctas
public class CancionController extends BaseController<Cancion, CancionesVista, CancionDAO> {

    // Los campos 'modelo', 'vista', 'consultas', 'vistaPrincipal' son ahora 'protected' y heredados.
    // Solo se debe mantener la lógica específica.

    public CancionController(Cancion modelo, CancionesVista vista, CancionDAO consultas, BaseView vistaPrincipal) {
        // Llama al constructor de la clase base.
        super(modelo, vista, consultas, vistaPrincipal);
        // Lógica específica que debe ejecutarse al inicio
        cargarDatosBD();
    }

    // --- IMPLEMENTACIONES ABSTRACTAS OBLIGATORIAS ---

    // 2. OBTENER ID DEL MODELO (para modificar/eliminar)
    @Override
    protected int getModelId() {
        // Asumiendo que el DTO Cancion tiene un método getIdCancion()
        return modelo.getIdCancion();
    }

    // 3. AGREGAR LISTENERS (Vincula botones a métodos centralizados)
    @Override
    protected void agregarListeners() {
        // Vincula los botones usando los getters de la interfaz CRUDView
        this.vista.getBtnAgregar().addActionListener(ActionEvent -> registrar());
        this.vista.getBtnModificar().addActionListener(ActionEvent -> modificar());
        this.vista.getBtnEliminar().addActionListener(ActionEvent -> eliminar());
        this.vista.getBuscarButton().addActionListener(e -> buscarCancion()); // Método local
        this.vista.getBtnRegresarMenu().addActionListener(e -> regresarAlMenu()); // Método de BaseController
        this.vista.getBtnLimpiar().addActionListener(e -> clearViewFields());
        this.vista.administarAlbumnesButton.addActionListener(e -> {
            Album modeloAlbum = new Album();
            AlbumVista vistaAlbum = new AlbumVista();
            AlbumDAO consultasAlbum = new AlbumDAO();
            AlbumController controllerAlbum = new AlbumController(modeloAlbum, vistaAlbum, consultasAlbum, this.vista);
            controllerAlbum.iniciar();
            this.vista.setVisible(false);
        });

        this.vista.administrarGenerosButton.addActionListener(e->{
            Genero genero = new Genero();
            GenerosVista generosVista = new GenerosVista();
            GeneroDAO consultasGenero = new GeneroDAO();
            GenerosController generosController = new GenerosController(genero,generosVista,consultasGenero,vista);
            generosController.iniciar();
            this.vista.setVisible(false);
        });
        this.vista.administrarInterpretesButton.addActionListener(e-> {
            Interprete interprete = new Interprete();
            InterpretesVista interpretesVista = new InterpretesVista();
            InterpreteDAO interpreteDAO = new InterpreteDAO();
            InterpreteController interpreteController = new InterpreteController(interprete,interpretesVista,interpreteDAO,this.vista);
            interpreteController.iniciar();
            this.vista.setVisible(false);
        });
    }

    // 4. RECOLECTAR DATOS DE LA VISTA AL MODELO (para registrar y modificar)
    @Override
    protected boolean collectDataFromView() {
        // El método validarDatos ya incluye la lógica de mensajes y foco.
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
            modelo.setTitulo(vista.txtTitulo.getText());
            modelo.setDuracion(vista.txtDuracion.getText());
            modelo.setBpm(Integer.parseInt(vista.txtBPM.getText()));
            modelo.setIdioma(vista.txtIdiomaCancion.getText());
            modelo.setFechaLanzamiento(FormatDates.getFormatDate(vista.txtFechaLanzamiento.getText()));
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

    // 5. CARGAR DATOS DEL MODELO A LA VISTA (después de buscar)
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
        vista.txtIdiomaCancion.setText(String.valueOf(cancionEncontrada.getIdCancion()));
        vista.txtTitulo.setText(cancionEncontrada.getTitulo());
        vista.txtDuracion.setText(cancionEncontrada.getDuracion());
        vista.txtBPM.setText(String.valueOf(cancionEncontrada.getBpm()));
        vista.txtIdiomaCancion.setText(cancionEncontrada.getIdioma());
        vista.txtFechaLanzamiento.setText(cancionEncontrada.getFechaLanzamiento().toString());
        // Seleccionar Género
        SQLQuerys.setSelectedItemById(vista.cmbGenero, cancionEncontrada.getGenero());

        // Seleccionar Álbum
        SQLQuerys.setSelectedItemById(vista.cmbAlbum, cancionEncontrada.getAlbum());

        // Seleccionar Intérprete
        SQLQuerys.setSelectedItemById(vista.cmbInterprete, cancionEncontrada.getInterprete());

        // El mensaje de éxito lo maneja la clase base
    }

    // 6. LIMPIAR CAMPOS (Asumo que la vista tiene un método limpiarCampos)
    @Override
    protected void clearViewFields() {
        vista.txtTitulo.setText("");
        vista.txtDuracion.setText("");
        vista.txtBPM.setText("");
        vista.txtIdiomaCancion.setText("");
        vista.txtFechaLanzamiento.setText("");
        vista.cmbAlbum.setSelectedIndex(0);
        vista.cmbGenero.setSelectedIndex(0);
        vista.cmbInterprete.setSelectedIndex(0);
    }

    // --- MÉTODOS LOCALES ESPECÍFICOS DE CANCIÓN ---

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

    private void cargarDatosBD() {
        vista.cmbAlbum.setModel(SQLQuerys.consultarDatos("albumnes", "idAlbum", "titulo"));
        vista.cmbGenero.setModel(SQLQuerys.consultarDatos("generos", "idGenero", "nombre"));
        vista.cmbInterprete.setModel(SQLQuerys.consultarDatos("interpretes", "idInterprete", "nombre"));
    }

    /**
     * Valida todos los campos del formulario. (Lógica de validación movida aquí)
     *
     * @return true si todos los campos son válidos; false en caso contrario.
     */
    private boolean validarDatos() {
        // Lógica de validación completa (se mantiene igual)
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
            javax.swing.JComponent componente = entry.getKey(); // ¡Ahora es JComponent!
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
}