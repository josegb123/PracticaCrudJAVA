package com.softly.fonoteca.Vistas;

import javax.swing.*;

public class AlbumVista extends JFrame implements CRUDView {
    public JTextField txtIdAlbum;
    public JTextField txtTitulo;
    public JTextField txtSello;
    public JTextField txtFecha;
    public JComboBox cmbGenero;
    public JLabel selloDiscograficoLabel;
    public JLabel tituloAlbumLabel;
    public JLabel IDAlbumLabel;
    public JLabel fechaLanzamientoLabel;
    public JLabel generoPrincipalLabel;
    public JPanel contentPane;
    public JLabel albumnesLabel;
    public JButton agregarButton;
    public JButton modificarButton;
    public JButton eliminarButton;
    public JButton limpiarCamposButton;
    public JButton buscarButton;
    public JButton regresarButton;
    public JTextField txtSearch;

    public AlbumVista(){
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
