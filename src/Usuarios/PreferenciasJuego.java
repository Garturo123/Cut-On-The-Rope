/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Usuarios;
import java.io.Serializable;

public class PreferenciasJuego implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private float volumenMusica;
    private float volumenEfectos;
    
    
    public PreferenciasJuego() {
        this.volumenMusica = 0.7f;
        this.volumenEfectos = 0.8f;
        
    }
    
    // Getters y setters
    public float getVolumenMusica() { return volumenMusica; }
    public void setVolumenMusica(float volumenMusica) { this.volumenMusica = volumenMusica; }
    public float getVolumenEfectos() { return volumenEfectos; }
    public void setVolumenEfectos(float volumenEfectos) { this.volumenEfectos = volumenEfectos; }
    
}


