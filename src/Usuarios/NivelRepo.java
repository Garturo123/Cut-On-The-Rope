package Usuarios;

import java.io.*;
import java.util.ArrayList;

public class NivelRepo {
    private static final String[] RUTAS_BASE = {"build/data/usuarios/", "data/usuarios/"};
    
    public ArrayList<Niveles> cargar(String username) {
        File archivo = archivoExistente(username);
        if (archivo == null) {
            return crearPuzzlesIniciales();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return completarPuzzlesFaltantes((ArrayList<Niveles>) in.readObject());
        } catch (Exception e) {
            return crearPuzzlesIniciales();
        }
    }
    
    public void guardar(String username, ArrayList<Niveles> puzzles) {
        for (String rutaBase : RUTAS_BASE) {
            try {
                File carpeta = new File(rutaBase + username);
                carpeta.mkdirs();
                if (!carpeta.isDirectory()) {
                    continue;
                }
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rutaArchivo(rutaBase, username)))) {
                    out.writeObject(puzzles);
                    return;
                }
            }
            catch (Exception e) {
                System.err.println("Error guardando puzzles en " + rutaBase + ": " + e.getMessage());
            }
        }
    }
    
    private ArrayList<Niveles> crearPuzzlesIniciales() {
        return completarPuzzlesFaltantes(new ArrayList<Niveles>());
    }

    private ArrayList<Niveles> completarPuzzlesFaltantes(ArrayList<Niveles> puzzles) {
        String[] dificultades = {"Neon Circuit", "Power Grid", "Voltage Run", "Electric Drift", "Overload"};
        
        for (int i = 1; i <= 10; i++) {
            if (!contieneNivel(puzzles, i)) {
                puzzles.add(new Niveles(i, dificultades[(i - 1) / 2]));
            }
        }
        return puzzles;
    }

    private boolean contieneNivel(ArrayList<Niveles> puzzles, int nivel) {
        for (Niveles puzzle : puzzles) {
            if (puzzle.getNivel() == nivel) {
                return true;
            }
        }
        return false;
    }
    
    private String rutaArchivo(String rutaBase, String username) {
        return rutaBase + username + "/puzzles.dat";
    }

    private File archivoExistente(String username) {
        for (String rutaBase : RUTAS_BASE) {
            File archivo = new File(rutaArchivo(rutaBase, username));
            if (archivo.exists()) {
                return archivo;
            }
        }
        return null;
    }
}
