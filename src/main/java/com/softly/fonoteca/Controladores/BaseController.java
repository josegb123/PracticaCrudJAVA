package com.softly.fonoteca.Controladores;

import com.softly.fonoteca.Modelos.DAOs.BaseDAO;
import com.softly.fonoteca.Vistas.BaseView;
import com.softly.fonoteca.Vistas.CRUDView;
import com.softly.fonoteca.utilities.SQLQuerys;
import com.softly.fonoteca.utilities.TablaUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

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
    protected BaseView vistaPrincipal;

    // 🌟 NUEVO: Variable para almacenar el modelo de datos crudo de la tabla
    protected DefaultTableModel rawModel;

    // 🌟 NUEVO: Referencia a la tabla principal si la vista la tiene.
    // Esto se inicializará en el constructor del controlador hijo.
    protected JTable mainTable;

    // Constructor se mantiene igual
    public BaseController(T modelo, V vista, D consultas, BaseView vistaPrincipal) {
        this.modelo = modelo;
        this.vista = vista;
        this.consultas = consultas;
        this.vistaPrincipal = vistaPrincipal;
        agregarListeners();
    }

    protected abstract int getModelId();

    // --- MÉTODOS ABSTRACTOS REQUERIDOS (Se mantienen igual) ---

    protected abstract boolean collectDataFromView() throws Exception;
    protected abstract void loadDataToView(T dto);
    protected abstract void clearViewFields();
    protected abstract void agregarListeners();

    // ----------------------------------------------------------------------
    // 🌟 LÓGICA DE TABLA GENÉRICA OPCIONAL
    // ----------------------------------------------------------------------

    /**
     * Maneja el evento de selección de fila de una tabla y carga los detalles de la fila
     * en los componentes de la vista usando la utilidad genérica.
     * * Este método debe ser llamado desde el ListSelectionListener en el controlador hijo.
     * Ejemplo en ReproduccionesController:
     * vista.tablaReproducciones.getSelectionModel().addListSelectionListener(this::loadTableDetailsToView);
     * * @param e El evento de selección de lista.
     * @param componentMappings Mapa que relaciona nombres de columna BD con componentes Swing.
     */
    protected void loadTableDetailsToView(ListSelectionEvent e, Map<String, Object> componentMappings) {
        if (!e.getValueIsAdjusting()) {
            JTable table = this.mainTable;
            int selectedRow = table.getSelectedRow();

            // Si no hay tabla principal definida o el rawModel no ha sido cargado, salir.
            if (this.rawModel == null) {
                System.err.println("Advertencia: No se ha cargado el rawModel para el controlador.");
                return;
            }

            try {
                // 1. Usa la utilidad genérica para cargar los JComboBox y JTextField
                TablaUtils.cargarDetalleGenerico(selectedRow, this.rawModel, componentMappings);

                // 2. Aquí el controlador hijo DEBE agregar la lógica para:
                //    a) Obtener los IDs de la fila seleccionada y cargarlos en 'this.modelo'
                //    b) Manejar componentes complejos (como DatePicker/TimePicker)

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al cargar los detalles de la fila: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("❌ ERROR en loadTableDetailsToView: " + ex.getMessage());
            }
        }
    }

    /**
     * Carga el modelo crudo (rawModel) de la BD y lo filtra para mostrar solo
     * las columnas especificadas en el JTable de la vista.
     * * @param tableName Nombre de la tabla de la BD (ej. "usuarios").
     * @param columnsToShow (Opcional) Array de Strings con los nombres de las columnas BD a mostrar.
     * Si es null o vacío, se muestran TODAS las columnas.
     * @param displayColumnNames (Opcional) Array de Strings con los nombres que tendrán las columnas
     * para la cabecera de la tabla. Debe coincidir en longitud con columnsToShow.
     */
    protected void cargarTabla(String tableName, String[] columnsToShow, String[] displayColumnNames) {
        try {
            // 1. Obtener el modelo crudo con TODAS las columnas
            this.rawModel = SQLQuerys.buildTableModel(tableName, new HashMap<>());

            // Si no hay datos, inicializar la tabla con las cabeceras provistas o vacías.
            if (this.rawModel.getRowCount() == 0) {
                String[] headers = (displayColumnNames != null && displayColumnNames.length > 0) ? displayColumnNames : new String[0];
                this.mainTable.setModel(new DefaultTableModel(headers, 0));
                return;
            }

            DefaultTableModel finalModel = new DefaultTableModel();

            // Determinar qué columnas vamos a mostrar y sus cabeceras
            List<String> rawColumnNames = new ArrayList<>();
            List<String> finalHeaders = new ArrayList<>();

            if (columnsToShow == null || columnsToShow.length == 0) {
                // Opción 1: Mostrar TODAS las columnas del rawModel
                int colCount = this.rawModel.getColumnCount();
                for (int i = 0; i < colCount; i++) {
                    String name = this.rawModel.getColumnName(i);
                    rawColumnNames.add(name);
                    // Usa el nombre de la BD como cabecera por defecto
                    finalHeaders.add(name);
                }
            } else {
                // Opción 2: Mostrar solo las columnas especificadas
                rawColumnNames.addAll(Arrays.asList(columnsToShow));
                if (displayColumnNames != null && displayColumnNames.length == columnsToShow.length) {
                    finalHeaders.addAll(Arrays.asList(displayColumnNames));
                } else {
                    // Si los nombres de cabecera no se proporcionan o no coinciden, usamos los nombres de la BD
                    finalHeaders.addAll(Arrays.asList(columnsToShow));
                }
            }

            // 2. Crear el modelo de visualización (finalModel)
            finalModel.setColumnIdentifiers(finalHeaders.toArray());

            // 3. Iterar sobre las filas del rawModel y extraer solo las columnas requeridas
            for (int i = 0; i < this.rawModel.getRowCount(); i++) {
                List<Object> rowData = new ArrayList<>();
                for (String colName : rawColumnNames) {
                    int rawIndex = this.rawModel.findColumn(colName);
                    if (rawIndex != -1) {
                        rowData.add(this.rawModel.getValueAt(i, rawIndex));
                    } else {
                        // Columna solicitada no existe en la BD
                        rowData.add(null);
                        System.err.println(STR."Advertencia: Columna '\{colName})' solicitada pero no encontrada en la BD.");
                    }
                }
                finalModel.addRow(rowData.toArray());
            }

            this.mainTable.setModel(finalModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.vista, STR."Error al cargar la tabla: \{e.getMessage()}", "Error de BD", JOptionPane.ERROR_MESSAGE);
            System.err.println(STR."❌ ERROR FATAL en cargarTabla: \{e.getMessage()}");
        }
    }

    // ----------------------------------------------------------------------
    // MÉTODOS CRUD CENTRALIZADOS (Se mantienen igual)
    // ----------------------------------------------------------------------

    protected void registrar() {
        try {
            // ... lógica de registro ...
            if (collectDataFromView()) {
                if (consultas.registrar(modelo)) {
                    JOptionPane.showMessageDialog(vista, "Registro exitoso.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    clearViewFields();
                    // 🌟 Opcional: Llamar a un método abstracto 'refreshTable()' si aplica
                } else {
                    JOptionPane.showMessageDialog(vista, "Error: No se pudo registrar. Verifique la consola.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al procesar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void modificar() {
        // ... lógica de modificar ...
        try {
            if (getModelId() <= 0) {
                JOptionPane.showMessageDialog(vista, "Debe buscar un registro antes de modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (collectDataFromView()) {
                if (consultas.modificar(modelo)) {
                    JOptionPane.showMessageDialog(vista, "Modificación exitosa.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    clearViewFields();
                    // 🌟 Opcional: Llamar a un método abstracto 'refreshTable()' si aplica
                } else {
                    JOptionPane.showMessageDialog(vista, "Error: No se pudo modificar. Verifique la consola.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al procesar la modificación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void eliminar() {
        // ... lógica de eliminar ...
        int idAEliminar = getModelId();

        try {
            if (idAEliminar <= 0) {
                JOptionPane.showMessageDialog(vista, "No hay registro seleccionado para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    vista, "Está seguro de eliminar el registro con ID " + idAEliminar + "?",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (consultas.eliminar(idAEliminar)) {
                    JOptionPane.showMessageDialog(vista, "Registro eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    clearViewFields();
                    // 🌟 Opcional: Llamar a un método abstracto 'refreshTable()' si aplica
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
        this.vistaPrincipal.setVisible(true);
    }

    abstract public void iniciar();
}