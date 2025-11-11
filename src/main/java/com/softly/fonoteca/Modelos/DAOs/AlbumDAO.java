package com.softly.fonoteca.Modelos.DAOs;

import com.softly.fonoteca.Modelos.DTOs.Album;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AlbumDAO extends BaseDAO<Album> {

    @Override
    protected String getTableName() {
        return "albumnes";
    }

    @Override
    protected String getPrimaryKeyColumnName() {
        return "idAlbum";
    }

    @Override
    protected String[] getAllColumns() {
        return new String[] {
                "titulo", "selloDiscografico", "fechaLanzamiento", "idGeneroPrincipal"
        };
    }

    @Override
    protected int getIdFromDto(Album dto) {
        return dto.getIdAlbum();
    }

    @Override
    protected void mapToStatement(PreparedStatement ps, Album album) throws SQLException {
        ps.setString(1, album.getTitulo());
        ps.setString(2, album.getSelloDiscografico());
        ps.setDate(3, java.sql.Date.valueOf(album.getFechaLanzamiento()));
        ps.setInt(4, album.getIdGeneroPrincipal());

    }

    @Override
    protected Album mapFromResultSet(ResultSet rs) throws SQLException {
        Album album = new Album();
        album.setIdAlbum(rs.getInt("idAlbum"));
        album.setTitulo(rs.getString("titulo"));
        album.setSelloDiscografico(rs.getString("selloDiscografico"));
        album.setFechaLanzamiento(rs.getObject("fechaLanzamiento", LocalDate.class));
        album.setIdGeneroPrincipal(rs.getInt("idGeneroPrincipal"));
        return album;
    }
}
