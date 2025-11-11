package com.softly.fonoteca;

import com.softly.fonoteca.Controladores.MainController;
import com.softly.fonoteca.Vistas.MainView;

public class Fonoteca {

    public static void main(String[] args) {

        MainController controller = new MainController(new MainView());
        controller.iniciar();
    }
}
