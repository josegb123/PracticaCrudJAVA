package com.softly.fonoteca.Modelos.DAOs;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.softly.fonoteca.Modelos.DTOs.Usuario;
import com.softly.fonoteca.utilities.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO extends BaseDAO<Usuario> {

    // Costo recomendado para BCrypt
    private static final int BCRYPT_COST = 12;

    @Override
    protected String getTableName() {
        return "usuarios";
    }

    @Override
    protected String getPrimaryKeyColumnName() {
        return "idUsuario";
    }

    @Override
    protected String[] getAllColumns() {
        return new String[]{
                "email", "password", "nombres", "apellidos", "sexo",
                "fechaNacimiento", "paisNacimiento", "paisResidencia", "idioma", "fechaRegistro"
        };
    }

    @Override
    protected int getIdFromDto(Usuario dto) {
        return dto.getId();
    }

    /**
     * Mapea el DTO a los parámetros del PreparedStatement.
     * Implementa el hashing de contraseñas usando BCrypt antes de la inserción/actualización.
     */
    @Override
    protected void mapToStatement(PreparedStatement ps, Usuario usuario) throws SQLException {
        String passwordValue = usuario.getHashedPassword();
        String finalHash;

        // 1. Detección y Hashing de Contraseña
        if (passwordValue != null && passwordValue.length() < 60 && !passwordValue.startsWith("$2")) {

            // Usamos el método de BCrypt para hashear
            finalHash = BCrypt.withDefaults().hashToString(BCRYPT_COST, passwordValue.toCharArray());
        } else {
            // Es un hash existente (viene de la DB o ya fue hasheado) o es nulo.
            finalHash = passwordValue;
        }

        ps.setString(1, usuario.getEmail());
        ps.setString(2, finalHash);
        ps.setString(3, usuario.getNombres());
        ps.setString(4, usuario.getApellidos());
        ps.setString(5, usuario.getSexo());
        ps.setObject(6, usuario.getFechaNacimiento());
        ps.setString(7, usuario.getPaisNacimiento());
        ps.setString(8, usuario.getPaisResidencia());
        ps.setString(9, usuario.getIdioma());
        ps.setObject(10, usuario.getFechaRegistro());

    }

    @Override
    protected Usuario mapFromResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("idUsuario"));
        usuario.setEmail(rs.getString("email"));
        // El campo 'password' de la DB ahora contiene el hash BCrypt
        usuario.setHashedPassword(rs.getString("password"));
        usuario.setNombres(rs.getString("nombres"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setSexo(rs.getString("sexo"));
        usuario.setFechaNacimiento(rs.getObject("fechaNacimiento", java.time.LocalDate.class));
        usuario.setPaisNacimiento(rs.getString("paisNacimiento"));
        usuario.setPaisResidencia(rs.getString("paisResidencia"));
        usuario.setIdioma(rs.getString("idioma"));
        usuario.setFechaRegistro(rs.getObject("fechaRegistro", java.time.LocalDateTime.class));
        return usuario;
    }

    public Usuario buscarPorEmail(String email) {

        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);


            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapFromResultSet(rs);

                    return usuario;
                }
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Error critico en la busqueda de usuario por email: " + e.getLocalizedMessage());

            return null;
        }
    }
}