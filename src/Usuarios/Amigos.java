
package Usuarios;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Amigos {
    private final UsuarioRepo usuarioRepo;
    private final ActividadLogger logger;
    
    public Amigos(UsuarioRepo usuarioRepo, ActividadLogger logger) {
        this.usuarioRepo = usuarioRepo;
        this.logger = logger;
    }
    
    public String agregarBidireccional(Usuario actual, String usernameAmigo) {
        String usernameLimpio = limpiar(usernameAmigo);
        
        if (actual.getUsername().equals(usernameLimpio)) return "No puede agregarse a sí mismo";
        if (!usuarioRepo.existe(usernameLimpio)) return "El usuario no existe";
        
        actual.agregarAmigoRival(usernameLimpio);
        usuarioRepo.guardar(actual);
        
        Usuario amigo = usuarioRepo.cargar(usernameLimpio);
        if (amigo != null) {
            amigo.agregarAmigoRival(actual.getUsername());
            usuarioRepo.guardar(amigo);
        }
        
        logger.registrar(actual.getUsername(), "account_activity.dat", "Agregó a " + usernameLimpio + " como amigo");
        return "Amigo agregado correctamente";
    }
    
    public String eliminar(Usuario actual, ArrayList<String> amigos) {
        if (amigos == null || amigos.isEmpty()) return "Seleccione al menos un amigo";
        
        for (String amigo : amigos) {
            String amigoLimpio = limpiar(amigo);
            actual.eliminarAmigoRival(amigoLimpio);
            
            Usuario usuarioAmigo = usuarioRepo.cargar(amigoLimpio);
            if (usuarioAmigo != null) {
                usuarioAmigo.eliminarAmigoRival(actual.getUsername());
                usuarioRepo.guardar(usuarioAmigo);
            }
        }
        usuarioRepo.guardar(actual);
        return "Amigo(s) eliminado(s) correctamente";
    }
    
    public ArrayList<String> buscarParaAgregar(Usuario actual, String filtro) {
        String filtroLimpio = limpiar(filtro);
        ArrayList<String> amigos = actual.getAmigosRivales();
        
        return usuarioRepo.cargarTodos().stream()
            .map(Usuario::getUsername)
            .filter(u -> !u.equals(actual.getUsername()))
            .filter(u -> !amigos.contains(u))
            .filter(u -> filtroLimpio.isEmpty() || u.contains(filtroLimpio))
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase();
    }
}
