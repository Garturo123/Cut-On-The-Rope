package Usuarios;

public class SessionManager {

    private Usuario usuarioActual;

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void iniciarSesion(
        Usuario usuario
    ) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }
}