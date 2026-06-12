package Usuarios;

import java.io.*;
import java.util.ArrayList;

public class GestorNiveles {
    private static final String RUTA_BASE = "data/usuarios/";
    
    public ArrayList<Niveles> cargar(String username) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(rutaArchivo(username)))) {
            return (ArrayList<Niveles>) in.readObject();
        } catch (Exception e) {
            return crearPuzzlesIniciales();
        }
    }
    
    public void guardar(String username, ArrayList<Niveles> puzzles) {
        try {
            new File(RUTA_BASE + username).mkdirs();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rutaArchivo(username)))) {
                out.writeObject(puzzles);
            }
        } catch (Exception e) {
            System.err.println("Error guardando puzzles: " + e.getMessage());
        }
    }
    
    private ArrayList<Niveles> crearPuzzlesIniciales() {
        ArrayList<Niveles> puzzles = new ArrayList<>();
        String[] dificultades = {"Neon Circuit", "Power Grid", "Voltage Run", "Electric Drift", "Overload"};
        
        for (int i = 0; i < 10; i++) {
            puzzles.add(new Niveles(i + 1, dificultades[i / 2]));
        }
        return puzzles;
    }
    
    private String rutaArchivo(String username) {
        return RUTA_BASE + username + "/puzzles.dat";
    }
}