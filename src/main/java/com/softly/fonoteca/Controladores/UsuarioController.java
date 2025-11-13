package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.UsuarioDAO;
import com.softly.fonoteca.Modelos.DTOs.Usuario;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.UsuariosVista;
import com.softly.fonoteca.utilities.FormatDates;
import com.softly.fonoteca.utilities.SecurityUtils;

import javax.swing.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UsuarioController extends BaseController<Usuario, UsuariosVista, UsuarioDAO> {

    // 🌟 1. DEFINICIÓN DE COLUMNAS A MOSTRAR 🌟
    // Columnas de la BD que queremos mostrar en la tabla (deben coincidir con el nombre de la columna en la BD)
    private static final String[] DB_COLUMNS_TO_SHOW =
            {"idUsuario", "email", "nombres", "paisNacimiento"};

    // Nombres de las cabeceras que se mostrarán al usuario
    private static final String[] DISPLAY_COLUMNS_HEADERS =
            {"ID", "Email", "Nombre(s)", "País de Nacimiento"};


    public UsuarioController(Usuario modelo, UsuariosVista vista, UsuarioDAO consultas, BaseView vistaPrincipal) {
        super(modelo, vista, consultas, vistaPrincipal);

        // 1. Inicializar la tabla principal
        this.mainTable = vista.tablaUsuarios;

        // 2. Cargar los datos al iniciar el controlador usando el método GENÉRICO
        cargarTablaUsuarios();
    }

    /**
     * 🌟 MÉTODO SIMPLIFICADO: Usa la implementación genérica del BaseController.
     */
    private void cargarTablaUsuarios() {
        // Llama al método del BaseController para cargar y filtrar la tabla.
        cargarTabla("usuarios", DB_COLUMNS_TO_SHOW, DISPLAY_COLUMNS_HEADERS);
        // Si quisieras mostrar todas las columnas, usarías: cargarTabla("usuarios", null, null);
    }

    // 🌟 MÉTODO ELIMINADO: transformarModeloUsuarios ya no es necesario.
    // ...

    @Override
    protected int getModelId() {
        return modelo.getId();
    }

    // --- MÉTODOS HEREDADOS Y ABSTRACTOS (Sin cambios en la lógica central) ---

    @Override
    protected boolean collectDataFromView() {
        // ... (Tu lógica de recolección de datos se mantiene sin cambios) ...
        try {
            // 1. Validar campos
            if (vista.txtEmail.getText().trim().isEmpty() || vista.txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El Email y Nombre son campos requeridos.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // 2. Recolectar datos de la vista y actualizar el modelo (incluyendo el ID)
            LocalDate fechaNacimiento = FormatDates.getFormatDate(vista.txtFechaN.getText());

            // Manejo de la contraseña
            String nuevaPassword = new String(vista.passField.getPassword());
            if (!nuevaPassword.isEmpty()) {
                modelo.setPassword(SecurityUtils.hashPassword(nuevaPassword));
            }
            if (modelo.getId() == 0 && nuevaPassword.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "La contraseña es requerida para nuevos registros.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            modelo.setEmail(vista.txtEmail.getText());
            modelo.setNombres(vista.txtNombre.getText());
            modelo.setApellidos(vista.txtApellido.getText());
            modelo.setSexo(vista.txtSexo.getText());
            modelo.setFechaNacimiento(fechaNacimiento);
            modelo.setPaisNacimiento(vista.txtPaisN.getText());
            modelo.setPaisResidencia(vista.txtPaisR.getText());
            modelo.setIdioma(vista.txtIdioma.getText());

            return true;

        } catch (Exception e) {
            System.err.println(STR."Error de formato de fecha o datos: \{e.getMessage()}");
            JOptionPane.showMessageDialog(vista, STR."Error al procesar datos: \{e.getMessage()}", "Error Crítico", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    @Override
    protected void loadDataToView(Usuario usuarioEncontrado) {
        // Se mantiene para el cumplimiento abstracto. Los datos se cargan desde la tabla.
        modelo.setId(usuarioEncontrado.getId());
        modelo.setEmail(usuarioEncontrado.getEmail());
        modelo.setPassword(usuarioEncontrado.getPassword());
        modelo.setNombres(usuarioEncontrado.getNombres());
        // ... (Cargar todas las propiedades del DTO) ...

        vista.txtEmail.setText(usuarioEncontrado.getEmail());
        vista.passField.setText("********");
        vista.txtNombre.setText(usuarioEncontrado.getNombres());
        // ... (Mostrar los demás campos) ...
    }

    @Override
    protected void clearViewFields() {
        modelo.setId(0);
        vista.txtEmail.setText("");
        vista.passField.setText("");
        vista.txtNombre.setText("");
        vista.txtApellido.setText("");
        vista.txtSexo.setText("");
        vista.txtFechaN.setText("");
        vista.txtPaisN.setText("");
        vista.txtPaisR.setText("");
        vista.txtIdioma.setText("");
        // Deseleccionar la fila de la tabla si aplica
        vista.tablaUsuarios.clearSelection();
    }

    /**
     * Implementa la lógica de selección de fila usando el método genérico del BaseController.
     */
    private void cargarDetalleFilaSeleccionada(javax.swing.event.ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && vista.tablaUsuarios.getSelectedRow() != -1 && this.rawModel != null) {

            int selectedRow = vista.tablaUsuarios.getSelectedRow();

            // 1. Mapeo de componentes simples
            Map<String, Object> componentMappings = new HashMap<>();
            componentMappings.put("email", vista.txtEmail);
            componentMappings.put("nombres", vista.txtNombre);
            componentMappings.put("apellidos", vista.txtApellido);
            componentMappings.put("sexo", vista.txtSexo);
            componentMappings.put("paisNacimiento", vista.txtPaisN);
            componentMappings.put("paisResidencia", vista.txtPaisR);
            componentMappings.put("idioma", vista.txtIdioma);

            // Usamos el método genérico para cargar los campos
            loadTableDetailsToView(e, componentMappings);

            try {
                // 2. Manejo de campos especiales (ID, Password, Fechas)

                // Obtener ID (necesario para CRUD)
                int col_id = rawModel.findColumn("idUsuario");
                int idUsuario = (int) rawModel.getValueAt(selectedRow, col_id);
                modelo.setId(idUsuario);

                // Cargar Contraseña (solo marcador y valor hasheado para el modelo)
                vista.passField.setText("********");
                int col_password = rawModel.findColumn("password");
                String hashedPassword = rawModel.getValueAt(selectedRow, col_password).toString();
                modelo.setPassword(hashedPassword);

                // Cargar Fechas y manejo de LocalDateTime
                int col_fechaN = rawModel.findColumn("fechaNacimiento");
                int col_fechaR = rawModel.findColumn("fechaRegistro");

                String fechaNacStr = rawModel.getValueAt(selectedRow, col_fechaN).toString();
                String fechaRegStr = rawModel.getValueAt(selectedRow, col_fechaR).toString();

                // Lógica de limpieza y corte para parsear a LocalDate
                fechaNacStr = fechaNacStr.trim().replaceAll("\"", "");
                fechaRegStr = fechaRegStr.trim().replaceAll("\"", "");

                if (fechaNacStr.length() > 10) fechaNacStr = fechaNacStr.substring(0, 10);
                if (fechaRegStr.length() > 10) fechaRegStr = fechaRegStr.substring(0, 10);

                // Parsear a LocalDate
                LocalDate fechaNacimiento = LocalDate.parse(fechaNacStr);
                LocalDate fechaRegistroLocal = LocalDate.parse(fechaRegStr);

                // Mostrar en la vista (asumiendo que tienes txtFechaR)
                vista.txtFechaN.setText(fechaNacimiento.toString());
                vista.txtFechaR.setText(fechaRegistroLocal.toString()); // Descomenta si tienes este campo

                // Cargar al modelo (LocalDateTime requerido para fechaRegistro)
                modelo.setFechaNacimiento(fechaNacimiento);
                modelo.setFechaRegistro(fechaRegistroLocal.atStartOfDay());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al cargar datos de fecha/ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("❌ ERROR al cargar detalles de la fila: " + ex.getMessage());
            }
        }
    }


    @Override
    protected void agregarListeners() {
        // Listener de la tabla para cargar datos
        vista.tablaUsuarios.getSelectionModel().addListSelectionListener(this::cargarDetalleFilaSeleccionada);

        // Listeners CRUD
        vista.btnAgregar.addActionListener(e -> {
            registrar();
            cargarTablaUsuarios(); // Refrescar la tabla después del CRUD
        });
        vista.btnModificar.addActionListener(e -> {
            modificar();
            cargarTablaUsuarios(); // Refrescar la tabla después del CRUD
        });
        vista.btnEliminar.addActionListener(e -> {
            eliminar();
            cargarTablaUsuarios(); // Refrescar la tabla después del CRUD
        });
        vista.btnLimpiar.addActionListener(e -> clearViewFields());
        vista.regresarAlMenuButton.addActionListener(e -> regresarAlMenu());
    }
}