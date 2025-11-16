package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.UsuarioDAO;
import com.softly.fonoteca.Modelos.DTOs.Usuario;
import com.softly.fonoteca.Vistas.LoginVista;
import com.softly.fonoteca.Vistas.MainView;
import com.softly.fonoteca.Vistas.RegistroVista;
import com.softly.fonoteca.utilities.SecurityUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LoginController {

    private final LoginVista vista;
    private final UsuarioDAO consultas;

    public LoginController(LoginVista vista, UsuarioDAO consultas) {
        this.vista = vista;
        this.consultas = consultas;
    }

    /**
     * Verifica las credenciales contra el hash BCrypt almacenado.
     * Recibe la contraseña como char[] para seguridad de memoria.
     *
     * @param email         Email del usuario.
     * @param passwordChars Contraseña en texto plano como char[].
     * @return El objeto Usuario si la autenticación es exitosa, null en caso contrario.
     */
    protected Usuario verificarSesion(String email, char[] passwordChars) {

        Usuario usuario = consultas.buscarPorEmail(email);

        if (usuario == null) {
            return null;
        }

        String storedHashedPassword = usuario.getHashedPassword();

        boolean isPasswordValid = SecurityUtils.verifyPassword(passwordChars, storedHashedPassword);

        return isPasswordValid ? usuario : null;
    }

    protected void iniciarSesion() {
        vista.setMessageAlert("");
        String email = vista.getTxtEmail().getText();
        char[] passwdChars = vista.getTxtPasswd().getPassword();
        Usuario usuarioAutenticado;

        try {
            usuarioAutenticado = verificarSesion(email, passwdChars);

            if (usuarioAutenticado != null) {
                MainView mainVista = new MainView();
                MainController mainInicio = new MainController(mainVista,this.vista);
                mainInicio.iniciar();
                this.vista.setVisible(false);
            } else {
                vista.setMessageAlert("Datos invalidos, intente de nuevo");
            }
        } catch (Exception e) {
            vista.setMessageAlert("Error interno del sistema durante la autenticación.");
            System.err.println("Error al iniciar sesión: " + e.getMessage());
        } finally {
            Arrays.fill(passwdChars, ' ');
        }
    }

    /**
     * Inicializa y muestra la ventana de Registro (JDialog).
     */
    protected void iniciarRegistro() {
        try {
            RegistroVista registroVista = new RegistroVista(this.vista);
            Usuario nuevoUsuario = new Usuario();

            RegisterController registerController =
                    new RegisterController(nuevoUsuario, registroVista, this.consultas);

            registerController.iniciar();
            registroVista.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al iniciar la ventana de registro: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error al invocar el registro: " + e.getMessage());
        }
    }

    public void iniciar() {
        vista.setContentPane(vista.getContentPane());
        vista.pack();
        vista.setVisible(true);
        vista.setLocationRelativeTo(null);
        vista.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        agregarListeners();
    }

    protected void agregarListeners() {
        vista.getIniciarSesionButton().addActionListener(e -> iniciarSesion());
        vista.getRegisterButton().addActionListener(e -> iniciarRegistro());
    }
}