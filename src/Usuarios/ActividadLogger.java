package Usuarios;

import java.io.*;
import java.util.ArrayList;


public class ActividadLogger {
    private static final String[] RUTAS_BASE = {"build/data/usuarios/", "data/usuarios/"};
    
    public void registrar(String username, String tipoArchivo, String mensaje) {
        ArrayList<Actividad> actividades = obtener(username, tipoArchivo);  // ← cambiado de cargar a obtener
        actividades.add(new Actividad(mensaje));
        
        for (String rutaBase : RUTAS_BASE) {
            try {
                File carpeta = new File(rutaBase + username);
                carpeta.mkdirs();
                if (!carpeta.isDirectory()) {
                    continue;
                }
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rutaArchivo(rutaBase, username, tipoArchivo)))) {
                    out.writeObject(actividades);
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error guardando actividad en " + rutaBase + ": " + e.getMessage());
            }
        }
    }
    
    public ArrayList<Actividad> obtener(String username, String tipoArchivo) {
        File archivo = archivoExistente(username, tipoArchivo);
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
    
    private String rutaArchivo(String rutaBase, String username, String archivo) {
        return rutaBase + username + "/" + archivo;
    }

    private File archivoExistente(String username, String tipoArchivo) {
        for (String rutaBase : RUTAS_BASE) {
            File archivo = new File(rutaArchivo(rutaBase, username, tipoArchivo));
            if (archivo.exists()) {
                return archivo;
            }
        }
        return new File(rutaArchivo(RUTAS_BASE[0], username, tipoArchivo));
    }
}
