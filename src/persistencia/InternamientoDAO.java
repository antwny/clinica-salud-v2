package persistencia;

import clases.Internamiento;
import java.sql.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InternamientoDAO {

    public ArrayList<Internamiento> listar() throws SQLException {
        ArrayList<Internamiento> lista = new ArrayList<>();
        String sql = "SELECT num_internamiento, cod_paciente, cod_tratamiento, fecha, hora FROM internamientos ORDER BY num_internamiento";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int num = rs.getInt("num_internamiento");
                int codPac = rs.getInt("cod_paciente");
                int codTrat = rs.getInt("cod_tratamiento");
                Date fecha = rs.getDate("fecha");
                Time hora = rs.getTime("hora");
                String fechaStr = new SimpleDateFormat("dd/MM/yyyy").format(fecha);
                String horaStr = hora.toString();
                lista.add(new Internamiento(num, codPac, codTrat, fechaStr, horaStr));
            }
        }
        return lista;
    }

    public void insertar(Internamiento i) throws SQLException {
        String sql = "INSERT INTO internamientos (num_internamiento, cod_paciente, cod_tratamiento, fecha, hora) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE cod_paciente = VALUES(cod_paciente), cod_tratamiento = VALUES(cod_tratamiento), fecha = VALUES(fecha), hora = VALUES(hora)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, i.getNumInternamiento());
            ps.setInt(2, i.getCodPaciente());
            ps.setInt(3, i.getCodTratamiento());
            // Convert fecha dd/MM/yyyy to yyyy-MM-dd
            java.sql.Date fechaSql = java.sql.Date.valueOf(convertDate(i.getFecha()));
            ps.setDate(4, fechaSql);
            ps.setTime(5, java.sql.Time.valueOf(i.getHora()));
            ps.executeUpdate();
        }
    }

    public void actualizar(Internamiento i) throws SQLException {
        String sql = "UPDATE internamientos SET cod_paciente = ?, cod_tratamiento = ?, fecha = ?, hora = ? WHERE num_internamiento = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, i.getCodPaciente());
            ps.setInt(2, i.getCodTratamiento());
            ps.setDate(3, java.sql.Date.valueOf(convertDate(i.getFecha())));
            ps.setTime(4, java.sql.Time.valueOf(i.getHora()));
            ps.setInt(5, i.getNumInternamiento());
            ps.executeUpdate();
        }
    }

    public void eliminar(int numInternamiento) throws SQLException {
        String sql = "DELETE FROM internamientos WHERE num_internamiento = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, numInternamiento);
            ps.executeUpdate();
        }
    }

    public Internamiento buscar(int numInternamiento) throws SQLException {
        String sql = "SELECT num_internamiento, cod_paciente, cod_tratamiento, fecha, hora FROM internamientos WHERE num_internamiento = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, numInternamiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date fecha = rs.getDate("fecha");
                    Time hora = rs.getTime("hora");
                    String fechaStr = new SimpleDateFormat("dd/MM/yyyy").format(fecha);
                    return new Internamiento(
                        rs.getInt("num_internamiento"),
                        rs.getInt("cod_paciente"),
                        rs.getInt("cod_tratamiento"),
                        fechaStr,
                        hora.toString()
                    );
                }
            }
        }
        return null;
    }

    public Integer maxCodCorrelativo() throws SQLException {
        String sql = "SELECT MAX(num_internamiento) AS max_cod FROM internamientos";
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