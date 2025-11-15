package com.softly.fonoteca.Vistas;

import javax.swing.*;

// 1. Extiende JFrame e implementa BaseView
public class MainView extends JFrame implements BaseView {

    // Estos componentes son creados y configurados por el diseñador de IntelliJ
    // y están asociados automáticamente al contentPane de este JFrame.
    public JPanel contentPane;
    public JButton usuariosButton;
    public JButton cancionesButton;
    public JButton reproduccionesButton;
    public JButton salirButton;
    public JButton calificacionesButton;

    public MainView(){
        // Usamos la implementación de JFrame (super) para configurar la ventana
        super.setContentPane(contentPane); // Usa el contentPane generado por el diseñador
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.pack();
    }

}