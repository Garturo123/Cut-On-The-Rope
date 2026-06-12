package Usuarios;

import java.util.ArrayList;

public class ActivityService {

    private final ActividadLogger logger;
    private final SessionManager session;

    public ActivityService(
        ActividadLogger logger,
        SessionManager session
    ) {
        this.logger = logger;
        this.session = session;
    }

    public ArrayList<Actividad>
        obtenerActividadCuenta() {

        Usuario usuario =
            session.getUsuarioActual();

        return logger.obtener(
            usuario.getUsername(),
            "account_activity.dat"
        );
    }

    public ArrayList<Actividad>
        obtenerActividadJuego() {

        Usuario usuario =
            session.getUsuarioActual();

        return logger.obtener(
            usuario.getUsername(),
            "game_activity.dat"
        );
    }
}
