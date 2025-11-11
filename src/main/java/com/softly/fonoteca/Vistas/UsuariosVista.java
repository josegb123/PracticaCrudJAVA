package com.softly.fonoteca.Vistas;

import javax.swing.*;

public class UsuariosVista extends JFrame implements CRUDView {
    public JPanel contentPane;
    public JPasswordField passField;
    public JTextField txtApellido;
    public JTextField txtSexo;
    public JTextField txtPaisN;
    public JTextField txtPaisR;
    public JTextField txtNombre;
    public JTextField txtIdioma;
    public JTextField txtFechaN;
    public JTextField txtFechaR;
    public JTextField txtSearch;
    public JButton buscarButton;
    public JButton btnAgregar;
    public JButton btnModificar;
    public JButton btnEliminar;
    public JButton btnLimpiar;
    public JTextField txtEmail;
    public JButton regresarAlMenuButton;


    public UsuariosVista(){
        super.setContentPane(contentPane);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.pack();
    }


    @Override
    public JButton getBtnAgregar() {
        return btnAgregar;
    }

    @Override
    public JButton getBtnModificar() {
        return btnModificar;
    }

    @Override
    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    @Override
    public JButton getBuscarButton() {
        return buscarButton;
    }

    @Override
    public JButton getBtnRegresarMenu() {
        return regresarAlMenuButton;
    }

    @Override
    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }

    @Override
    public String getSearchText() {
        return txtSearch.getText();
    }
}
