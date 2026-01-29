package persistencia;

import clases.Alta;
import java.sql.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AltaDAO {

    public ArrayList<Alta> listar() throws SQLException {
        ArrayList<Alta> lista = new ArrayList<>();
        String sql = "SELECT num_alta, num_internamiento, fecha, hora FROM altas ORDER BY num_alta";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int numAlta = rs.getInt("num_alta");
                int numInternamiento = rs.getInt("num_internamiento");
                Date fecha = rs.getDate("fecha");
                Time hora = rs.getTime("hora");
                String fechaStr = new SimpleDateFormat("dd/MM/yyyy").format(fecha);
                lista.add(new Alta(numAlta, numInternamiento, fechaStr, hora.toString()));
            }
        }
        return lista;
    }

    public void insertar(Alta a) throws SQLException {
        String sql = "INSERT INTO altas (num_alta, num_internamiento, fecha, hora) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE num_internamiento = VALUES(num_internamiento), fecha = VALUES(fecha), hora = VALUES(hora)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, a.getNumAlta());
            ps.setInt(2, a.getNumInternamiento());
            ps.setDate(3, java.sql.Date.valueOf(convertDate(a.getFecha())));
            ps.setTime(4, java.sql.Time.valueOf(a.getHora()));
            ps.executeUpdate();
        }
    }

    public void actualizar(Alta a) throws SQLException {
        String sql = "UPDATE altas SET num_internamiento = ?, fecha = ?, hora = ? WHERE num_alta = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, a.getNumInternamiento());
            ps.setDate(2, java.sql.Date.valueOf(convertDate(a.getFecha())));
            ps.setTime(3, java.sql.Time.valueOf(a.getHora()));
            ps.setInt(4, a.getNumAlta());
            ps.executeUpdate();
        }
    }

    public void eliminar(int numAlta) throws SQLException {
        String sql = "DELETE FROM altas WHERE num_alta = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, numAlta);
            ps.executeUpdate();
        }
    }

    public Alta buscar(int numAlta) throws SQLException {
        String sql = "SELECT num_alta, num_internamiento, fecha, hora FROM altas WHERE num_alta = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, numAlta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date fecha = rs.getDate("fecha");
                    Time hora = rs.getTime("hora");
                    String fechaStr = new SimpleDateFormat("dd/MM/yyyy").format(fecha);
                    return new Alta(rs.getInt("num_alta"), rs.getInt("num_internamiento"), fechaStr, hora.toString());
                }
            }
        }
        return null;
    }

    public Integer maxCodCorrelativo() throws SQLException {
        String sql = "SELECT MAX(num_alta) AS max_cod FROM altas";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int max = rs.getInt("max_cod");
                if (rs.wasNull()) return null;
                return max;
            }
        }
        return null;
    }

    private String convertDate(String fechaDdMmYyyy) {
        // input dd/MM/yyyy -> output yyyy-MM-dd
        try {
            String[] parts = fechaDdMmYyyy.split("/");
            if (parts.length != 3) throw new IllegalArgumentException("Formato de fecha invalido: " + fechaDdMmYyyy);
            return parts[2] + "-" + String.format("%02d", Integer.parseInt(parts[1])) + "-" + String.format("%02d", Integer.parseInt(parts[0]));
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo convertir fecha: " + fechaDdMmYyyy + " -> " + e.getMessage());
        }
    }
}