package com.softly.fonoteca.utilities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FormatDates {
    public static LocalDate getFormatDate(String campo){

        try {
            String fechaTexto = campo.trim();

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(fechaTexto,formato);
        } catch (Exception e) {
            throw new RuntimeException(e + "Fallo al formatear fecha");
        }
    }
    public static LocalTime parsearTiempo(String texto) {
        try {

            // Formatear el tiempo
            String tiempoFormateado = texto.trim().toUpperCase();

            // Validar la entrada
            if (!tiempoFormateado.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")) {
                throw new Exception("Formato incorrecto. Debe ser MM:ss:mm");
            }

            // Separar minutos, segundos y milisegundos
            String[] partes = tiempoFormateado.split(":");
            int minutos = Integer.parseInt(partes[0]);
            int segundos = Integer.parseInt(partes[1]);
            int milisegundos = Integer.parseInt(partes[2]);

            // Validar que los minutos estén entre 00 y 59
            if (minutos < 0 || minutos > 23) { // Cambiado de 59 a 23 para horas
                throw new Exception("Minutos deben estar entre 00 y 23");
            }

            // Validar que los segundos estén entre 00 y 59
            if (segundos < 0 || segundos > 59) {
                throw new Exception("Segundos deben estar entre 00 y 59");
            }

            // Calcular el valor total en milisegundos
            long totalMilliseconds = minutos * 3600000 + segundos * 60000; // Cambiado de 1000 a 60000

            // Convertir a LocalTime
            return LocalTime.of(minutos, segundos, milisegundos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
