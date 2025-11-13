package com.softly.fonoteca.Vistas;

import javax.swing.*;
import java.awt.*;

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
    public JTable tablaGeneros;

    public GenerosVista(){
        super.setContentPane(contentPane);
        Color colorDelTema = UIManager.getColor("MenuBar.borderColor");
        txtDescripcion.setBorder(BorderFactory.createLineBorder(colorDelTema, 1));
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
