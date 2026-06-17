/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Usuarios;

import java.io.*;
import java.util.ArrayList;

public class UsuarioRepo {
    private static final String[] RUTAS_BASE = {"build/data/usuarios/", "data/usuarios/"};
    
    public UsuarioRepo() {
        new File(RUTAS_BASE[0]).mkdirs();
    }
    
    public boolean existe(String username) {
        return archivoExistente(username) != null;
    }
    
    public Usuario cargar(String username) {
        File archivo = archivoExistente(username);
        if (archivo == null) {
            return null;
        }

    try (ObjectInputStream in =
            new ObjectInputStream(
                new FileInputStream(
                    archivo))) {

        return (Usuario) in.readObject();

    } catch (Exception e) {

        System.err.println(
            "Error cargando usuario "
            + username + ": "
            + e.getMessage());

        return null;
    }
}
    
    public boolean guardar(Usuario usuario) {
        if (usuario == null) return false;
        for (String rutaBase : RUTAS_BASE) {
            try {
                File carpeta = new File(rutaBase + usuario.getUsername());
                carpeta.mkdirs();
                if (!carpeta.isDirectory()) {
                    continue;
                }
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rutaArchivo(rutaBase, usuario.getUsername())))) {
                    out.writeObject(usuario);
                    return true;
                }
            } catch (Exception e) {
                System.err.println("Error guardando usuario en " + rutaBase + ": " + e.getMessage());
            }
        }
        return false;
    }
    
    public ArrayList<Usuario> cargarTodos() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        ArrayList<String> usernames = new ArrayList<>();
        for (String rutaBase : RUTAS_BASE) {
            File[] carpetas = new File(rutaBase).listFiles();
            if (carpetas != null) {
                for (File carpeta : carpetas) {
                    if (carpeta.isDirectory() && !usernames.contains(carpeta.getName())) {
                        usernames.add(carpeta.getName());
                    }
                }
            }
        }
        for (String username : usernames) {
            Usuario u = cargar(username);
            if (u != null) usuarios.add(u);
        }
        return usuarios;
    }
    
    public boolean eliminarCarpeta(String username) {
        boolean eliminado = true;
        for (String rutaBase : RUTAS_BASE) {
            eliminado = eliminarRecursivamente(new File(rutaBase + username)) && eliminado;
        }
        return eliminado;
    }
    
    private String rutaArchivo(String rutaBase, String username) {
        return rutaBase + username + "/usuario.dat";
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
    
    
    private boolean eliminarRecursivamente(File archivo) {
        if (archivo == null || !archivo.exists()) return true;
        
        if (archivo.isDirectory()) {
            File[] hijos = archivo.listFiles();
            if (hijos != null) {
                for (File hijo : hijos) {
                    if (!eliminarRecursivamente(hijo)) return false;
                }
            }
        }
        return archivo.delete();
    }

}
