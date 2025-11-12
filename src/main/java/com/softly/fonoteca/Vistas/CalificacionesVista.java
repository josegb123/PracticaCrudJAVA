package com.softly.fonoteca.Vistas;

import javax.swing.*;

public class CalificacionesVista extends JFrame {
    public JComboBox cmbCanciones;
    public JComboBox cmbUsuarios;
    public JTextArea txtComentarios;
    public JTextField txtFechaCalificacion;
    public JTextField txtHoraCalificacion;
    public JPanel contentPane;
    public JTable tablaCalificaciones;
    public JButton agregarButton;
    public JButton modificarButton;
    public JButton eliminarButton;
    public JButton regresarButton;
    public JTextField txtCalificacion;

    public CalificacionesVista() {
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }


}
