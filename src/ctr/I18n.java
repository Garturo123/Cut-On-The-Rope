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
        put("audio_saved", "Audio settings saved!", "Audio guardado");
        put("menu_welcome", "Welcome, ", "Bienvenido, ");
        put("friends_title", "FRIENDS", "AMIGOS");
        put("add_friend", "Add", "Agregar");
        put("challenge", "Challenge", "Reto");
        put("friend_username", "Username", "Usuario");
        put("no_friends", "No friends added yet.", "No hay amigos agregados.");
        put("select_friend", "Select a friend first", "Elige un amigo primero");
        put("players_title", "PLAYERS", "JUGADORES");
        put("no_players", "No other players yet.", "No hay otros jugadores.");
        put("select_player", "Select a player first", "Elige un jugador primero");
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
        put("reactivate_title", "REACTIVATE ACCOUNT", "REACTIVAR CUENTA");
        put("complete_fields", "Complete all fields", "Completa todos los campos");
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
