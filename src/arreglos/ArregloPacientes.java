package arreglos;

import java.io.*;
import java.util.ArrayList;
import clases.Paciente;
import persistencia.PacienteDAO;
import persistencia.DBConnection;
import java.sql.SQLException;

public class ArregloPacientes {
    private ArrayList<Paciente> pacientes;
    private boolean useDB = false;
    private PacienteDAO pacienteDAO = null;

    public ArregloPacientes() {
        pacientes = new ArrayList<Paciente>();
        // Decide whether to use DB or text files. If DB is available, use it.
        try {
            useDB = DBConnection.isAvailable();
        } catch (Exception e) {
            useDB = false;
        }
        if (useDB) {
            pacienteDAO = new PacienteDAO();
        }
        cargarPacientes();
    }

    public void adicionar(Paciente x) {
        pacientes.add(x);
        if (useDB && pacienteDAO != null) {
            try {
                pacienteDAO.insertar(x);
            } catch (SQLException e) {
                System.out.println("Error al insertar paciente en BD, guardando en .txt: " + e.getMessage());
                grabarPacientes();
            }
        } else {
            grabarPacientes();
        }
    }

    public void eliminar(Paciente x) {
        pacientes.remove(x);
        if (useDB && pacienteDAO != null) {
            try {
                pacienteDAO.eliminar(x.getCodPaciente());
            } catch (SQLException e) {
                System.out.println("Error al eliminar paciente en BD, actualizando .txt: " + e.getMessage());
                grabarPacientes();
            }
        } else {
            grabarPacientes();
        }
    }

    public int tamanio() {
        return pacientes.size();
    }

    public Paciente obtener(int i) {
        return pacientes.get(i);
    }

    public Paciente buscar(int codigo) {
        // Prefer returning the in-memory instance so GUI modifications affect the list.
        for (Paciente p : pacientes) {
            if (p.getCodPaciente() == codigo)
                return p;
        }
        // If not found in memory, and DB is available, try DB and add to memory for future consistency.
        if (useDB && pacienteDAO != null) {
            try {
                Paciente p = pacienteDAO.buscar(codigo);
                if (p != null) {
                    pacientes.add(p);
                    return p;
                }
            } catch (SQLException e) {
                System.out.println("Error al buscar paciente en BD, usando cache: " + e.getMessage());
            }
        }
        return null;
    }

    public int codigoCorrelativo() {
        if (useDB && pacienteDAO != null) {
            try {
                Integer max = pacienteDAO.maxCodCorrelativo();
                if (max == null) return 202010001;
                return max + 1;
            } catch (SQLException e) {
                System.out.println("Error al obtener correlativo desde BD, usando archivos: " + e.getMessage());
                // fallback to file-based behaviour
            }
        }
        if (pacientes.isEmpty()) return 202010001;
        return pacientes.get(pacientes.size() - 1).getCodPaciente() + 1;
    }

    public void actualizarArchivo() {
        // When called from GUI after modifying an object, persist changes.
        if (useDB && pacienteDAO != null) {
            // try to update all modified patients in DB. Simpler: update each record to keep parity.
            try {
                for (Paciente p : pacientes) {
                    pacienteDAO.actualizar(p);
                }
            } catch (SQLException e) {
                System.out.println("Error al actualizar pacientes en BD, guardando en .txt: " + e.getMessage());
                grabarPacientes();
            }
        } else {
            grabarPacientes();
        }
    }

    private void grabarPacientes() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("pacientes.txt"))) {
            for (Paciente p : pacientes) {
                String linea = p.getCodPaciente() + ";" +
                               p.getNombres() + ";" +
                               p.getApellidos() + ";" +
                               p.getDni() + ";" +
                               p.getEdad() + ";" +
                               p.getCelular() + ";" +
                               p.getEstado();
                pw.println(linea);
            }
        } catch (Exception e) {
            System.out.println("Error al grabar archivo de pacientes: " + e.getMessage());
        }
    }

    private void cargarPacientes() {
        // If DB is available prefer loading from DB
        if (useDB && pacienteDAO != null) {
            try {
                pacientes = pacienteDAO.listar();
                return;
            } catch (SQLException e) {
                System.out.println("No se pudo cargar pacientes desde BD, usando .txt: " + e.getMessage());
                // fallback to file
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader("pacientes.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] s = linea.split(";");
                int codPaciente = Integer.parseInt(s[0].trim());
                String nombres = s[1].trim();
                String apellidos = s[2].trim();
                String dni = s[3].trim();
                int edad = Integer.parseInt(s[4].trim());
                int celular = Integer.parseInt(s[5].trim());
                int estado = Integer.parseInt(s[6].trim());
                pacientes.add(new Paciente(codPaciente, nombres, apellidos, dni, edad, celular, estado));
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar archivo de pacientes (puede no existir todavía)." + e.getMessage());
        }
    }
}