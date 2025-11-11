package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.BaseDAO;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.CRUDView;

import javax.swing.*;
import java.awt.*;

/**
 * Plantilla genérica para todos los controladores CRUD.
 *
 * @param <T> El DTO (Usuario, Album, etc.)
 * @param <V> La Vista de la entidad CRUD (UsuariosVista, AlbumVista, etc.)
 * @param <D> El DAO (UsuarioDAO, AlbumDAO, etc.)
 */
public abstract class BaseController<T, V extends Component & CRUDView, D extends BaseDAO<T>> {

    protected final T modelo;
    protected final V vista;
    protected final D consultas;
    // CORRECCIÓN 1: La vista principal (MainView) es de un tipo genérico que implementa BaseView,
    // pero no necesariamente CRUDView. Usaremos BaseView.
    protected final BaseView vistaPrincipal;

    // CORRECCIÓN 2: Cambiamos el tipo del constructor de V a BaseView para vistaPrincipal
    public BaseController(T modelo, V vista, D consultas, BaseView vistaPrincipal) {
        this.modelo = modelo;
        this.vista = vista;
        this.consultas = consultas;
        this.vistaPrincipal = vistaPrincipal;
        agregarListeners(); // Se llama una vez
    }

    protected abstract int getModelId();

    // --- MÉTODOS ABSTRACTOS REQUERIDOS (Se mantienen igual) ---

    /**
     * Mapea los datos de los campos de la Vista al objeto DTO (para INSERT/UPDATE).
     */
    protected abstract boolean collectDataFromView() throws Exception;

    /**
     * Carga los datos del DTO encontrado a los campos de la Vista (para BUSCAR).
     */
    protected abstract void loadDataToView(T dto);

    /**
     * Reinicia los campos del formulario.
     */
    protected abstract void clearViewFields();

    /**
     * Define y agrega todos los ActionListener a los botones de la vista.
     */
    protected abstract void agregarListeners();

    // --- LÓGICA DE NEGOCIO CENTRALIZADA (Se mantiene igual) ---

    protected void registrar() {
        try {
            // 1. Llama al método abstracto para recoger y validar datos específicos del DTO
            if (collectDataFromView()) {

                // 2. Si la recolección es exitosa, llama al DAO (consultas)
                if (consultas.registrar(modelo)) {
                    JOptionPane.showMessageDialog(vista, "Registro exitoso.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    clearViewFields(); // Opcional: Limpia los campos
                } else {
                    JOptionPane.showMessageDialog(vista, "Error: No se pudo registrar. Verifique la consola.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al procesar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void modificar() {
        try {
            // 1. Verifica si el modelo tiene un ID cargado (de una búsqueda previa)
            if (getModelId() <= 0) {
                JOptionPane.showMessageDialog(vista, "Debe buscar un registro antes de modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Llama al método abstracto para recoger y validar datos actualizados
            if (collectDataFromView()) {

                // 3. Si la recolección es exitosa, llama al DAO
                if (consultas.modificar(modelo)) {
                    JOptionPane.showMessageDialog(vista, "Modificación exitosa.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    clearViewFields();
                } else {
                    JOptionPane.showMessageDialog(vista, "Error: No se pudo modificar. Verifique la consola.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al procesar la modificación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void eliminar() {
        // Obtenemos el ID específico usando el método abstracto
        int idAEliminar = getModelId();

        try {
            if (idAEliminar <= 0) {
                JOptionPane.showMessageDialog(vista, "No hay registro seleccionado para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    vista, "¿Está seguro de eliminar el registro con ID " + idAEliminar + "?",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // 1. Llama al DAO usando el ID obtenido
                if (consultas.eliminar(idAEliminar)) {
                    JOptionPane.showMessageDialog(vista, "Registro eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    clearViewFields();
                } else {
                    JOptionPane.showMessageDialog(vista, "Error: No se pudo eliminar. Verifique la consola.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al procesar la eliminación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected T buscarRegistroPorId(int id) {
        T encontrado = consultas.buscarPorId(id);
        if (encontrado != null) {
            loadDataToView(encontrado);
            JOptionPane.showMessageDialog(vista, "Registro encontrado con ID: " + id, "Búsqueda Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vista, "Registro con ID " + id + " no encontrado.", "No Encontrado", JOptionPane.WARNING_MESSAGE);
        }
        return encontrado;
    }

    protected void regresarAlMenu() {
        this.vista.setVisible(false);
        this.vistaPrincipal.setVisible(true); // Ambos usan setVisible de BaseView
    }

    // CORRECCIÓN 3: Implementación completa del método iniciar() usando los métodos de BaseView
    public void iniciar() {
        // Usa los métodos de BaseView para configurar y mostrar la ventana
        this.vista.pack();
        this.vista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Nota: setLocationRelativeTo(null) no está en tu BaseView, si lo necesitas agrégalo
        // La vista de Swing generalmente es un JFrame/JPanel que tiene un método pack()
        this.vista.setVisible(true);
    }
}