package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.UsuarioDAO;
import com.softly.fonoteca.Modelos.DTOs.Usuario;
import com.softly.fonoteca.Vistas.LoginVista;
import com.softly.fonoteca.Vistas.MainView;
import com.softly.fonoteca.utilities.SecurityUtils;
import java.util.Arrays; // Necesario para Arrays.fill

public class LoginController {

    private final LoginVista vista;
    private final UsuarioDAO consultas;
    // MainController no debe ser una propiedad del LoginController, se crea y se delega.

    public LoginController(LoginVista vista, UsuarioDAO consultas) {
        this.vista = vista;
        this.consultas = consultas;
        agregarListeners();
    }

    // --- Lógica de Servicio (Simulada aquí, idealmente en AuthService) ---
    /**
     * Verifica las credenciales contra el hash BCrypt almacenado.
     * Recibe la contraseña como char[] para seguridad de memoria.
     * @param email Email del usuario.
     * @param passwordChars Contraseña en texto plano como char[].
     * @return El objeto Usuario si la autenticación es exitosa, null en caso contrario.
     */
    protected Usuario verificarSesion(String email, char[] passwordChars) {

        Usuario usuario = consultas.buscarPorEmail(email);

        if(usuario == null) {
            return null;
        }

        String storedHashedPassword = usuario.getHashedPassword();

        // Llamada directa a SecurityUtils con el char[], eliminando la String insegura.
        boolean isPasswordValid = SecurityUtils.verifyPassword(passwordChars, storedHashedPassword);

        // System.out.println(isPasswordValid); // Se recomienda eliminar logs de seguridad en producción

        return isPasswordValid ? usuario : null;
    }
    // --------------------------------------------------------------------


    protected void iniciarSesion() throws Exception {
        String email = vista.getTxtEmail().getText();
        char[] passwdChars = vista.getTxtPasswd().getPassword(); // Contraseña como char[]

        Usuario usuarioAutenticado = null;

        try {
            // Se llama a verificarSesion directamente con el char[]
            usuarioAutenticado = verificarSesion(email, passwdChars);

            if(usuarioAutenticado != null) {
                // Lógica de navegación
                MainView mainVista = new MainView();
                MainController mainInicio = new MainController(mainVista);
                // Si el MainController necesita el usuario, habría que pasárselo: mainInicio = new MainController(mainVista, usuarioAutenticado);
                mainInicio.iniciar();
                vista.dispose(); // Cierra o esconde la vista de Login
            } else {
                vista.setMessageAlert("Datos invalidos, intente de nuevo");
            }
        } catch (Exception e) {
            // Manejo de excepciones de DB o errores criptográficos
            vista.setMessageAlert("Error interno del sistema durante la autenticación.");
            throw e;
        } finally {
            // Borrado seguro del array de caracteres, ejecutado siempre
            Arrays.fill(passwdChars, ' ');
        }
    }

    // Métodos auxiliares y de listeners sin cambios
    public void iniciar(){
        vista.setVisible(true);
        vista.setLocationRelativeTo(null);
    }

    protected void agregarListeners() {
        vista.getIniciarSesionButton().addActionListener(_-> {
            try {
                iniciarSesion();
            } catch (Exception e) {
                // Manejo de errores de Runtime para la UI
                System.err.println("Error al iniciar sesión: " + e.getMessage());
                // No propagar la excepción checked a través de RuntimeException en producción sin logging adecuado.
                throw new RuntimeException(e);
            }
        });
    }
}