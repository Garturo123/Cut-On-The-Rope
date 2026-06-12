package Usuarios;


public class AuthService {

    private final UsuarioRepo repo;
    private final SessionManager session;
    private final ValidadorPassWord passwordValidator = new ValidadorPassWord();
    private final ActividadLogger logger = new ActividadLogger();

    private String idiomaSeleccionadoTemporal;

    public AuthService(
        UsuarioRepo repo,
        SessionManager session
    ) {
        this.repo = repo;
        this.session = session;
    }

    public String login(String username, String password) {

        String usernameLimpio = limpiar(username);

        if (!repo.existe(usernameLimpio))
            return "El usuario no existe";

        Usuario usuario = repo.cargar(usernameLimpio);

        if (usuario == null)
            return "Error al cargar usuario";

        if (!usuario.validarPassword(password))
            return "Contraseña incorrecta";

        if (!usuario.isCuentaActiva())
            return "Cuenta desactivada";

        usuario.iniciarSesion();

        session.iniciarSesion(usuario);

        aplicarIdiomaTemporal();

        repo.guardar(usuario);

        logger.registrar(
            usuario.getUsername(),
            "account_activity.dat",
            "Login exitoso"
        );

        return "Bienvenido";
    }

    public String crearUsuario(String username, String password, String nombreCompleto) {

        String usernameLimpio = limpiar(username);

        if (usernameLimpio.isEmpty()
            || password.isEmpty()
            || nombreCompleto.trim().isEmpty()) {
            return "Complete todos los campos";
        }

        if (repo.existe(usernameLimpio))
            return "Usuario ya existe";

        ValidadorPassWord.ValidationResult result =
            passwordValidator.validar(password);

        if (!result.valido)
            return result.mensaje;

        Usuario nuevo = new Usuario(
            usernameLimpio,
            SecurityUtil.hash(password),
            nombreCompleto.trim()
        );

        if (idiomaSeleccionadoTemporal != null)
            nuevo.setIdioma(idiomaSeleccionadoTemporal);

        repo.guardar(nuevo);

        logger.registrar(
            nuevo.getUsername(),
            "account_activity.dat",
            "Cuenta creada"
        );

        return "Usuario creado correctamente";
    }

    public String logout() {

        Usuario u = session.getUsuarioActual();

        if (u == null)
            return "No hay sesión activa";

        logger.registrar(
            u.getUsername(),
            "account_activity.dat",
            "Logout"
        );

        u.cerrarSesion();

        repo.guardar(u);

        session.cerrarSesion();

        return "Sesión cerrada";
    }

    public void seleccionarIdiomaTemporal(String idioma) {
        idiomaSeleccionadoTemporal =
            (idioma != null &&
            (idioma.equalsIgnoreCase("English")
             || idioma.equalsIgnoreCase("Spanish")))
            ? idioma
            : null;
    }

    private void aplicarIdiomaTemporal() {

        Usuario u = session.getUsuarioActual();

        if (u != null && idiomaSeleccionadoTemporal != null) {
            u.setIdioma(idiomaSeleccionadoTemporal);
            repo.guardar(u);
        }
    }

    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase();
    }
    public String reactivarCuenta(String username, String password) {
        String usernameLimpio = limpiar(username);

        if (!repo.existe(usernameLimpio))
            return "User does not exist";

        Usuario usuario = repo.cargar(usernameLimpio);

        if (usuario == null)
            return "Error loading user";

        if (!usuario.validarPassword(password))
            return "Incorrect password";

        if (usuario.isCuentaActiva())
            return "Account is already active";

        usuario.reactivarCuenta();
        repo.guardar(usuario);

        logger.registrar(
            usuario.getUsername(),
            "account_activity.dat",
            "Account reactivated"
        );

        return "Account reactivated successfully";
    }
}