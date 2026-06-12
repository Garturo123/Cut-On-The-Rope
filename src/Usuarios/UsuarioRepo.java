/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Usuarios;

import java.io.*;
import java.util.ArrayList;

public class UsuarioRepo {
    private static final String RUTA_BASE = "data/usuarios/";
    
    public UsuarioRepo() {
        new File(RUTA_BASE).mkdirs();
    }
    
    public boolean existe(String username) {
        return new File(rutaArchivo(username)).exists();
    }
    
    public Usuario cargar(String username) {

    try (ObjectInputStream in =
            new ObjectInputStream(
                new FileInputStream(
                    rutaArchivo(username)))) {

        return (Usuario) in.readObject();

    } catch (Exception e) {

        System.err.println(
            "Error cargando usuario "
            + username + ": "
            + e.getMessage());

        return null;
    }
}
    
    public void guardar(Usuario usuario) {
        try {
            new File(RUTA_BASE + usuario.getUsername()).mkdirs();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rutaArchivo(usuario.getUsername())))) {
                out.writeObject(usuario);
            }
        } catch (Exception e) {
            System.err.println("Error guardando usuario: " + e.getMessage());
        }
    }
    
    public ArrayList<Usuario> cargarTodos() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        File[] carpetas = new File(RUTA_BASE).listFiles();
        
        if (carpetas != null) {
            for (File carpeta : carpetas) {
                if (carpeta.isDirectory()) {
                    Usuario u = cargar(carpeta.getName());
                    if (u != null) usuarios.add(u);
                }
            }
        }
        return usuarios;
    }
    
    public boolean eliminarCarpeta(String username) {
        return eliminarRecursivamente(new File(RUTA_BASE + username));
    }
    
    private String rutaArchivo(String username) {
        return RUTA_BASE + username + "/usuario.dat";
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