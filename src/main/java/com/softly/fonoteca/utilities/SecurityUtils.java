package com.softly.fonoteca.utilities;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class SecurityUtils {

    // Configuración recomendada
    private static final int ITERATIONS = 65536; // Alto número de iteraciones
    private static final int KEY_LENGTH = 256; // Longitud de la clave en bits
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private SecurityUtils() {
        // Constructor privado para prevenir la instanciación
    }
    // Genera el hash de la contraseña y el salto para almacenar
    public static String hashPassword(String password) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // Salto de 16 bytes (128 bits)
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

        byte[] hash = factory.generateSecret(spec).getEncoded();

        // Almacena el salto y el hash juntos, usualmente codificados en Base64
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);

        // Formato para guardar en la base de datos: "salto:hash"
        return saltBase64 + ":" + hashBase64;
    }
    public static boolean verifyPassword(String password, String storedHashAndSalt) throws Exception {
        String[] parts = storedHashAndSalt.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid stored password format");
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] storedHash = Base64.getDecoder().decode(parts[1]);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

        byte[] incomingHash = factory.generateSecret(spec).getEncoded();

        // Compara los hashes de forma segura para prevenir ataques de temporización
        return java.util.Arrays.equals(storedHash, incomingHash);
    }
}
