package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.UsuarioDAO;
import com.softly.fonoteca.Modelos.DTOs.Usuario;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.UsuariosVista;
import com.softly.fonoteca.utilities.FormatDates;
import com.softly.fonoteca.utilities.SecurityUtils;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Arrays;

public class UsuarioController extends BaseController<Usuario, UsuariosVista, UsuarioDAO> {


    public UsuarioController(Usuario modelo, UsuariosVista vista, UsuarioDAO consultas, BaseView vistaPrincipal) {
        super(modelo, vista, consultas, vistaPrincipal);
    }

    @Override
    protected int getModelId() {
        return Integer.parseInt(vista.txtSearch.getText());
    }

    @Override
    protected boolean collectDataFromView() {
        try {


            // 2. Recolectar datos de la vista y actualizar el modelo (incluyendo el ID)
            LocalDate fechaNacimiento = FormatDates.getFormatDate(vista.txtFechaN.getText());

            // Manejo de la contraseña: Si el campo está vacío, asumimos que no se quiere cambiar la contraseña
            String nuevaPassword = Arrays.toString(vista.passField.getPassword());
            if (!nuevaPassword.isEmpty()) {
                modelo.setPassword(SecurityUtils.hashPassword(nuevaPassword));
            }
            // Si la contraseña está vacía, mantenemos la contraseña anterior del modelo

            modelo.setEmail(vista.txtEmail.getText());
            modelo.setNombres(vista.txtNombre.getText());
            modelo.setApellidos(vista.txtApellido.getText());
            modelo.setSexo(vista.txtSexo.getText());
            modelo.setFechaNacimiento(fechaNacimiento);
            modelo.setPaisNacimiento(vista.txtPaisN.getText());
            modelo.setPaisResidencia(vista.txtPaisR.getText());
            modelo.setIdioma(vista.txtIdioma.getText());
            // NO actualizar la fechaRegistro aquí, ya que es la fecha original.
            return true;

        } catch (Exception e) {
            System.err.println(STR."Error de conversión de números o IDs: \{e.getMessage()}");
            JOptionPane.showMessageDialog(vista, STR."Error interno de formato de datos\{e.getMessage()}", "Error Crítico", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    @Override
    protected void loadDataToView(Usuario usuarioEncontrado) {
        modelo.setId(usuarioEncontrado.getId());
        modelo.setEmail(usuarioEncontrado.getEmail());
        modelo.setPassword(usuarioEncontrado.getPassword());
        modelo.setNombres(usuarioEncontrado.getNombres());
        modelo.setApellidos(usuarioEncontrado.getApellidos());
        modelo.setSexo(usuarioEncontrado.getSexo());
        modelo.setFechaNacimiento(usuarioEncontrado.getFechaNacimiento());
        modelo.setPaisNacimiento(usuarioEncontrado.getPaisNacimiento());
        modelo.setPaisResidencia(usuarioEncontrado.getPaisResidencia());
        modelo.setIdioma(usuarioEncontrado.getIdioma());
        modelo.setFechaRegistro(usuarioEncontrado.getFechaRegistro());

        // 3. Mostrar los datos en los campos de la vista
        vista.txtEmail.setText(usuarioEncontrado.getEmail());
        vista.passField.setText("********");
        vista.txtNombre.setText(usuarioEncontrado.getNombres());
        vista.txtApellido.setText(usuarioEncontrado.getApellidos());
        vista.txtSexo.setText(usuarioEncontrado.getSexo());
        vista.txtFechaN.setText(usuarioEncontrado.getFechaNacimiento().toString());
        vista.txtPaisN.setText(usuarioEncontrado.getPaisNacimiento());
        vista.txtPaisR.setText(usuarioEncontrado.getPaisResidencia());
        vista.txtIdioma.setText(usuarioEncontrado.getIdioma());
    }

    @Override
    protected void clearViewFields() {
        vista.txtEmail.setText("");
        vista.passField.setText("");
        vista.txtNombre.setText("");
        vista.txtApellido.setText("");
        vista.txtSexo.setText("");
        vista.txtFechaN.setText("");
        vista.txtPaisN.setText("");
        vista.txtPaisR.setText("");
        vista.txtIdioma.setText("");
    }


    @Override
    protected void agregarListeners() {
        vista.btnAgregar.addActionListener(e -> registrar());
        vista.regresarAlMenuButton.addActionListener(e -> regresarAlMenu());
        vista.buscarButton.addActionListener(e -> buscarRegistroPorId(getModelId()));
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnModificar.addActionListener(e -> modificar());
        vista.btnLimpiar.addActionListener(e-> clearViewFields());
    }
}