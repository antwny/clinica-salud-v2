package persistencia;

import clases.Tratamiento;
import java.sql.*;
import java.util.ArrayList;

public class TratamientoDAO {

    public ArrayList<Tratamiento> listar() throws SQLException {
        ArrayList<Tratamiento> lista = new ArrayList<>();
        String sql = "SELECT cod_tratamiento, nombre_tratamiento, duracion_dias, sesiones, costo FROM tratamientos ORDER BY cod_tratamiento";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cod = rs.getInt("cod_tratamiento");
                String nombre = rs.getString("nombre_tratamiento");
                int duracion = rs.getInt("duracion_dias");
                int sesiones = rs.getInt("sesiones");
                double costo = rs.getDouble("costo");
                lista.add(new Tratamiento(cod, nombre, duracion, sesiones, costo));
            }
        }
        return lista;
    }

    public void insertar(Tratamiento t) throws SQLException {
        String sql = "INSERT INTO tratamientos (cod_tratamiento, nombre_tratamiento, duracion_dias, sesiones, costo) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE nombre_tratamiento = VALUES(nombre_tratamiento), duracion_dias = VALUES(duracion_dias), sesiones = VALUES(sesiones), costo = VALUES(costo)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, t.getCodTratamiento());
            ps.setString(2, t.getNombreTratamiento());
            ps.setInt(3, t.getDuracionDias());
            ps.setInt(4, t.getSesiones());
            ps.setDouble(5, t.getCosto());
            ps.executeUpdate();
        }
    }

    public void actualizar(Tratamiento t) throws SQLException {
        String sql = "UPDATE tratamientos SET nombre_tratamiento = ?, duracion_dias = ?, sesiones = ?, costo = ? WHERE cod_tratamiento = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getNombreTratamiento());
            ps.setInt(2, t.getDuracionDias());
            ps.setInt(3, t.getSesiones());
            ps.setDouble(4, t.getCosto());
            ps.setInt(5, t.getCodTratamiento());
            ps.executeUpdate();
        }
    }

    public void eliminar(int codTratamiento) throws SQLException {
        String sql = "DELETE FROM tratamientos WHERE cod_tratamiento = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, codTratamiento);
            ps.executeUpdate();
        }
    }

    public Tratamiento buscar(int codTratamiento) throws SQLException {
        String sql = "SELECT cod_tratamiento, nombre_tratamiento, duracion_dias, sesiones, costo FROM tratamientos WHERE cod_tratamiento = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, codTratamiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Tratamiento(
                        rs.getInt("cod_tratamiento"),
                        rs.getString("nombre_tratamiento"),
                        rs.getInt("duracion_dias"),
                        rs.getInt("sesiones"),
                        rs.getDouble("costo")
                    );
                }
            }
        }
        return null;
    }

    public Integer maxCodCorrelativo() throws SQLException {
        String sql = "SELECT MAX(cod_tratamiento) AS max_cod FROM tratamientos";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int max = rs.getInt("max_cod");
                if (rs.wasNull()) return null;
                return max;
            }
        }
        return null;
    }
}