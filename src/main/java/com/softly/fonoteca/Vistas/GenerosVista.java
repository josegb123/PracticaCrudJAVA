package com.softly.fonoteca.Vistas;

import javax.swing.*;

public class GenerosVista extends JFrame implements CRUDView{
    public JPanel contentPane;
    public JTextField txtIdGenero;
    public JTextField txtNombre;
    public JTextArea txtDescripcion;
    public JTextField txtSearch;
    public JButton agregarButton;
    public JButton modificarButton;
    public JButton eliminarButton;
    public JButton limpiarCamposButton;
    public JButton buscarButton;
    public JButton regresarButton;

    public GenerosVista(){
        super.setContentPane(contentPane);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.pack();
        super.setLocationRelativeTo(null);
    }

    @Override
    public JButton getBtnAgregar() {
        return agregarButton;
    }

    @Override
    public JButton getBtnModificar() {
        return modificarButton;
    }

    @Override
    public JButton getBtnEliminar() {
        return eliminarButton;
    }

    @Override
    public JButton getBuscarButton() {
        return buscarButton;
    }

    @Override
    public JButton getBtnRegresarMenu() {
        return regresarButton;
    }

    @Override
    public JButton getBtnLimpiar() {
        return limpiarCamposButton;
    }

    @Override
    public String getSearchText() {
        return txtSearch.getText();
    }
}
