package com.softly.fonoteca.Vistas;

import javax.swing.*;

public class ReproduccionesVista extends JFrame {
    public JTable tablaReproducciones;
    public JTextField txtNombreCancion;
    public JTextField txtNombreUsuario;
    public JTextField txtFechaReproduccion;
    public JTextField txtHoraReproduccion;
    public JTextField txtSegundosReproduccidos;
    public JPanel contentPane;
    public JButton regresarButton;

    public ReproduccionesVista(){
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }
    
}
