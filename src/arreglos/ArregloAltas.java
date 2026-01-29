package arreglos;

import java.io.*;
import java.util.ArrayList;
import clases.Alta;
import persistencia.AltaDAO;
import persistencia.DBConnection;
import java.sql.SQLException;

public class ArregloAltas {
    private ArrayList<Alta> altas;
    private static final String RUTA_ARCHIVO = System.getProperty("user.dir") + File.separator + "altas.txt";
    private boolean useDB = false;
    private AltaDAO altaDAO = null;

    public ArregloAltas() {
        altas = new ArrayList<Alta>();
        try { useDB = DBConnection.isAvailable(); } catch (Exception e) { useDB = false; }
        if (useDB) altaDAO = new AltaDAO();
        cargarAltas();
    }

    public void adicionar(Alta x) {
        altas.add(x);
        if (useDB && altaDAO != null) {
            try { altaDAO.insertar(x); } catch (SQLException e) {
                System.out.println("Error al insertar alta en BD, guardando en .txt: " + e.getMessage());
                grabarAltas();
            }
        } else grabarAltas();
    }

    public void eliminar(Alta x) {
        altas.remove(x);
        if (useDB && altaDAO != null) {
            try { altaDAO.eliminar(x.getNumAlta()); } catch (SQLException e) {
                System.out.println("Error al eliminar alta en BD, actualizando .txt: " + e.getMessage());
                grabarAltas();
            }
        } else grabarAltas();
    }

    public int tamanio() {
        return altas.size();
    }

    public Alta obtener(int i) {
        return altas.get(i);
    }

    public Alta buscar(int codigo) {
        if (useDB && altaDAO != null) {
            try {
                Alta a = altaDAO.buscar(codigo);
                if (a != null) return a;
            } catch (SQLException e) {
                System.out.println("Error al buscar alta en BD, usando cache: " + e.getMessage());
            }
        }
        for (Alta a : altas) if (a.getNumAlta() == codigo) return a;
        return null;
    }

    public int codigoCorrelativo() {
        if (useDB && altaDAO != null) {
            try {
                Integer max = altaDAO.maxCodCorrelativo();
                if (max == null) return 200001;
                return max + 1;
            } catch (SQLException e) {
                System.out.println("Error al obtener correlativo desde BD, usando archivos: " + e.getMessage());
            }
        }
        if (altas.isEmpty()) return 200001;
        return altas.get(altas.size() - 1).getNumAlta() + 1;
    }

    public void actualizarArchivo() {
        if (useDB && altaDAO != null) {
            try {
                for (Alta a : altas) altaDAO.actualizar(a);
            } catch (SQLException e) {
                System.out.println("Error al actualizar altas en BD, guardando en .txt: " + e.getMessage());
                grabarAltas();
            }
        } else grabarAltas();
    }

    private void grabarAltas() {
        File archivo = new File(RUTA_ARCHIVO);
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            for (Alta x : altas) {
                String linea = x.getNumAlta() + ";" +
                               x.getNumInternamiento() + ";" +
                               x.getFecha() + ";" +
                               x.getHora();
                pw.println(linea);
            }
            System.out.println(" Archivo de altas guardado en: " + archivo.getAbsolutePath());
        } catch (Exception e) {
            System.out.println(" Error al grabar archivo de altas: " + e.getMessage());
        }
    }

    private void cargarAltas() {
        File archivo = new File(RUTA_ARCHIVO);
        System.out.println("Intentando cargar altas desde: " + archivo.getAbsolutePath());
        if (useDB && altaDAO != null) {
            try {
                altas = altaDAO.listar();
                System.out.println("Altas cargadas desde BD: " + altas.size());
                return;
            } catch (SQLException e) {
                System.out.println("No se pudo cargar altas desde BD (se usará archivo): " + e.getMessage());
            }
        }
        try {
            if (!archivo.exists()) {
                archivo.createNewFile();
                System.out.println("Archivo 'altas.txt' no existía, se creó vacío.");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] s = linea.split(";");
                    int numAlta = Integer.parseInt(s[0].trim());
                    int numInternamiento = Integer.parseInt(s[1].trim());
                    String fecha = s[2].trim();
                    String hora = s[3].trim();
                    altas.add(new Alta(numAlta, numInternamiento, fecha, hora));
                }
                System.out.println("Altas cargadas correctamente: " + altas.size());
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar archivo de altas: " + e.getMessage());
        }
    }
}