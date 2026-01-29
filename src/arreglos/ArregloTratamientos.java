package arreglos;

import java.io.*;
import java.util.ArrayList;
import clases.Tratamiento;
import persistencia.TratamientoDAO;
import persistencia.DBConnection;
import java.sql.SQLException;

public class ArregloTratamientos {
	private static final String RUTA_ARCHIVO = System.getProperty("user.dir") + File.separator + "tratamientos.txt";
    private ArrayList<Tratamiento> tratamientos;
    private boolean useDB = false;
    private TratamientoDAO tratamientoDAO = null;

    public ArregloTratamientos() {
        tratamientos = new ArrayList<Tratamiento>();
        try { useDB = DBConnection.isAvailable(); } catch (Exception e) { useDB = false; }
        if (useDB) tratamientoDAO = new TratamientoDAO();
        cargarTratamientos();
    }

    public void adicionar(Tratamiento x) {
        tratamientos.add(x);
        if (useDB && tratamientoDAO != null) {
            try { tratamientoDAO.insertar(x); } catch (SQLException e) {
                System.out.println("Error al insertar tratamiento en BD, guardando en .txt: " + e.getMessage());
                grabarTratamientos();
            }
        } else grabarTratamientos();
    }

    public void eliminar(Tratamiento x) {
        tratamientos.remove(x);
        if (useDB && tratamientoDAO != null) {
            try { tratamientoDAO.eliminar(x.getCodTratamiento()); } catch (SQLException e) {
                System.out.println("Error al eliminar tratamiento en BD, actualizando .txt: " + e.getMessage());
                grabarTratamientos();
            }
        } else grabarTratamientos();
    }

    public int tamanio() {
        return tratamientos.size();
    }

    public Tratamiento obtener(int i) {
        return tratamientos.get(i);
    }

    public Tratamiento buscar(int codigo) {
        // Prefer returning the in-memory instance so GUI modifications affect the list.
        for (Tratamiento t : tratamientos) if (t.getCodTratamiento() == codigo) return t;
        // If not found in memory, and DB is available, try DB and add to memory for future consistency.
        if (useDB && tratamientoDAO != null) {
            try {
                Tratamiento t = tratamientoDAO.buscar(codigo);
                if (t != null) {
                    tratamientos.add(t);
                    return t;
                }
            } catch (SQLException e) {
                System.out.println("Error al buscar tratamiento en BD, usando cache: " + e.getMessage());
            }
        }
        return null;
    }

    public int codigoCorrelativo() {
        if (useDB && tratamientoDAO != null) {
            try {
                Integer max = tratamientoDAO.maxCodCorrelativo();
                if (max == null) return 101;
                return max + 1;
            } catch (SQLException e) {
                System.out.println("Error al obtener correlativo desde BD, usando archivos: " + e.getMessage());
            }
        }
        if (tratamientos.isEmpty()) return 101;
        return tratamientos.get(tratamientos.size() - 1).getCodTratamiento() + 1;
    }

    public void actualizarArchivo() {
        if (useDB && tratamientoDAO != null) {
            try {
                for (Tratamiento t : tratamientos) tratamientoDAO.actualizar(t);
            } catch (SQLException e) {
                System.out.println("Error al actualizar tratamientos en BD, guardando en .txt: " + e.getMessage());
                grabarTratamientos();
            }
        } else grabarTratamientos();
    }

    private void grabarTratamientos() {
        File archivo = new File(RUTA_ARCHIVO);
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            for (Tratamiento t : tratamientos) {
                String linea = t.getCodTratamiento() + ";" +
                               t.getNombreTratamiento() + ";" +
                               t.getDuracionDias() + ";" +
                               t.getSesiones() + ";" +
                               t.getCosto();
                pw.println(linea);
            }
            System.out.println("Archivo de tratamientos guardado en: " + archivo.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error al grabar archivo de tratamientos: " + e.getMessage());
        }
    }

    private void cargarTratamientos() {
        File archivo = new File(RUTA_ARCHIVO);
        System.out.println("Intentando cargar tratamientos desde: " + archivo.getAbsolutePath());
        if (useDB && tratamientoDAO != null) {
            try {
                tratamientos = tratamientoDAO.listar();
                System.out.println("Tratamientos cargados desde BD: " + tratamientos.size());
                return;
            } catch (SQLException e) {
                System.out.println("No se pudo cargar tratamientos desde BD (se usará archivo): " + e.getMessage());
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] s = linea.split(";");
                int codTratamiento = Integer.parseInt(s[0].trim());
                String nombreTratamiento = s[1].trim();
                int duracionDias = Integer.parseInt(s[2].trim());
                int sesiones = Integer.parseInt(s[3].trim());
                double costo = Double.parseDouble(s[4].trim());
                tratamientos.add(new Tratamiento(codTratamiento, nombreTratamiento, duracionDias, sesiones, costo));
            }
            System.out.println("Tratamientos cargados correctamente: " + tratamientos.size());
        } catch (Exception e) {
            System.out.println("No se pudo cargar archivo de tratamientos (" + archivo.getAbsolutePath() + ")");
        }
    }
}