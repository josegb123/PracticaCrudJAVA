package com.softly.fonoteca.Vistas;

import javax.swing.*;

public class InterpretesVista extends JFrame implements CRUDView{
    public JPanel contentPane;
    public JTextField txtID;
    public JTextField txtNombre;
    public JTextField txtYearLanzamiento;
    public JTextField txtYearRetiro;
    public JTextField txtTitulo;
    public JComboBox cmbGenero;
    public JTextField txtSearch;
    public JButton buscarButton;
    public JButton agregarButton;
    public JButton modificarButton;
    public JButton eliminarButton;
    public JButton limpiarCamposButton;
    public JButton regresarButton;
    public JTable tablaInterpretes;

    public InterpretesVista(){
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
