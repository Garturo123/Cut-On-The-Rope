package ctr;

import java.util.HashMap;
import java.util.Map;

public final class I18n {
    private static final String ENGLISH = "English";
    private static final String SPANISH = "Spanish";
    private static String language = ENGLISH;
    private static final Map<String, String> en = new HashMap<String, String>();
    private static final Map<String, String> es = new HashMap<String, String>();

    static {
        put("play", "Play", "Jugar");
        put("profile", "Profile", "Perfil");
        put("audio", "Audio", "Audio");
        put("friends", "Friends", "Amigos");
        put("challenges", "Challenges", "Retos");
        put("stats", "Stats", "Estadisticas");
        put("social_menu", "Social", "Social");
        put("settings", "Settings", "Ajustes");
        put("logout", "Logout", "Salir sesion");
        put("login", "Login", "Iniciar");
        put("register", "Register", "Registro");
        put("exit", "Exit", "Salir");
        put("back", "Back", "Volver");
        put("save", "Save", "Guardar");
        put("avatar", "Avatar", "Avatar");
        put("language", "Language", "Idioma");
        put("english", "English", "Ingles");
        put("spanish", "Spanish", "Espanol");
        put("settings_title", "SETTINGS", "AJUSTES");
        put("language_title", "LANGUAGE", "IDIOMA");
        put("select_avatar", "SELECT AVATAR", "ELEGIR AVATAR");
        put("border_color", "Border Color:", "Color de borde:");
        put("preview", "Preview:", "Vista:");
        put("audio_settings", "AUDIO SETTINGS", "AJUSTES DE AUDIO");
        put("sfx_volume", "SFX Volume:", "Volumen SFX:");
        put("music_volume", "Music Volume:", "Volumen musica:");
        put("mute", "Mute", "Silenciar");
        put("unmute", "Unmute", "Sonido");
        put("pause", "Pause", "Pausa");
        put("resume", "Resume", "Continuar");
        put("restart", "Restart", "Reiniciar");
        put("exit_level", "Exit level", "Salir nivel");
        put("level_selector", "Levels", "Niveles");
        put("audio_saved", "Audio settings saved!", "Audio guardado");
        put("menu_welcome", "Welcome, ", "Bienvenido, ");
        put("friends_title", "FRIENDS", "AMIGOS");
        put("add_friend", "Add", "Agregar");
        put("accept", "Accept", "Aceptar");
        put("challenge", "Challenge", "Reto");
        put("friend_username", "Username", "Usuario");
        put("no_friends", "No friends added yet.", "No hay amigos agregados.");
        put("select_friend", "Select a friend first", "Elige un amigo primero");
        put("select_request", "Select a request first", "Elige una solicitud primero");
        put("friend_requests", "Friend requests", "Solicitudes");
        put("no_requests", "No pending requests", "Sin solicitudes pendientes");
        put("pending_request", "Pending request", "Solicitud pendiente");
        put("players_title", "PLAYERS", "JUGADORES");
        put("ranking_title", "GLOBAL RANKING", "RANKING GLOBAL");
        put("ranking_rules_1", "World ranking is ordered by completed levels.", "El ranking mundial se ordena por niveles completados.");
        put("ranking_rules_2", "Stars break ties; friendly challenges require accepted friends.", "Las estrellas desempatan; los retos requieren amistad aceptada.");
        put("friendly_challenge", "FRIENDLY CHALLENGE", "RETO AMISTOSO");
        put("challenge_rules_1", "Play the same 10 levels in 2 minutes.", "Juegan los mismos 10 niveles en 2 minutos.");
        put("challenge_rules_2", "More completed levels wins; stars break ties.", "Gana quien pase mas niveles; estrellas desempatan.");
        put("challenge_win", "You won:", "Ganaste:");
        put("challenge_loss", "You lost:", "Perdiste:");
        put("challenge_tie", "Tie:", "Empate:");
        put("no_players", "No other players yet.", "No hay otros jugadores.");
        put("select_player", "Select a player first", "Elige un jugador primero");
        put("cant_challenge_self", "Choose another player", "Elige otro jugador");
        put("friend", "Friend", "Amigo");
        put("player", "Player", "Jugador");
        put("you", "You", "Tu");
        put("challenge_sent", "Challenge sent", "Reto enviado");
        put("score", "Score", "Puntos");
        put("stars", "Stars", "Estrellas");
        put("level_select", "SELECT LEVEL", "ELEGIR NIVEL");
        put("level", "Level", "Nivel");
        put("unlocked", "Unlocked", "Abierto");
        put("locked", "Locked", "Bloqueado");
        put("complete_previous", "Complete the previous level first", "Completa el nivel anterior");
        put("stats_title", "STATS", "ESTADISTICAS");
        put("total_score", "Total Score: ", "Puntos totales: ");
        put("levels_completed", "Levels Completed: ", "Niveles completados: ");
        put("challenges_won", "Challenges Won: ", "Retos ganados: ");
        put("unlocked_level", "Unlocked Level: ", "Nivel abierto: ");
        put("no_session", "No active session.", "Sin sesion activa.");
        put("reactivate", "Reactivate", "Reactivar");
        put("deactivate_account", "Deactivate", "Desactivar");
        put("confirm_deactivate", "Click again to deactivate", "Presiona otra vez");
        put("account_deactivated", "Account deactivated", "Cuenta desactivada");
        put("reactivate_title", "REACTIVATE ACCOUNT", "REACTIVAR CUENTA");
        put("complete_fields", "Complete all fields", "Completa todos los campos");
        put("tutorial_1", "Cut the ropes and use the air", "Corta las cuerdas y usa el aire");
        put("tutorial_2", "Collect as many stars as you can", "Obten tantas estrellas como puedas");
        put("tutorial_3", "Try cutting one rope at a time", "Prueba cortar una cuerda a la vez");
        put("tutorial_4", "Use the swing to aim the candy", "Usa el balanceo para apuntar el caramelo");
        put("tutorial_5", "Bubbles lift the candy", "Las burbujas elevaran el caramelo");
        put("tutorial_6", "Do not let the candy leave the box", "No dejes que el caramelo salga de la caja");
        put("tutorial_7", "Plan the last cuts carefully", "Planea bien los ultimos cortes");
        put("tutorial_8", "Avoid the spikes with short swings", "Evita los picos con balanceos cortos");
        put("tutorial_9", "Use the bubble before the final drop", "Usa la burbuja antes de la caida final");
        put("tutorial_10", "Chain the ropes one by one", "Encadena las cuerdas una por una");
    }

    private I18n() {
    }

    public static String t(String key) {
        Map<String, String> active = isSpanish() ? es : en;
        String value = active.get(key);
        return value != null ? value : key;
    }

    public static void setLanguage(String newLanguage) {
        language = isSpanishName(newLanguage) ? SPANISH : ENGLISH;
    }

    public static String getLanguage() {
        return language;
    }

    public static boolean isSpanish() {
        return isSpanishName(language);
    }

    private static boolean isSpanishName(String value) {
        return value != null
            && (value.equalsIgnoreCase(SPANISH)
             || value.equalsIgnoreCase("Espanol")
             || value.equalsIgnoreCase("Spanish"));
    }

    private static void put(String key, String english, String spanish) {
        en.put(key, english);
        es.put(key, spanish);
    }
}
