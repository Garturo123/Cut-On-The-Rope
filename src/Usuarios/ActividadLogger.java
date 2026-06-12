package Usuarios;

import java.io.*;
import java.util.ArrayList;


public class ActividadLogger {
    private static final String RUTA_BASE = "data/usuarios/";
    
    public void registrar(String username, String tipoArchivo, String mensaje) {
        ArrayList<Actividad> actividades = obtener(username, tipoArchivo);  // ← cambiado de cargar a obtener
        actividades.add(new Actividad(mensaje));
        
        try {
            new File(RUTA_BASE + username).mkdirs();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rutaArchivo(username, tipoArchivo)))) {
                out.writeObject(actividades);
            }
        } catch (Exception e) {
            System.err.println("Error guardando actividad: " + e.getMessage());
        }
    }
    
    public ArrayList<Actividad> obtener(String username, String tipoArchivo) {
        File archivo = new File(rutaArchivo(username, tipoArchivo));
        if (!archivo.exists()) {
            return new ArrayList<>();  // Si no existe, retorna lista vacía
        }
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (ArrayList<Actividad>) in.readObject();
        } catch (Exception e) {
            System.err.println("Error cargando actividad: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private String rutaArchivo(String username, String archivo) {
        return RUTA_BASE + username + "/" + archivo;
    }
}