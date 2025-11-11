package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DTOs.Cancion;
import com.softly.fonoteca.Modelos.DAOs.CancionDAO;
import com.softly.fonoteca.Modelos.DTOs.Usuario;
import com.softly.fonoteca.Modelos.DAOs.UsuarioDAO;
import com.softly.fonoteca.Vistas.CancionesVista;
import com.softly.fonoteca.Vistas.MainView;
import com.softly.fonoteca.Vistas.UsuariosVista;

public class MainController {

    private final MainView vistaPrincipal;

    public MainController(MainView vistaPrincipal) {
        this.vistaPrincipal = vistaPrincipal;
        agregarListeners();
    }

    private void agregarListeners() {
        this.vistaPrincipal.cancionesButton.addActionListener(e -> launchCancionModule());
        this.vistaPrincipal.usuariosButton.addActionListener(e -> launchUsuariosModule());
        this.vistaPrincipal.salirButton.addActionListener(e-> this.vistaPrincipal.dispose());
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

    public void iniciar() {
        vistaPrincipal.setLocationRelativeTo(null);
        vistaPrincipal.setVisible(true);
    }
}
