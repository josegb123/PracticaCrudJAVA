package com.softly.fonoteca.Vistas;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.optionalusertools.PickerUtilities;
import javax.swing.*;
import java.time.LocalTime;

public class ReproduccionesVista extends JFrame {
    public JTable tablaReproducciones;
    public JTextField txtSegundosReproduccidos;
    public JPanel contentPane;
    public JButton regresarButton;
    public JButton agregarButton;
    public JButton modificarButton;
    public JButton eliminarButton;
    public JComboBox cmbUsuarios;
    public JComboBox cmbCanciones;
    public DatePicker txtFechaReproduccion;
    public TimePicker txtHoraReproduccion;


    public ReproduccionesVista(){
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

    }


    private void createUIComponents() {
        TimePickerSettings timeSettings = new TimePickerSettings();
        timeSettings.setFormatForDisplayTime(PickerUtilities.createFormatterFromPatternString(
                "HH:mm:ss", timeSettings.getLocale()));
        timeSettings.setFormatForMenuTimes(PickerUtilities.createFormatterFromPatternString(
                "HH:mm", timeSettings.getLocale()));
        timeSettings.initialTime = LocalTime.of(15, 00, 00);
        timeSettings.setSizeTextFieldMinimumWidth(180);
        txtHoraReproduccion = new TimePicker(timeSettings);
    }
}
