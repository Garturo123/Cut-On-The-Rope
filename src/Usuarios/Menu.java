package Usuarios;

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
}