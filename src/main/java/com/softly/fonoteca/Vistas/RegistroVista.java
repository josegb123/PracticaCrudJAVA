package com.softly.fonoteca.Vistas;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RegistroVista extends JDialog {
    public JPanel contentPane;

    public JTextField txtEmail;
    public JTextField txtNombre;
    public JPasswordField passField;
    public JComboBox cmbSexo;
    public JTextField txtApellidos;
    public JTextField txtPaisBorn;
    public JTextField txtPaisResidence;
    public JTextField txtIdioma;
    public DatePicker txtDateBirth;
    public JPasswordField passConfirmField;

    public JButton registrarButton;
    public JButton buttonCancel;
    public JButton OKButton;

    /**
     * Constructor ajustado para aceptar el JFrame propietario.
     *
     * @param owner El JFrame propietario (ej. LoginVista).
     */
    public RegistroVista(JFrame owner) {
        super(owner, "Registro de Nuevo Usuario", true);
        setContentPane(contentPane);
        pack();
        setModal(true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        contentPane.registerKeyboardAction(_ -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    public JButton getRegistrarButton() {
        return registrarButton;
    }

    public JButton getCancelButton() {
        return buttonCancel;
    }

    private void createUIComponents() {
        DatePickerSettings dateSettings;
        dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        dateSettings.setFormatForDatesBeforeCommonEra("uuuu-MM-dd");

        txtDateBirth = new DatePicker(dateSettings);
        txtDateBirth.setDateToToday();
    }
}