package com.softly.fonoteca.Modelos.DTOs;

public class ComboBoxItem {
    private final int id;
    private final String displayValue;

    public ComboBoxItem(int id, String displayValue) {
        this.id = id;
        this.displayValue = displayValue;
    }

    public int getId() {
        return id;
    }

    // SOBREESCRITURA CLAVE: Esto es lo que JComboBox muestra
    @Override
    public String toString() {
        return displayValue;
    }
}
