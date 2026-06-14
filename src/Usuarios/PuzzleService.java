package Usuarios;

import java.util.ArrayList;

public class PuzzleService {

    private final NivelRepo repo;
    private final SessionManager session;
    private final ActividadLogger logger;

    public PuzzleService(NivelRepo repo, SessionManager session, ActividadLogger logger) {
        this.repo = repo;
        this.session = session;
        this.logger = logger;
    }

    public ArrayList<Niveles> obtenerPuzzles() {
        Usuario usuario = session.getUsuarioActual();

        if (usuario == null)
            return new ArrayList<>();

        return repo.cargar(usuario.getUsername());
    }

    public boolean puedeJugarNivel(int nivel) {
        Usuario usuario = session.getUsuarioActual();

        return usuario != null
            && nivel >= 1
            && nivel <= usuario.getNivelDesbloqueado();
    }

    public boolean nivelCompletado(int nivel) {
        return obtenerPuzzles()
            .stream()
            .anyMatch(p -> p.getNivel() == nivel && p.isCompletado());
    }

    public String completarNivel(int nivel, int puntaje, long tiempo) {
        return completarNivel(nivel, puntaje, tiempo, 0);
    }

    public String completarNivel(int nivel, int puntaje, long tiempo, int estrellas) {
        Usuario usuario = session.getUsuarioActual();

        if (usuario == null)
            return "Sin sesion";

        ArrayList<Niveles> puzzles = obtenerPuzzles();

        for (Niveles p : puzzles) {
            if (p.getNivel() == nivel) {
                p.completarNivel(puntaje, tiempo, estrellas);
                repo.guardar(usuario.getUsername(), puzzles);

                logger.registrar(
                    usuario.getUsername(),
                    "game_activity.dat",
                    "Nivel " + nivel + " completado con " + estrellas + " estrellas"
                );

                return "Nivel completado";
            }
        }

        return "Nivel no encontrado";
    }
}