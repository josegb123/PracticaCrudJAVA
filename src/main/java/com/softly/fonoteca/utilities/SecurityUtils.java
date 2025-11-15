package com.softly.fonoteca.utilities;

import at.favre.lib.crypto.bcrypt.BCrypt; // Importar la librería BCrypt
import at.favre.lib.crypto.bcrypt.BCrypt.Result;

/**
 * Clase de utilidad para el hashing y verificación de contraseñas usando el
 * algoritmo BCrypt, compatible con las funciones nativas de PHP.
 */
public class SecurityUtils {

    // Costo recomendado para BCrypt: 12 es un buen equilibrio para 2024.
    private static final int BCRYPT_COST = 12;

    private SecurityUtils() {
        // Constructor privado para prevenir la instanciación
    }

    /**
     * Genera el hash de la contraseña usando BCrypt.
     * Este método solo se debe usar para registrar nuevas contraseñas.
     * * @param password La contraseña en texto plano.
     * @return El hash BCrypt completo (incluye el costo y el sal).
     */
    public static String hashPassword(char[] password) {
        // Usa la implementación de 'favre lib' para hashear
        // El hashing se realiza directamente con el array de caracteres para mejor seguridad en memoria.
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password);
    }

    /**
     * Verifica una contraseña en texto plano contra un hash BCrypt almacenado.
     * * @param passwordChars La contraseña en texto plano como array de caracteres.
     * @param storedHash El hash BCrypt almacenado en la base de datos.
     * @return True si la contraseña es válida, False en caso contrario.
     */
    public static boolean verifyPassword(char[] passwordChars, String storedHash) {
        // El verificador de BCrypt extrae el sal y el costo del storedHash automáticamente
        // y realiza una comparación de tiempo constante.
        Result result = BCrypt.verifyer().verify(passwordChars, storedHash);

        return result.verified;
    }
}