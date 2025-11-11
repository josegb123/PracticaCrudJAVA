package com.softly.fonoteca.Modelos.DAOs;

import com.softly.fonoteca.Modelos.DTOs.Genero;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GeneroDAO extends BaseDAO<Genero> {

    @Override
    protected String getTableName() {
        return "generos";
    }

    @Override
    protected String getPrimaryKeyColumnName() {
        return "idGenero";
    }

    @Override
    protected String[] getAllColumns() {
        return new String[]{
                "nombre", "descripcion"
        };
    }

    @Override
    protected int getIdFromDto(Genero dto) {
        return dto.getIdGenero();
    }

    @Override
    protected void mapToStatement(PreparedStatement ps, Genero genero) throws SQLException {
        ps.setString(1, genero.getNombre());
        ps.setString(2, genero.getDescripcion());
    }

    @Override
    protected Genero mapFromResultSet(ResultSet rs) throws SQLException {
        Genero genero = new Genero();
        genero.setIdGenero(rs.getInt("idGenero"));
        genero.setNombre(rs.getString("nombre"));
        genero.setDescripcion(rs.getString("descripcion"));
        return genero;
    }


}

