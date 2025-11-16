package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.CalificacionDAO;
import com.softly.fonoteca.Modelos.DAOs.ReproduccionDAO;
import com.softly.fonoteca.Modelos.DTOs.Calificacion;
import com.softly.fonoteca.Modelos.DTOs.Cancion;
import com.softly.fonoteca.Modelos.DAOs.CancionDAO;
import com.softly.fonoteca.Modelos.DTOs.Reproduccion;
import com.softly.fonoteca.Modelos.DTOs.Usuario;
import com.softly.fonoteca.Modelos.DAOs.UsuarioDAO;
import com.softly.fonoteca.Vistas.*;

public class MainController {

    private final MainView vistaPrincipal;
    private final LoginVista login;

    public MainController(MainView vistaPrincipal, LoginVista login) {
        this.vistaPrincipal = vistaPrincipal;
        this.login = login;
        agregarListeners();
    }

    private void agregarListeners() {
        this.vistaPrincipal.cancionesButton.addActionListener(e -> launchCancionModule());
        this.vistaPrincipal.usuariosButton.addActionListener(e -> launchUsuariosModule());
        this.vistaPrincipal.reproduccionesButton.addActionListener(e ->launchReproduccionesModule());
        this.vistaPrincipal.calificacionesButton.addActionListener(e ->launchComentariosModule());
        this.vistaPrincipal.salirButton.addActionListener(e -> salir());
    }

    private void salir(){
        this.vistaPrincipal.dispose();
        login.setVisible(true);
    }
    private void launchReproduccionesModule() {
        this.vistaPrincipal.setVisible(false);

        Reproduccion modeloReproduccion = new Reproduccion();
        ReproduccionDAO consultasReproduccion = new ReproduccionDAO();
        ReproduccionesVista vistaReproduccion = new ReproduccionesVista();

        ReproduccionesController reproduccionController = new ReproduccionesController(modeloReproduccion, vistaReproduccion,consultasReproduccion ,this.vistaPrincipal);
        reproduccionController.iniciar();
    }

    private void launchCancionModule() {

        this.vistaPrincipal.setVisible(false);

        Cancion modeloCancion = new Cancion();
        CancionDAO consultasCancion = new CancionDAO();
        CancionesVista vistaCancion = new CancionesVista();

        CancionController cancionController = new CancionController(modeloCancion, vistaCancion,consultasCancion ,this.vistaPrincipal);
        cancionController.iniciar();
    }

    private void launchUsuariosModule() {

        this.vistaPrincipal.setVisible(false);

        Usuario modeloUsuario = new Usuario();
        UsuarioDAO consultasUsuario = new UsuarioDAO();
        UsuariosVista vistaUsuarios = new UsuariosVista();

        UsuarioController usuarioController = new UsuarioController(modeloUsuario, vistaUsuarios, consultasUsuario,this.vistaPrincipal);
        usuarioController.iniciar();

    }

    private void launchComentariosModule() {
        this.vistaPrincipal.setVisible(false);

        Calificacion modeloCalificacion = new Calificacion();
        CalificacionDAO consultasCalificacion = new CalificacionDAO();
        CalificacionesVista vistaCalificacions = new CalificacionesVista();

        CalificacionesController calificacionController = new CalificacionesController(modeloCalificacion, vistaCalificacions, consultasCalificacion,this.vistaPrincipal);
        calificacionController.iniciar();
    }

    public void iniciar() {
        vistaPrincipal.setLocationRelativeTo(null);
        vistaPrincipal.setVisible(true);
    }
}
