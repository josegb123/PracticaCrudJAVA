package com.softly.fonoteca;


import com.softly.fonoteca.Controladores.LoginController;
import com.softly.fonoteca.Modelos.DAOs.UsuarioDAO;
import com.softly.fonoteca.Vistas.LoginVista;

public class Fonoteca {

    public static void main(String[] args) {

        LoginController controller = new LoginController(new LoginVista(),new UsuarioDAO());
        controller.iniciar();
    }
}
