package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.GeneroDAO;
import com.softly.fonoteca.Modelos.DTOs.Genero;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.GenerosVista;

public class GenerosController extends BaseController<Genero, GenerosVista, GeneroDAO>{
    public GenerosController(Genero modelo, GenerosVista vista, GeneroDAO consultas, BaseView vistaPrincipal) {
        super(modelo, vista, consultas, vistaPrincipal);
        agregarListeners();
    }

    @Override
    protected int getModelId() {
        return Integer.parseInt(vista.getSearchText());
    }

    @Override
    protected boolean collectDataFromView() throws Exception {
        try {
            modelo.setIdGenero(Integer.parseInt(vista.txtIdGenero.getText()));
            modelo.setNombre(vista.txtNombre.getText());
            modelo.setDescripcion(vista.txtDescripcion.getText());
            return true;
        } catch (Exception e) {
            System.err.println(STR."Error \{e.getMessage()}");
            return false;
        }
    }

    @Override
    protected void loadDataToView(Genero generoEncontrado) {
        modelo.setIdGenero(generoEncontrado.getIdGenero());
        modelo.setNombre(generoEncontrado.getNombre());
        modelo.setDescripcion(generoEncontrado.getDescripcion());

        vista.txtIdGenero.setText(String.valueOf(generoEncontrado.getIdGenero()));
        vista.txtNombre.setText(generoEncontrado.getNombre());
        vista.txtDescripcion.setText(generoEncontrado.getDescripcion());
    }

    @Override
    protected void clearViewFields() {

        vista.txtIdGenero.setText("");
        vista.txtNombre.setText("");
        vista.txtDescripcion.setText("");
    }

    @Override
    protected void agregarListeners() {

        vista.agregarButton.addActionListener(e->registrar());
        vista.eliminarButton.addActionListener(e->eliminar());
        vista.modificarButton.addActionListener(e->modificar());
        vista.buscarButton.addActionListener(e->buscarRegistroPorId(getModelId()));
        vista.limpiarCamposButton.addActionListener(e->clearViewFields());
        vista.regresarButton.addActionListener(e->regresarAlMenu());
    }
}
