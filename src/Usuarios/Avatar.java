package Usuarios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Avatar {
    private final List<String> coloresDisponibles = Arrays.asList("#a2b794", "#c893c9", "#d99b18", "#b4b4b4", "#205c97");
    
    public String guardar(Usuario usuario, String avatar, String colorHex) {
        if (!obtenerAvataresDisponibles().contains(avatar)) return "Avatar inválido";
        if (!coloresDisponibles.contains(colorHex)) return "Color inválido";
        
        usuario.setAvatar(avatar);
        usuario.setAvatarColorHex(colorHex);
        return "Avatar guardado correctamente";
    }
    
    public String obtenerAvatarActual(Usuario usuario) {
        return (usuario == null || usuario.getAvatar() == null) ? "avatar_1.png" : usuario.getAvatar();
    }
    
    public String obtenerColorActual(Usuario usuario) {
        return (usuario == null || usuario.getAvatarColorHex() == null) ? "#a2b794" : usuario.getAvatarColorHex();
    }
    
    public ArrayList<String> obtenerAvataresDisponibles() {
        ArrayList<String> avatares = new ArrayList<>();
        for (int i = 1; i <= 21; i++) avatares.add("avatar_" + i + ".png");
        return avatares;
    }
    
    public ArrayList<String> obtenerColoresDisponibles() {
        return new ArrayList<>(coloresDisponibles);
    }
}
