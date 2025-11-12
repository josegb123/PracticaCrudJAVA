package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.ReproduccionDAO;
import com.softly.fonoteca.Modelos.DTOs.ComboBoxItem;
import com.softly.fonoteca.Modelos.DTOs.Reproduccion;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.ReproduccionesVista;
import com.softly.fonoteca.utilities.SQLQuerys;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * 🎵 Controlador para la visualización detallada de las reproducciones registradas.
 * Se encarga de cargar los datos de la tabla 'reproducciones' en un JTable,
 * convirtiendo las claves foráneas (IDs) a sus respectivos nombres (títulos/usuarios)
 * para la visualización y maneja las operaciones CRUD.
 */
public class ReproduccionesController {

    // --- DEPENDENCIAS Y MODELOS ---
    private final Reproduccion modelo;
    private final ReproduccionesVista vista;
    private final ReproduccionDAO consultas;
    private final BaseView vistaPrincipal;

    /**
     * Modelo de tabla con los datos crudos obtenidos directamente de la BD (contiene IDs).
     * Se mantiene para poder obtener los IDs al seleccionar una fila.
     */
    private DefaultTableModel rawModel;

    // 💡 NOTA: Asumimos que la vista tiene campos o ComboBoxes para obtener:
    // vista.cmbUsuario.getSelectedItem().getId()
    // vista.cmbCancion.getSelectedItem().getId()
    // vista.txtSegundos.getText()

    /**
     * Constructor del controlador de Reproducciones.
     *
     * @param modelo         El DTO de Reproduccion (modelo de datos).
     * @param vista          La vista de la tabla de reproducciones.
     * @param consultas      La capa DAO para interactuar con la tabla de Reproducciones.
     * @param vistaPrincipal La vista padre a la que se debe regresar.
     */
    public ReproduccionesController(Reproduccion modelo, ReproduccionesVista vista, ReproduccionDAO consultas, BaseView vistaPrincipal) {
        this.modelo = modelo;
        this.vista = vista;
        this.consultas = consultas;
        this.vistaPrincipal = vistaPrincipal;

        cargarTablaReproducciones();
        vista.cmbCanciones.setModel(SQLQuerys.consultarDatos("canciones", "idCancion", "titulo"));
        vista.cmbUsuarios.setModel(SQLQuerys.consultarDatos("usuarios", "idUsuario", "nombres"));

        agregarListeners();
    }

    // ------------------------------------------------------------------------------------------
    // --- LÓGICA DE CARGA Y TRANSFORMACIÓN DE DATOS ---
    // ------------------------------------------------------------------------------------------

    /**
     * Carga todos los registros de la tabla 'reproducciones', realiza la conversión
     * de IDs a nombres y asigna el modelo final al JTable de la vista.
     */
    private void cargarTablaReproducciones() {
        try {
            Map<String, String> columnMapping = new HashMap<>();

            // 1. OBTENER EL MODELO CRÍTICO CON LOS IDs ORIGINALES de la BD.
            this.rawModel = SQLQuerys.buildTableModel("reproducciones", columnMapping);

            // 2. CONVERTIR IDs A NOMBRES usando el rawModel para el modelo de la vista.
            DefaultTableModel finalModel = convertirIDsANombres(this.rawModel);

            // 3. Asignar el modelo final a la tabla.
            vista.tablaReproducciones.setModel(finalModel);


        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar la tabla de reproducciones: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ ERROR FATAL en cargarTablaReproducciones: " + e.getMessage());
        }
    }

    /**
     * Recorre el modelo de tabla (con IDs), consulta la BD para obtener los nombres
     * de usuarios y canciones, y genera un nuevo modelo listo para la vista.
     *
     * @param model El DefaultTableModel retornado por SQLQuerys.buildTableModel() (contiene IDs).
     * @return Un nuevo DefaultTableModel con los IDs reemplazados por nombres/títulos.
     */
    private DefaultTableModel convertirIDsANombres(DefaultTableModel model) {
        if (model.getRowCount() == 0) return model;

        String[] newColumnNames = {"Usuario", "Canción", "Fecha", "Hora", "Segundos"};
        DefaultTableModel newModel = new DefaultTableModel(newColumnNames, 0);

        int col_idUsuario = model.findColumn("idUsuario");
        int col_idCancion = model.findColumn("idCancion");
        int col_fecha = model.findColumn("fechaReproduccion");

        int col_hora = model.findColumn("HoraReproduccion");
        if (col_hora == -1) col_hora = model.findColumn("horaReproduccion");

        int col_segundos = model.findColumn("segundosReproducidos");

        if (col_idUsuario == -1 || col_idCancion == -1 || col_fecha == -1 || col_hora == -1 || col_segundos == -1) {
            System.err.println("❌ ERROR: Una columna clave de BD no fue encontrada.");
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
                System.err.println("❌ ERROR: Fallo de conversión de tipos en la fila " + i + ". ¿Los IDs no son INT?");
            }
        }

        return newModel;
    }

    // ------------------------------------------------------------------------------------------
    // --- LÓGICA CRUD ---
    // ------------------------------------------------------------------------------------------

    /**
     * Intenta guardar una nueva reproducción. Usa la lógica de UPSERT (insertar o modificar)
     * implementada en el ReproduccionDAO.
     * Requiere que la vista proporcione el ID de Usuario, ID de Canción, Fecha, Hora y Segundos.
     */
    private void agregarReproduccion() {
        if (!validarCampos()) return;

        // 1. Obtener datos de la vista y mapear al modelo DTO
        if (!mapearVistaAModelo()) return;

        // 2. Ejecutar la operación de inserción/modificación (UPSERT)
        if (consultas.vincular(modelo)) {
            JOptionPane.showMessageDialog(vista, "Reproducción guardada (agregada/modificada) con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaReproducciones(); // Refrescar la tabla
        } else {
            JOptionPane.showMessageDialog(vista, "Error al guardar la reproducción.", "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Modifica el registro de la reproducción actualmente seleccionado/mostrado.
     * Reutiliza la lógica de vinculación/modificación.
     */
    private void modificarReproduccion() {
        if (!validarCampos()) return;

        // 1. Obtener datos de la vista y mapear al modelo DTO
        if (!mapearVistaAModelo()) return;

        // 2. Ejecutar la modificación
        if (consultas.modificar(modelo)) {
            JOptionPane.showMessageDialog(vista, "Reproducción modificada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaReproducciones(); // Refrescar la tabla
        } else {
            JOptionPane.showMessageDialog(vista, "Error al modificar la reproducción. Asegúrese de que el Usuario y la Canción ya existen.", "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina el registro de la reproducción actualmente seleccionado/mostrado en los campos.
     */
    private void eliminarReproduccion() {
        // 1. Asegurarse de que las claves (idUsuario, idCancion) estén cargadas en el modelo
        if (!mapearVistaAModelo()) return;

        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro de eliminar la reproducción del usuario " + modelo.getIdUsuario() + " para la canción " + modelo.getIdCancion() + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (consultas.desvincular(modelo)) {
                JOptionPane.showMessageDialog(vista, "Reproducción eliminada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTablaReproducciones(); // Refrescar la tabla
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
        // 💡 NOTA: Asumo que tienes ComboBoxes para Usuario y Canción que ya proporcionan IDs válidos > 0.
        // Aquí solo se verifica que los campos de texto esenciales tengan formato.
        try {
            // Asumo que el ID de usuario y canción provienen de ComboBoxes (o campos de IDs ocultos)
            // Aquí se valida el formato de Segundos, Fecha y Hora

            // Si txtSegundosReproduccidos contiene " X seg", se debe limpiar antes de parsear.
            String segundosStr = vista.txtSegundosReproduccidos.getText().replaceAll("[^0-9]", "").trim();
            Integer.parseInt(segundosStr);

            // Se valida el formato de fecha y hora
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
            // Asumo que tienes ComboBoxes para IDs o algún otro mecanismo para obtener IDs válidos
            // 💡 IMPORTANTE: Debes asegurarte de tener el ID del Usuario y la Canción seleccionados.
            // Si usas ComboBoxItem, sería algo como:
            int idUsuario = ((ComboBoxItem) vista.cmbUsuarios.getSelectedItem()).getId();
            int idCancion = ((ComboBoxItem) vista.cmbCanciones.getSelectedItem()).getId();

            // Para fines de prueba y si solo tienes campos de texto:
            // int idUsuario = Integer.parseInt(vista.txtIdUsuario.getText());
            // int idCancion = Integer.parseInt(vista.txtIdCancion.getText());

            // Si los IDs provienen del registro seleccionado, ya están en el modelo.
            // Para operaciones CRUD, se asume que la vista tiene campos específicos para la entrada de IDs.

            // --- Carga de IDs (Ajusta esto según tus componentes de entrada) ---
            // Si la vista está diseñada para ingresar/seleccionar un nuevo registro:
            modelo.setIdUsuario(idUsuario);
            modelo.setIdCancion(idCancion);

            // Si solo se están modificando los datos de la fila seleccionada, los IDs ya deberían estar en el DTO.

            // --- Carga de Datos (requiere limpieza si se muestra con unidades " seg") ---
            String segundosStr = vista.txtSegundosReproduccidos.getText().replaceAll("[^0-9]", "").trim();
            int segundos = Integer.parseInt(segundosStr);

            modelo.setFechaReproduccion(LocalDate.parse(vista.txtFechaReproduccion.getText()));
            modelo.setHoraReproduccion(LocalTime.parse(vista.txtHoraReproduccion.getText()));
            modelo.setSegundosReproducidos(segundos);

            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Faltan datos de ID o el formato de fecha/hora/segundos es incorrecto.", "Error de Datos", JOptionPane.ERROR_MESSAGE);
            System.err.println("❌ ERROR al mapear vista a modelo: " + e.getMessage());
            return false;
        }
    }


    // ------------------------------------------------------------------------------------------
    // --- MANEJO DE EVENTOS Y VISTA ---
    // ------------------------------------------------------------------------------------------

    /**
     * Configura y añade todos los listeners necesarios a los componentes de la vista.
     */
    private void agregarListeners() {
        // Listener para la selección de fila en la tabla.
        vista.tablaReproducciones.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cargarDetalleFilaSeleccionada();
                }
            }
        });

        // 💡 LISTENERS DE BOTONES CRUD
        // Asegúrate de que los nombres de los botones (btnAgregar, etc.) sean correctos en tu vista.
        vista.agregarButton.addActionListener(e -> agregarReproduccion());
        vista.modificarButton.addActionListener(e -> modificarReproduccion());
        vista.eliminarButton.addActionListener(e -> eliminarReproduccion());

        // Listener para el botón de regresar.
        vista.regresarButton.addActionListener(e -> cerrarVista());
    }

    /**
     * Carga el detalle de la reproducción seleccionada en los campos de texto.
     * Es CRÍTICO que esta función use el 'rawModel' para buscar los IDs originales
     * y cargarlos en el modelo DTO (this.modelo) para usarlos en el CRUD.
     */
    private void cargarDetalleFilaSeleccionada() {
        int selectedRow = vista.tablaReproducciones.getSelectedRow();

        if (selectedRow != -1 && this.rawModel != null) {

            try {
                // Obtener los IDs y datos del modelo ORIGINAL (rawModel)
                int idUsuario = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("idUsuario"));
                int idCancion = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("idCancion"));

                // 💡 CRÍTICO: Cargar los IDs al DTO del controlador para que CRUD los use.
                modelo.setIdUsuario(idUsuario);
                modelo.setIdCancion(idCancion);

                String fecha = rawModel.getValueAt(selectedRow, rawModel.findColumn("fechaReproduccion")).toString();

                int col_hora_raw = rawModel.findColumn("HoraReproduccion");
                if (col_hora_raw == -1) col_hora_raw = rawModel.findColumn("horaReproduccion");
                String hora = rawModel.getValueAt(selectedRow, col_hora_raw).toString();

                int segundos = (int) rawModel.getValueAt(selectedRow, rawModel.findColumn("segundosReproducidos"));

                // Obtener los nombres ya convertidos del modelo de la vista (columna 0 y 1)
                //String nombreUsuario = (String) vista.tablaReproducciones.getValueAt(selectedRow, 0);
                //String nombreCancion = (String) vista.tablaReproducciones.getValueAt(selectedRow, 1);

                SQLQuerys.setSelectedItemById(vista.cmbUsuarios,idUsuario);
                SQLQuerys.setSelectedItemById(vista.cmbCanciones,idCancion);
                // Mostrar detalles en los componentes de la vista
                //vista.txtNombreUsuario.setText(nombreUsuario != null ? nombreUsuario : "Usuario no encontrado");
                //vista.txtNombreCancion.setText(nombreCancion != null ? nombreCancion : "Canción no encontrada");
                vista.txtFechaReproduccion.setText(STR."\{fecha}");
                vista.txtHoraReproduccion.setText(STR."\{hora}");
                vista.txtSegundosReproduccidos.setText(String.valueOf(segundos) + " seg");

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
     * Configura y hace visible la ventana de Reproducciones.
     */
    public void iniciar() {
        this.vista.pack();
        this.vista.setDefaultCloseOperation(ReproduccionesVista.DISPOSE_ON_CLOSE);
        this.vista.setVisible(true);
    }
}