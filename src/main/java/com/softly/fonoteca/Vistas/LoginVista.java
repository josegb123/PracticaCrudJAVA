package com.softly.fonoteca.Vistas;

import javax.swing.*;

public class LoginVista extends JFrame {
    private JPasswordField txtPasswd;
    private JTextField txtEmail;
    private JButton iniciarSesionButton;
    private JLabel lblAlerts;
    private JButton registerButton;
    private JPanel contentPane;

    public LoginVista(){
        setContentPane(contentPane);
        pack();
    }

    public JButton getIniciarSesionButton() {
        return iniciarSesionButton;
    }

    public JButton getRegisterButton() {
        return registerButton;
    }

    public JPasswordField getTxtPasswd() {
        return txtPasswd;
    }

    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public void setMessageAlert(String mensaje) {
        this.lblAlerts.setText(mensaje);
    }


    public JLabel getLblAlerts() {
        return lblAlerts;
    }
}
