package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.AlbumDAO;
import com.softly.fonoteca.Modelos.DTOs.Album;
import com.softly.fonoteca.Modelos.DTOs.ComboBoxItem;
import com.softly.fonoteca.Vistas.AlbumVista;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.utilities.FormatDates;
import com.softly.fonoteca.utilities.SQLQuerys;

import javax.swing.*;
import java.time.LocalDate;

public class AlbumController extends BaseController<Album, AlbumVista, AlbumDAO> {
    public AlbumController(Album modelo, AlbumVista vista, AlbumDAO consultas, BaseView vistaPrincipal) {
        super(modelo, vista, consultas, vistaPrincipal);
        vista.cmbGenero.setModel(SQLQuerys.consultarDatos("generos", "idGenero", "nombre"));

    }

    @Override
    protected int getModelId() {
        return Integer.parseInt(vista.txtSearch.getText());
    }

    @Override
    protected boolean collectDataFromView() throws Exception {
        try {
            LocalDate fechaLanzamiento = FormatDates.getFormatDate(vista.txtFecha.getText());

            modelo.setTitulo(vista.txtTitulo.getText());
            modelo.setSelloDiscografico(vista.txtSello.getText());
            modelo.setFechaLanzamiento(fechaLanzamiento);
            ComboBoxItem generoSeleccionado = (ComboBoxItem) vista.cmbGenero.getSelectedItem();
            modelo.setIdGeneroPrincipal(generoSeleccionado.getId());
            return true;
        } catch (Exception e) {
            System.err.println(STR."Error: \{e.getMessage()}");
            JOptionPane.showMessageDialog(vista, STR."Error interno\{e.getMessage()}", "Error Crítico", JOptionPane.ERROR_MESSAGE);
            return false;
        }

    }

    @Override
    protected void loadDataToView(Album albumEncontrado) {


        this.modelo.setIdAlbum(albumEncontrado.getIdAlbum());
        this.modelo.setTitulo(albumEncontrado.getTitulo());
        this.modelo.setSelloDiscografico(albumEncontrado.getSelloDiscografico());
        this.modelo.setFechaLanzamiento(albumEncontrado.getFechaLanzamiento());
        this.modelo.setIdGeneroPrincipal(albumEncontrado.getIdGeneroPrincipal());


        // B. Mostrar los datos en los campos de la vista
        vista.txtTitulo.setText(albumEncontrado.getTitulo());
        vista.txtSello.setText(albumEncontrado.getSelloDiscografico());
        vista.txtIdAlbum.setText(String.valueOf(albumEncontrado.getIdAlbum()));
        vista.txtFecha.setText(albumEncontrado.getFechaLanzamiento().toString());
        // Seleccionar Género
        SQLQuerys.setSelectedItemById(vista.cmbGenero, albumEncontrado.getIdGeneroPrincipal());

    }

    @Override
    protected void clearViewFields() {
        vista.txtTitulo.setText("");
        vista.txtSello.setText("");
        vista.txtIdAlbum.setText("");
        vista.txtFecha.setText("");
        vista.cmbGenero.setSelectedIndex(0);
    }

    @Override
    protected void agregarListeners() {
        vista.agregarButton.addActionListener(e->registrar());
        vista.regresarButton.addActionListener(e -> regresarAlMenu());
        vista.buscarButton.addActionListener(e -> buscarRegistroPorId(getModelId()));
        vista.modificarButton.addActionListener(e->modificar());
        vista.eliminarButton.addActionListener(e->eliminar());
        vista.limpiarCamposButton.addActionListener(e->clearViewFields());
    }
}
