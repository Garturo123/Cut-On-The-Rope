package Usuarios;

public class UsuarioService {

    private final UsuarioRepo repo;
    private final SessionManager session;

    public UsuarioService(
        UsuarioRepo repo,
        SessionManager session
    ) {
        this.repo = repo;
        this.session = session;
    }

    public void actualizarAvatar(String avatar) {

        Usuario u =
            session.getUsuarioActual();

        u.setAvatar(avatar);

        repo.guardar(u);
    }
    
}