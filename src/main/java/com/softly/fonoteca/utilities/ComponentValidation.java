package com.softly.fonoteca.utilities;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class ComponentValidation {

    // Borde predefinido para errores
    private static final LineBorder ERROR_BORDER = new LineBorder(Color.RED, 2);

    // Borde por defecto para limpiar el error
    private static final LineBorder DEFAULT_BORDER = new LineBorder(Color.GRAY, 1);

    public static String validate(JComponent component, String[] rules) {
        Object value = getValue(component);
        return checkRules(value, rules, component);
    }

    private static Object getValue(JComponent component) {
        if (component instanceof JTextField) {
            return ((JTextField) component).getText();
        }
        if (component instanceof JComboBox) {
            return ((JComboBox<?>) component).getSelectedItem();
        }
        if (component instanceof JCheckBox) {
            return ((JCheckBox) component).isSelected();
        }
        return null;
    }

    private static String checkRules(Object value, String[] ruleArray, JComponent component) {
        String stringValue = (value != null) ? value.toString() : null;

        for (String rule : ruleArray) {

            // --- 1. Regla 'required' (Vacío o Nulo) ---
            if (rule.equals("required")) {
                if (component instanceof JCheckBox) {
                    if (value == null || !(Boolean) value) {
                        System.out.println("❌ VALIDATION FAILED: Component [" + component.getClass().getSimpleName() + "] failed 'required' (not selected).");
                        return "required";
                    }
                } else {
                    if (stringValue == null || stringValue.trim().isEmpty() ||
                            (component instanceof JComboBox && ((JComboBox<?>) component).getSelectedIndex() == -1)) {
                        System.out.println("❌ VALIDATION FAILED: Component [" + component.getClass().getSimpleName() + "] failed 'required' (value is empty or null).");
                        return "required";
                    }
                }
            }

            // Si el campo no es requerido y está vacío, salimos de las validaciones de formato.
            if (stringValue == null || stringValue.trim().isEmpty()) {
                continue;
            }

            // --- PREPARACIÓN: Limpiamos espacios antes de parsear formatos ---
            String valueToParse = stringValue.trim();

            // --- 2. Regla 'localdate' (yyyy-MM-dd) ---
            if (rule.equals("localdate")) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            .withResolverStyle(ResolverStyle.SMART);
                    LocalDate.parse(valueToParse, formatter);
                } catch (DateTimeParseException e) {
                    System.err.println("❌ VALIDATION FAILED: Component [" + component.getClass().getSimpleName() + "] value '" + valueToParse + "' failed 'localdate' (Expected yyyy-MM-dd).");
                    System.err.println("   --> Reason: " + e.getMessage());
                    return "localdate";
                }
            }

            // --- 3. Regla 'time' (mm:ss) --
            if (rule.equals("time")) {

                // 1. Verificación de estructura estricta (dos dígitos : dos dígitos)
                if (!valueToParse.matches("\\d{2}:\\d{2}")) {
                    System.err.println("❌ VALIDATION FAILED: Component [" + component.getClass().getSimpleName() + "] value '" + valueToParse + "' failed 'time' (Expected strict MM:SS format).");
                    return "time";
                }

                // 2. Verificación de rango (Segundos debe estar entre 00 y 59)
                try {
                    String[] parts = valueToParse.split(":");
                    // parts[0] son los minutos (pueden ser > 59)
                    int seconds = Integer.parseInt(parts[1]);

                    if (seconds < 0 || seconds > 59) {
                        System.err.println("❌ VALIDATION FAILED: Component [" + component.getClass().getSimpleName() + "] value '" + valueToParse + "' failed 'time' (Seconds part must be 00-59).");
                        return "time";
                    }

                } catch (NumberFormatException ex) {
                    // Esto solo ocurriría si el regex fallara o el split fuera raro, pero es bueno tenerlo.
                    System.err.println("❌ VALIDATION FAILED: Component [" + component.getClass().getSimpleName() + "] value '" + valueToParse + "' failed 'time' (Internal number parsing error).");
                    return "time";
                }

                // Si la estructura es MM:SS y los segundos son 00-59, es una duración válida.
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------

    /**
     * Muestra u oculta un tooltip de error y un borde de error.
     * * @param component     El JComponent a modificar.
     * @param reglaFallida  El String devuelto por validate (ej: "required", "localdate", o null)
     * @param nombreCampo   El nombre amigable para el mensaje.
     */
    public static void mostrarError(JComponent component, String reglaFallida, String nombreCampo) {

        // --- Caso Éxito ---
        if (reglaFallida == null) {
            component.setToolTipText(null);
            component.setBorder(DEFAULT_BORDER);
            return;
        }

        // --- Caso Error ---
        String mensajeError = switch (reglaFallida) {
            case "required" -> "El campo **'" + nombreCampo + "'** es obligatorio.";
            case "localdate" -> "El campo **'" + nombreCampo + "'** debe tener el formato AAAA-MM-DD.";
            case "time" -> "El campo **'" + nombreCampo + "'** debe tener el formato MM:SS (Minutos:Segundos).";
            default -> "Error de formato desconocido en el campo **'" + nombreCampo + "'**.";
        };

        // Aplicamos el tooltip de error
        component.setToolTipText(mensajeError);

        // Resaltar el componente con un borde rojo
        component.setBorder(ERROR_BORDER);
    }
}