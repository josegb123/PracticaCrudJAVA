package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.InterpreteDAO;
import com.softly.fonoteca.Modelos.DTOs.ComboBoxItem;
import com.softly.fonoteca.Modelos.DTOs.Interprete;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.InterpretesVista;
import com.softly.fonoteca.utilities.FormatDates;
import com.softly.fonoteca.utilities.SQLQuerys;

import javax.swing.*;
import java.time.LocalDate;

public class InterpreteController extends BaseController<Interprete, InterpretesVista, InterpreteDAO>{
    public InterpreteController(Interprete modelo, InterpretesVista vista, InterpreteDAO consultas, BaseView vistaPrincipal) {
        super(modelo, vista, consultas, vistaPrincipal);
        agregarListeners();
    }

    @Override
    protected int getModelId() {
        return Integer.parseInt(vista.txtID.getText());
    }

    @Override
    protected boolean collectDataFromView() throws Exception {
        try {
            LocalDate fechaLanzamiento = FormatDates.getFormatDate(vista.txtYearLanzamiento.getText());
            LocalDate fechaRetiro = FormatDates.getFormatDate(vista.txtYearRetiro.getText());

            modelo.setIdInterprete(Integer.parseInt(vista.txtID.getText()));
            modelo.setNombre(vista.txtTitulo.getText());
            modelo.setYearLanzamiento(fechaLanzamiento);
            modelo.setYearRetiro(fechaRetiro);
            modelo.setTituloInterprete(vista.txtTitulo.getText());
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
    protected void loadDataToView(Interprete interpreteEncontrado) {

        this.modelo.setIdInterprete(interpreteEncontrado.getIdInterprete());
        this.modelo.setTituloInterprete(interpreteEncontrado.getTituloInterprete());
        this.modelo.setYearLanzamiento(interpreteEncontrado.getYearLanzamiento());
        this.modelo.setYearRetiro(interpreteEncontrado.getYearRetiro());
        this.modelo.setIdGeneroPrincipal(interpreteEncontrado.getIdGeneroPrincipal());

        vista.txtTitulo.setText(interpreteEncontrado.getTituloInterprete());
        vista.txtNombre.setText(interpreteEncontrado.getNombre());
        vista.txtID.setText(String.valueOf(interpreteEncontrado.getIdInterprete()));
        vista.txtYearLanzamiento.setText(interpreteEncontrado.getYearLanzamiento().toString());
        vista.txtYearRetiro.setText(interpreteEncontrado.getYearRetiro().toString());

        SQLQuerys.setSelectedItemById(vista.cmbGenero, interpreteEncontrado.getIdGeneroPrincipal());
    }

    @Override
    protected void clearViewFields() {
        vista.txtID.setText("");
        vista.txtNombre.setText("");
        vista.txtTitulo.setText("");
        vista.txtYearLanzamiento.setText("");
        vista.txtYearRetiro.setText("");
        vista.cmbGenero.setSelectedIndex(0);
        vista.txtSearch.setText("");
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
