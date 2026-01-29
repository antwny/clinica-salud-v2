package persistencia;

import clases.Paciente;
import java.sql.*;
import java.util.ArrayList;

public class PacienteDAO {

    public ArrayList<Paciente> listar() throws SQLException {
        ArrayList<Paciente> lista = new ArrayList<>();
        String sql = "SELECT cod_paciente, nombres, apellidos, dni, edad, celular, estado FROM pacientes ORDER BY cod_paciente";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cod = rs.getInt("cod_paciente");
                String nombres = rs.getString("nombres");
                String apellidos = rs.getString("apellidos");
                String dni = rs.getString("dni");
                int edad = rs.getInt("edad");
                int celular = rs.getInt("celular");
                int estado = rs.getInt("estado");
                lista.add(new Paciente(cod, nombres, apellidos, dni, edad, celular, estado));
            }
        }
        return lista;
    }

    public void insertar(Paciente p) throws SQLException {
        String sql = "INSERT INTO pacientes (cod_paciente, nombres, apellidos, dni, edad, celular, estado) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE nombres = VALUES(nombres), apellidos = VALUES(apellidos), dni = VALUES(dni), edad = VALUES(edad), celular = VALUES(celular), estado = VALUES(estado)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, p.getCodPaciente());
            ps.setString(2, p.getNombres());
            ps.setString(3, p.getApellidos());
            ps.setString(4, p.getDni());
            ps.setInt(5, p.getEdad());
            ps.setInt(6, p.getCelular());
            ps.setInt(7, p.getEstado());
            ps.executeUpdate();
        }
    }

    public void actualizar(Paciente p) throws SQLException {
        String sql = "UPDATE pacientes SET nombres = ?, apellidos = ?, dni = ?, edad = ?, celular = ?, estado = ? WHERE cod_paciente = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNombres());
            ps.setString(2, p.getApellidos());
            ps.setString(3, p.getDni());
            ps.setInt(4, p.getEdad());
            ps.setInt(5, p.getCelular());
            ps.setInt(6, p.getEstado());
            ps.setInt(7, p.getCodPaciente());
            ps.executeUpdate();
        }
    }

    public void eliminar(int codPaciente) throws SQLException {
        String sql = "DELETE FROM pacientes WHERE cod_paciente = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, codPaciente);
            ps.executeUpdate();
        }
    }

    public Paciente buscar(int codPaciente) throws SQLException {
        String sql = "SELECT cod_paciente, nombres, apellidos, dni, edad, celular, estado FROM pacientes WHERE cod_paciente = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, codPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Paciente(
                        rs.getInt("cod_paciente"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("dni"),
                        rs.getInt("edad"),
                        rs.getInt("celular"),
                        rs.getInt("estado")
                    );
                }
            }
        }
        return null;
    }

    public Integer maxCodCorrelativo() throws SQLException {
        String sql = "SELECT MAX(cod_paciente) AS max_cod FROM pacientes";
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