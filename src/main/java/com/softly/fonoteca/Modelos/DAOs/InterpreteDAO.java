package com.softly.fonoteca.Modelos.DAOs;

import com.softly.fonoteca.Modelos.DTOs.Interprete;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class InterpreteDAO extends BaseDAO<Interprete> {
    @Override
    protected String getTableName() {
        return "interpretes";
    }

    @Override
    protected String getPrimaryKeyColumnName() {
        return "idInterprete";
    }

    @Override
    protected String[] getAllColumns() {
        return new String[]{
                "nombre","yearLanzamiento","yearRetiro","tituloInterprete","idGeneroPrincipal"
        };
    }

    @Override
    protected int getIdFromDto(Interprete interprete) {
        return interprete.getIdInterprete();
    }

    @Override
    protected void mapToStatement(PreparedStatement ps, Interprete interprete) throws SQLException {

        ps.setString(1,interprete.getNombre());
        ps.setDate(2,java.sql.Date.valueOf(interprete.getYearLanzamiento()));
        ps.setDate(3,java.sql.Date.valueOf(interprete.getYearRetiro()));
        ps.setString(4,interprete.getTituloInterprete());
        ps.setInt(5,interprete.getIdGeneroPrincipal());

    }

    @Override
    protected Interprete mapFromResultSet(ResultSet rs) throws SQLException {

        Interprete interprete = new Interprete();
        interprete.setIdInterprete(rs.getInt("idInterprete"));
        interprete.setNombre(rs.getString("nombre"));
        interprete.setYearLanzamiento(rs.getObject("yearLanzamiento", LocalDate.class));
        interprete.setYearRetiro(rs.getObject("yearRetiro", LocalDate.class));
        interprete.setTituloInterprete(rs.getString("tituloInterprete"));
        interprete.setIdGeneroPrincipal(rs.getInt("idGeneroPrincipal"));
        return interprete;

    }
}
