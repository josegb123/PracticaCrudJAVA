package com.softly.fonoteca.Modelos.DAOs;

import com.softly.fonoteca.Modelos.DTOs.Cancion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class CancionDAO extends BaseDAO<Cancion> {

    @Override
    protected String getTableName() {
        return "canciones";
    }

    @Override
    protected String getPrimaryKeyColumnName() {
        return "idCancion";
    }

    @Override
    protected String[] getAllColumns() {
        return new String[]{
                "titulo", "duracion", "tiempoBPM", "idioma",
                "fechaLanzamiento", "idAlbumOriginal", "idGenero", "IdInterpretePrincipal", "esInstrumental"
        };
    }

    @Override
    protected int getIdFromDto(Cancion dto) {
        return dto.getIdCancion();
    }

    @Override
    protected void mapToStatement(PreparedStatement ps, Cancion cancion) throws SQLException {

        ps.setString(1, cancion.getTitulo());
        ps.setString(2, cancion.getDuracion());
        ps.setInt(3, cancion.getBpm());
        ps.setString(4, cancion.getIdioma());
        ps.setDate(5, java.sql.Date.valueOf(cancion.getFechaLanzamiento()));
        ps.setInt(6, cancion.getAlbum());
        ps.setInt(7, cancion.getGenero());
        ps.setInt(8, cancion.getInterprete());
        ps.setBoolean(9, cancion.isInstrumental());

    }

    @Override
    protected Cancion mapFromResultSet(ResultSet rs) throws SQLException {
        Cancion cancion = new Cancion();
        cancion.setIdCancion(rs.getInt("idCancion"));
        cancion.setTitulo(rs.getString("titulo"));
        cancion.setDuracion(rs.getString("duracion"));
        cancion.setBpm(rs.getInt("tiempoBPM"));
        cancion.setIdioma(rs.getString("idioma"));
        cancion.setFechaLanzamiento(rs.getObject("fechaLanzamiento", LocalDate.class));
        cancion.setAlbum(rs.getInt("idAlbumOriginal"));
        cancion.setGenero(rs.getInt("idGenero"));
        cancion.setInterprete(rs.getInt("idInterpretePrincipal"));
        cancion.setInstrumental(rs.getBoolean("esInstrumental"));

        return cancion;
    }
}
