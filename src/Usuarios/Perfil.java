
package Usuarios;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Perfil {
    private static final String DEFAULT_AVATAR = "avatar_1.png";
    private static final String DEFAULT_COLOR = "#a2b794";
    
    public String obtenerAvatarPath(Usuario usuario) {
        if (usuario == null || usuario.getAvatar() == null || usuario.getAvatar().equals("default")) {
            return "src/ashley/galatea/progra2/proyecto2/assets/" + DEFAULT_AVATAR;
        }
        return "src/ashley/galatea/progra2/proyecto2/assets/" + usuario.getAvatar();
    }
    
    public String obtenerNombreCompleto(Usuario usuario) {
        return usuario == null ? "USER" : usuario.getNombreCompleto().toUpperCase();
    }
    
    public String obtenerUsername(Usuario usuario) {
        return usuario == null ? "USER" : usuario.getUsername().toUpperCase();
    }
    
    public String obtenerStatus(Usuario usuario) {
        return usuario == null ? "N/A" : (usuario.isCuentaActiva() ? "ACTIVE" : "DISABLED");
    }
    
    public String obtenerFechaRegistro(Usuario usuario) {
        return usuario == null ? "N/A" : formatearFecha(usuario.getFechaRegistro());
    }
    
    public String obtenerUltimoLogin(Usuario usuario) {
        return usuario == null ? "N/A" : formatearFecha(usuario.getUltimaSesion());
    }
    
    public String obtenerScore(Usuario usuario) {
        return usuario == null ? "0" : String.valueOf(usuario.getPuntuacionGeneral());
    }
    
    public String actualizar(Usuario usuario, String avatar, int volumen, String idioma, String controles) {
        if (volumen < 0 || volumen > 100) return "Volumen debe estar entre 0 y 100";
        usuario.actualizarPerfil(avatar, volumen, idioma, controles);
        return "Perfil actualizado correctamente";
    }
    
    private String formatearFecha(Date fecha) {
        return fecha == null ? "N/A" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fecha);
    }
}
