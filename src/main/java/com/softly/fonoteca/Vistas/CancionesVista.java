package com.softly.fonoteca.Vistas;

import javax.swing.*;

public class CancionesVista extends JFrame implements CRUDView {
    public JPanel contentPane;
    public JTextField txtIDCancion;
    public JTextField txtTitulo;
    public JTextField txtDuracion;
    public JTextField txtBPM;
    public JTextField txtIdiomaCancion;
    public JTextField txtFechaLanzamiento;
    public JCheckBox esInstrumentalCheckBox;
    public JTextField txtBuscarCancion;
    public JButton btnAgregar;
    public JButton modificarButton;
    public JButton eliminarButton;
    public JButton limpiarButton;
    
    public JComboBox cmbAlbum;
    public JComboBox cmbGenero;
    public JComboBox cmbInterprete;
    public JButton regresarAlMenuButton;
    public JButton administarAlbumnesButton;
    public JButton administrarGenerosButton;
    public JButton administrarInterpretesButton;
    public JTable tablaCanciones;


    public CancionesVista() {
        super.setContentPane(contentPane);
    }

    @Override
    public JButton getBtnAgregar() {
        return btnAgregar;
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
    public JButton getBtnRegresarMenu() {
        return regresarAlMenuButton;
    }

    @Override
    public JButton getBtnLimpiar() {
        return limpiarButton;
    }

    @Override
    public String getSearchText() {
        return txtBuscarCancion.getText();
    }
}
