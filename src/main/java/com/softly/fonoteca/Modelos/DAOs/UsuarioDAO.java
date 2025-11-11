package com.softly.fonoteca.Modelos.DAOs;

import com.softly.fonoteca.Modelos.DTOs.Usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UsuarioDAO extends BaseDAO<Usuario> {

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

    @Override
    protected void mapToStatement(PreparedStatement ps, Usuario usuario) throws SQLException {

        ps.setString(1, usuario.getEmail());
        ps.setString(2, usuario.getPassword());
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
}