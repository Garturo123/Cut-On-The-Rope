package Usuarios;

import java.util.ArrayList;

public class Menu {

    private final SessionManager session;

    private final UsuarioRepo usuarioRepo;
    private final NivelRepo puzzleRepo;

    private final Amigos amigosService;
    private final Challenge challengeService;
    private final Perfil perfilService;
    private final Avatar avatarService;

    private final ActividadLogger logger;

    public Menu(
        SessionManager session,
        UsuarioRepo repo
    ) {
        this.session = session;

        this.usuarioRepo = repo;
        this.puzzleRepo = new NivelRepo();

        this.logger = new ActividadLogger();

        this.amigosService = new Amigos(repo, logger);
        this.challengeService = new Challenge(repo, logger);

        this.perfilService = new Perfil();
        this.avatarService = new Avatar();
    }

    private Usuario u() {
        return session.getUsuarioActual();
    }

    public String agregarAmigo(String username) {
        Usuario actual = u();
        if (actual == null) return "Sin sesion";
        return amigosService.solicitarAmistad(actual, username);
    }

    public String aceptarSolicitudAmistad(String username) {
        Usuario actual = u();
        if (actual == null) return "Sin sesion";
        return amigosService.aceptarSolicitud(actual, username);
    }

    public String iniciarChallenge(String usernameRival) {
        Usuario actual = u();
        if (actual == null) return "Sin sesion";
        return challengeService.iniciar(actual, usernameRival, "Neon Circuit");
    }

    public Usuario cargarUsuario(String username) {
        return usuarioRepo.cargar(username);
    }

    public ArrayList<Usuario> cargarTodosUsuarios() {
        return usuarioRepo.cargarTodos();
    }

    public int totalEstrellas(String username) {
        int total = 0;
        for (Niveles nivel : puzzleRepo.cargar(username)) {
            total += nivel.getMejoresEstrellas();
        }
        return total;
    }

    public int nivelesCompletados(String username) {
        int total = 0;
        for (Niveles nivel : puzzleRepo.cargar(username)) {
            if (nivel.isCompletado()) {
                total++;
            }
        }
        return total;
    }
}
