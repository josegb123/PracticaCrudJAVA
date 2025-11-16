package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.UsuarioDAO;
import com.softly.fonoteca.Modelos.DTOs.Usuario;
import com.softly.fonoteca.Vistas.RegistroVista;
import com.softly.fonoteca.utilities.FormatDates;
import com.softly.fonoteca.utilities.SecurityUtils;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

/**
 * Controlador específico para el flujo de registro de nuevos usuarios.
 * Utiliza lógica de hashing y validación similar a UsuarioController.
 */
public class RegisterController {

    private final Usuario modelo;
    private final RegistroVista vista;
    private final UsuarioDAO consultas;

    public RegisterController(Usuario modelo, RegistroVista vista, UsuarioDAO consultas) {
        this.modelo = modelo;
        this.vista = vista;
        this.consultas = consultas;
        agregarListeners();
        cargarDatos();
    }

    private void cargarDatos() {
        DefaultComboBoxModel sexo = new DefaultComboBoxModel<>(new String[] {"Masculino","Femenino","Otro"});
        vista.cmbSexo.setModel(sexo);
    }

    /**
     * Intenta recolectar los datos de la vista, validarlos y hacer el hashing de la contraseña.
     *
     * @return true si los datos son válidos y listos para ser guardados.
     */
    private boolean collectDataFromView() {
        char[] passwordChars = null;

        try {
            // 1. Validar campos mínimos requeridos
            if (vista.txtEmail.getText().trim().isEmpty() || vista.txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El Email y Nombre son campos requeridos.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            passwordChars = vista.passField.getPassword();
            char[] confirmPassChars = vista.passConfirmField.getPassword();

            // 2. Validación y Hashing de Contraseña
            if (passwordChars.length == 0) {
                JOptionPane.showMessageDialog(vista, "La contraseña es requerida para el registro.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (!Arrays.equals(passwordChars, confirmPassChars)) {
                JOptionPane.showMessageDialog(vista, "Las contraseñas no coinciden.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Generar hash BCrypt
            String nuevoHash = SecurityUtils.hashPassword(passwordChars);
            modelo.setHashedPassword(nuevoHash);

            // 3. Recolectar datos restantes

            String fechaNacStr = vista.txtDateBirth.getText();
            LocalDate fechaNacimiento = FormatDates.getFormatDate(fechaNacStr);

            modelo.setEmail(vista.txtEmail.getText());
            modelo.setNombres(vista.txtNombre.getText());
            modelo.setApellidos(vista.txtApellidos.getText());
            modelo.setSexo(Objects.requireNonNull(vista.cmbSexo.getSelectedItem()).toString());
            modelo.setFechaNacimiento(fechaNacimiento);
            modelo.setPaisNacimiento(vista.txtPaisBorn.getText());
            modelo.setPaisResidencia(vista.txtPaisResidence.getText());
            modelo.setIdioma(vista.txtIdioma.getText());

            // Asignar fecha de registro actual (la hora se maneja a nivel de DB o DTO)
            modelo.setFechaRegistro(LocalDate.now().atStartOfDay());

            return true;

        } catch (Exception e) {
            System.err.println("Error de formato de fecha o datos: " + e.getMessage());
            JOptionPane.showMessageDialog(vista, "Error al procesar datos: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            // Borrado seguro de memoria de las contraseñas
            if (passwordChars != null) Arrays.fill(passwordChars, ' ');
            if (passwordChars != null)
                Arrays.fill(passwordChars, ' '); // Suponiendo que confirmPassChars se asigna al mismo array para limpieza.
            // Si confirmPassChars fuera diferente, se limpiaría también.
        }
    }

    /**
     * Ejecuta el proceso de registro en la DB.
     */
    private void registrarUsuario() {
        if (!collectDataFromView()) return;

        // Se usa el método registrar() del DAO
        if (consultas.registrar(modelo)) {
            JOptionPane.showMessageDialog(vista, "Usuario registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cerrarVista();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al registrar el usuario. Verifique el log de la base de datos.", "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarListeners() {
        vista.registrarButton.addActionListener(e -> registrarUsuario());
        vista.buttonCancel.addActionListener(e -> cerrarVista());
    }

    private void cerrarVista() {
        vista.dispose();
    }

    /**
     * Inicializa y muestra la vista de Registro.
     */
    public void iniciar() {
        vista.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        vista.setVisible(true);
    }
}
