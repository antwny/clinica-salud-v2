package persistencia;

import java.io.*;
import java.sql.SQLException;
import clases.Paciente;
import clases.Tratamiento;
import clases.Internamiento;
import clases.Alta;

public class DataMigrator {

    private static String basePath = "."; // por defecto carpeta actual
    private static String PATH_PACIENTES = "pacientes.txt";
    private static String PATH_TRATAMIENTOS = "tratamientos.txt";
    private static String PATH_INTERNAMIENTOS = "internamientos.txt";
    private static String PATH_ALTAS = "altas.txt";

    public static void main(String[] args) {
        try {
            if (args.length > 0 && args[0] != null && !args[0].isEmpty()) {
                basePath = args[0];
            }
            System.out.println("Base path para archivos: " + new File(basePath).getAbsolutePath());

            if (!DBConnection.isAvailable()) {
                System.out.println("BD no disponible. Asegure que MySQL esté corriendo y que DBConnection tenga las credenciales correctas.");
                return;
            }
            migrarPacientes();
            migrarTratamientos();
            migrarInternamientos();
            migrarAltas();
            System.out.println("Migración finalizada.");
        } catch (Exception e) {
            System.out.println("Error en migración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static File resolve(String relative) {
        File f = new File(basePath, relative);
        return f;
    }

    private static void migrarPacientes() {
        File f = resolve(PATH_PACIENTES);
        System.out.println("Migrando pacientes desde archivo: " + f.getAbsolutePath());
        if (!f.exists()) { System.out.println("No existe " + f.getAbsolutePath() + ", omitiendo."); return; }
        PacienteDAO dao = new PacienteDAO();
        int cont = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] s = linea.split(";");
                if (s.length < 7) { System.out.println("Línea inválida pacientes: " + linea); continue; }
                try {
                    int codPaciente = Integer.parseInt(s[0].trim());
                    String nombres = s[1].trim();
                    String apellidos = s[2].trim();
                    String dni = s[3].trim();
                    int edad = Integer.parseInt(s[4].trim());
                    int celular = Integer.parseInt(s[5].trim());
                    int estado = Integer.parseInt(s[6].trim());
                    Paciente p = new Paciente(codPaciente, nombres, apellidos, dni, edad, celular, estado);
                    try {
                        if (dao.buscar(codPaciente) == null) {
                            dao.insertar(p);
                            cont++;
                            System.out.println("[PACIENTE] Insertado: " + codPaciente);
                        } else {
                            dao.actualizar(p);
                            System.out.println("[PACIENTE] Actualizado: " + codPaciente);
                        }
                    } catch (SQLException sq) {
                        System.err.println("SQLException al migrar paciente " + codPaciente + ": " + sq.getMessage());
                        sq.printStackTrace();
                    }
                } catch (Exception ex) {
                    System.out.println("Error parseando paciente: " + ex.getMessage() + " -> " + linea);
                    ex.printStackTrace();
                }
            }
            System.out.println("Pacientes insertados: " + cont);
        } catch (IOException e) {
            System.out.println("No se pudo leer " + f.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void migrarTratamientos() {
        File f = resolve(PATH_TRATAMIENTOS);
        System.out.println("Migrando tratamientos desde archivo: " + f.getAbsolutePath());
        if (!f.exists()) { System.out.println("No existe " + f.getAbsolutePath() + ", omitiendo."); return; }
        TratamientoDAO dao = new TratamientoDAO();
        int cont = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] s = linea.split(";");
                if (s.length < 5) { System.out.println("Línea inválida tratamientos: " + linea); continue; }
                try {
                    int cod = Integer.parseInt(s[0].trim());
                    String nombre = s[1].trim();
                    int duracion = Integer.parseInt(s[2].trim());
                    int sesiones = Integer.parseInt(s[3].trim());
                    double costo = Double.parseDouble(s[4].trim());
                    Tratamiento t = new Tratamiento(cod, nombre, duracion, sesiones, costo);
                    try {
                        if (dao.buscar(cod) == null) { dao.insertar(t); cont++; System.out.println("[TRATAMIENTO] Insertado: " + cod); } else { dao.actualizar(t); System.out.println("[TRATAMIENTO] Actualizado: " + cod); }
                    } catch (SQLException sq) {
                        System.err.println("SQLException al migrar tratamiento " + cod + ": " + sq.getMessage());
                        sq.printStackTrace();
                    }
                } catch (Exception ex) {
                    System.out.println("Error parseando tratamiento: " + ex.getMessage() + " -> " + linea);
                    ex.printStackTrace();
                }
            }
            System.out.println("Tratamientos insertados: " + cont);
        } catch (IOException e) {
            System.out.println("No se pudo leer " + f.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void migrarInternamientos() {
        File f = resolve(PATH_INTERNAMIENTOS);
        System.out.println("Migrando internamientos desde archivo: " + f.getAbsolutePath());
        if (!f.exists()) { System.out.println("No existe " + f.getAbsolutePath() + ", omitiendo."); return; }
        InternamientoDAO dao = new InternamientoDAO();
        int cont = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] s = linea.split(";");
                if (s.length < 5) { System.out.println("Línea inválida internamientos: " + linea); continue; }
                try {
                    int num = Integer.parseInt(s[0].trim());
                    int codPac = Integer.parseInt(s[1].trim());
                    int codTrat = Integer.parseInt(s[2].trim());
                    String fecha = s[3].trim(); // dd/MM/yyyy
                    String hora = s[4].trim();  // HH:mm:ss
                    Internamiento it = new Internamiento(num, codPac, codTrat, fecha, hora);
                    try {
                        if (dao.buscar(num) == null) { dao.insertar(it); cont++; System.out.println("[INTERNAMIENTO] Insertado: " + num); } else { dao.actualizar(it); System.out.println("[INTERNAMIENTO] Actualizado: " + num); }
                    } catch (SQLException sq) {
                        System.err.println("SQLException al migrar internamiento " + num + ": " + sq.getMessage());
                        sq.printStackTrace();
                    }
                } catch (Exception ex) {
                    System.out.println("Error parseando internamiento: " + ex.getMessage() + " -> " + linea);
                    ex.printStackTrace();
                }
            }
            System.out.println("Internamientos insertados: " + cont);
        } catch (IOException e) {
            System.out.println("No se pudo leer " + f.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void migrarAltas() {
        File f = resolve(PATH_ALTAS);
        System.out.println("Migrando altas desde archivo: " + f.getAbsolutePath());
        if (!f.exists()) { System.out.println("No existe " + f.getAbsolutePath() + ", omitiendo."); return; }
        AltaDAO dao = new AltaDAO();
        int cont = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] s = linea.split(";");
                if (s.length < 4) { System.out.println("Línea inválida altas: " + linea); continue; }
                try {
                    int numAlta = Integer.parseInt(s[0].trim());
                    int numIntern = Integer.parseInt(s[1].trim());
                    String fecha = s[2].trim();
                    String hora = s[3].trim();
                    Alta a = new Alta(numAlta, numIntern, fecha, hora);
                    try {
                        if (dao.buscar(numAlta) == null) { dao.insertar(a); cont++; System.out.println("[ALTA] Insertado: " + numAlta); } else { dao.actualizar(a); System.out.println("[ALTA] Actualizado: " + numAlta); }
                    } catch (SQLException sq) {
                        System.err.println("SQLException al migrar alta " + numAlta + ": " + sq.getMessage());
                        sq.printStackTrace();
                    }
                } catch (Exception ex) {
                    System.out.println("Error parseando alta: " + ex.getMessage() + " -> " + linea);
                    ex.printStackTrace();
                }
            }
            System.out.println("Altas insertadas: " + cont);
        } catch (IOException e) {
            System.out.println("No se pudo leer " + f.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}