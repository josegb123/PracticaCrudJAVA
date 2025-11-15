package com.softly.fonoteca.Vistas;

import javax.swing.*;

public interface CRUDView extends BaseView {
    // Elementos de la interfaz de usuario que el Controlador necesita para la lógica
    JButton getBtnAgregar();
    JButton getBtnModificar();
    JButton getBtnEliminar();
    JButton getBtnRegresarMenu();
    JButton getBtnLimpiar();

    String getSearchText(); // Un método que devuelve el contenido del txtSearch

}
