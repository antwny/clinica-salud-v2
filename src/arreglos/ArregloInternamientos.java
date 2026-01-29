package arreglos;

import java.io.*;
import java.util.ArrayList;
import clases.Internamiento;
import persistencia.InternamientoDAO;
import persistencia.DBConnection;
import java.sql.SQLException;

public class ArregloInternamientos {
    private ArrayList<Internamiento> internamientos;
    private static final String RUTA_ARCHIVO = System.getProperty("user.dir") + File.separator + "internamientos.txt";
    private boolean useDB = false;
    private InternamientoDAO internamientoDAO = null;

    public ArregloInternamientos() {
        internamientos = new ArrayList<Internamiento>();
        try { useDB = DBConnection.isAvailable(); } catch (Exception e) { useDB = false; }
        if (useDB) internamientoDAO = new InternamientoDAO();
        cargarInternamientos();
    }

    public void adicionar(Internamiento x) {
        internamientos.add(x);
        if (useDB && internamientoDAO != null) {
            try { internamientoDAO.insertar(x); } catch (SQLException e) {
                System.out.println("Error al insertar internamiento en BD, guardando en .txt: " + e.getMessage());
                grabarInternamientos();
            }
        } else grabarInternamientos();
    }

    public void eliminar(Internamiento x) {
        internamientos.remove(x);
        if (useDB && internamientoDAO != null) {
            try { internamientoDAO.eliminar(x.getNumInternamiento()); } catch (SQLException e) {
                System.out.println("Error al eliminar internamiento en BD, actualizando .txt: " + e.getMessage());
                grabarInternamientos();
            }
        } else grabarInternamientos();
    }

    public int tamanio() {
        return internamientos.size();
    }

    public Internamiento obtener(int i) {
        return internamientos.get(i);
    }

    public Internamiento buscar(int codigo) {
        // Prefer returning the in-memory instance so GUI modifications affect the list.
        for (Internamiento x : internamientos) if (x.getNumInternamiento() == codigo) return x;
        if (useDB && internamientoDAO != null) {
            try {
                Internamiento x = internamientoDAO.buscar(codigo);
                if (x != null) {
                    internamientos.add(x);
                    return x;
                }
            } catch (SQLException e) {
                System.out.println("Error al buscar internamiento en BD, usando cache: " + e.getMessage());
            }
        }
        return null;
    }

    public Internamiento buscarPorPaciente(int codPaciente) {
        for (Internamiento x : internamientos) {
            if (x.getCodPaciente() == codPaciente)
                return x;
        }
        return null;
    }

    public int codigoCorrelativo() {
        if (useDB && internamientoDAO != null) {
            try {
                Integer max = internamientoDAO.maxCodCorrelativo();
                if (max == null) return 100001;
                return max + 1;
            } catch (SQLException e) {
                System.out.println("Error al obtener correlativo desde BD, usando archivos: " + e.getMessage());
            }
        }
        if (internamientos.isEmpty()) return 100001;
        return internamientos.get(internamientos.size() - 1).getNumInternamiento() + 1;
    }

    public void actualizarArchivo() {
        if (useDB && internamientoDAO != null) {
            try {
                for (Internamiento x : internamientos) internamientoDAO.actualizar(x);
            } catch (SQLException e) {
                System.out.println("Error al actualizar internamientos en BD, guardando en .txt: " + e.getMessage());
                grabarInternamientos();
            }
        } else grabarInternamientos();
    }

    private void grabarInternamientos() {
        File archivo = new File(RUTA_ARCHIVO);
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            for (Internamiento x : internamientos) {
                String linea = x.getNumInternamiento() + ";" +
                               x.getCodPaciente() + ";" +
                               x.getCodTratamiento() + ";" +
                               x.getFecha() + ";" +
                               x.getHora();
                pw.println(linea);
            }
            System.out.println("Archivo de internamientos guardado en: " + archivo.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error al grabar archivo de internamientos: " + e.getMessage());
        }
    }

    private void cargarInternamientos() {
        File archivo = new File(RUTA_ARCHIVO);
        System.out.println("Intentando cargar internamientos desde: " + archivo.getAbsolutePath());
        if (useDB && internamientoDAO != null) {
            try {
                internamientos = internamientoDAO.listar();
                System.out.println("Internamientos cargados desde BD: " + internamientos.size());
                return;
            } catch (SQLException e) {
                System.out.println("No se pudo cargar internamientos desde BD (se usará archivo): " + e.getMessage());
            }
        }
        try {
            if (!archivo.exists()) {
                archivo.createNewFile();
                System.out.println("Archivo 'internamientos.txt' no existía, se creó vacío.");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] s = linea.split(";");
                    int numInternamiento = Integer.parseInt(s[0].trim());
                    int codPaciente = Integer.parseInt(s[1].trim());
                    int codTratamiento = Integer.parseInt(s[2].trim());
                    String fecha = s[3].trim();
                    String hora = s[4].trim();
                    internamientos.add(new Internamiento(numInternamiento, codPaciente, codTratamiento, fecha, hora));
                }
                System.out.println("Internamientos cargados correctamente: " + internamientos.size());
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar archivo de internamientos: " + e.getMessage());
        }
    }
}