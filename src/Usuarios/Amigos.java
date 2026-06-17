
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
    
    public String solicitarAmistad(Usuario actual, String usernameAmigo) {
        String usernameLimpio = limpiar(usernameAmigo);
        
        if (actual.getUsername().equals(usernameLimpio)) return "No puede agregarse a sí mismo";
        if (!usuarioRepo.existe(usernameLimpio)) return "El usuario no existe";
        if (actual.getAmigosRivales().contains(usernameLimpio)) return "Ya son amigos";
        if (actual.getSolicitudesAmistadEnviadas().contains(usernameLimpio)) return "Solicitud ya enviada";
        
        Usuario amigo = usuarioRepo.cargar(usernameLimpio);
        if (amigo == null) return "El usuario no existe";

        if (actual.getSolicitudesAmistadRecibidas().contains(usernameLimpio)) {
            return aceptarSolicitud(actual, usernameLimpio);
        }

        actual.agregarSolicitudEnviada(usernameLimpio);
        amigo.agregarSolicitudRecibida(actual.getUsername());
        usuarioRepo.guardar(actual);
        usuarioRepo.guardar(amigo);
        
        logger.registrar(actual.getUsername(), "account_activity.dat", "Envió solicitud a " + usernameLimpio);
        logger.registrar(usernameLimpio, "account_activity.dat", "Recibió solicitud de " + actual.getUsername());
        return "Solicitud enviada";
    }

    public String aceptarSolicitud(Usuario actual, String usernameSolicitante) {
        String solicitanteLimpio = limpiar(usernameSolicitante);

        if (!actual.getSolicitudesAmistadRecibidas().contains(solicitanteLimpio)) {
            return "No hay solicitud de ese usuario";
        }

        Usuario solicitante = usuarioRepo.cargar(solicitanteLimpio);
        if (solicitante == null) return "El usuario no existe";

        actual.eliminarSolicitudRecibida(solicitanteLimpio);
        actual.agregarAmigoRival(solicitanteLimpio);
        solicitante.eliminarSolicitudEnviada(actual.getUsername());
        solicitante.agregarAmigoRival(actual.getUsername());

        usuarioRepo.guardar(actual);
        usuarioRepo.guardar(solicitante);

        logger.registrar(actual.getUsername(), "account_activity.dat", "Aceptó solicitud de " + solicitanteLimpio);
        logger.registrar(solicitanteLimpio, "account_activity.dat", actual.getUsername() + " aceptó la solicitud");
        return "Solicitud aceptada";
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
            .filter(u -> !actual.getSolicitudesAmistadEnviadas().contains(u))
            .filter(u -> filtroLimpio.isEmpty() || u.contains(filtroLimpio))
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase();
    }
}
