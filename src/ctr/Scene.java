package ctr;

import static ctr.Scene.GameState.*;
import Audio.Manager;
import Usuarios.ActividadLogger;
import Usuarios.Menu;
import Usuarios.NivelRepo;
import Usuarios.PuzzleService;
import Usuarios.SessionManager;
import Usuarios.Usuario;
import Usuarios.UsuarioRepo;

import ctr.entity.AirCushionEntity;
import ctr.entity.AmigosListEntity;
import ctr.entity.AudioConfigEntity;
import ctr.entity.AvatarSelectorEntity;
import ctr.entity.BackgroundEntity;
import ctr.entity.BubbleEntity;
import ctr.entity.CandyEntity;
import ctr.entity.ChallengeSelectEntity;
import ctr.entity.CurtainEntity;
import ctr.entity.FadeEffectEntity;
import ctr.entity.GameOverEntity;
import ctr.entity.InitializerEntity;
import ctr.entity.LanguageEntity;
import ctr.entity.LevelHudEntity;
import ctr.entity.LevelClearedEntity;
import ctr.entity.LevelSelectEntity;
import ctr.entity.LoginEntity;
import ctr.entity.MenuPrincipalEntity;
import ctr.entity.MenuSesionEntity;
import ctr.entity.PerfilEntity;
import ctr.entity.PetEntity;
import ctr.entity.PinRopeEntity;
import ctr.entity.ReactivateAccountEntity;
import ctr.entity.RegisterEntity;
import ctr.entity.RopeEntity;
import ctr.entity.SpikesEntity;
import ctr.entity.SocialMenuEntity;
import ctr.entity.StarEntity;
import ctr.entity.StatsEntity;
import ctr.entity.SettingsEntity;
import ctr.entity.TitleEntity;
import ctr.model.AirCushion;
import ctr.model.Bubble;
import ctr.model.Model;
import ctr.model.PinRope;
import ctr.model.Rope;
import ctr.model.Spikes;
import ctr.model.Star;
import ctr.ui.TextField;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    public static final int MAX_LEVEL = 10;
    public static final int MAX_STARS = 3;
    public static final int CHALLENGE_LIMIT_SECONDS = 120;
    private static final String MUSIC_INTRO = "ctr_intro.mp3";
    private static final String MUSIC_MENU = "ctr_MainTheme.mp3";
    private static final String MUSIC_GAMEPLAY = "ctr_GameplayTheme1.mp3";
    private static final String MUSIC_RESULTS = "ctr_outro.mp3";

    private final Model model = new Model(View.SCREEN_WIDTH, View.SCREEN_HEIGHT, 10);
    protected List<Entity> entities = new ArrayList<Entity>();
    protected List<Entity> levelEntities = new ArrayList<Entity>();
    private TextField textFieldFocused = null;

    public static enum GameState {
        INITIALIZING, OL_PRESENTS, TITLE, LEVEL_SELECT, READY, PLAYING,
        LEVEL_CLEARED, GAME_OVER,
        MENU_PRINCIPAL, LOGIN, REGISTER, REACTIVATE_ACCOUNT, MENU_SESION, SOCIAL_MENU, PERFIL,
        AVATAR_SELECTOR, AUDIO_CONFIG, SETTINGS, LANGUAGE, AMIGOS_LIST, CHALLENGE_SELECT, STATS
    }

    private GameState gameState = GameState.INITIALIZING;
    private int currentLevel = 1;
    private boolean levelPaused;
    private boolean updatingEntities;
    private Integer pendingStartLevel;
    private boolean pendingBackToLevelSelect;
    private boolean pendingFinishChallenge;
    private boolean friendlyChallengeActive;
    private String challengeRival;
    private long challengeEndTimeMillis;
    private final int[] challengeStarsByLevel = new int[MAX_LEVEL + 1];
    private final boolean[] challengeCompletedLevels = new boolean[MAX_LEVEL + 1];
    private String lastChallengeResult = "";

    private FadeEffectEntity fadeEffect;
    private CurtainEntity curtain;

    private final Menu menus;
    private final Manager audioManager;
    private final SessionManager seccion;
    private final UsuarioRepo repo;
    private final NivelRepo nivelRepo;
    private final PuzzleService puzzleService;

    private MenuPrincipalEntity menuPrincipal;
    private MenuSesionEntity menuSesionEntity;
    private SocialMenuEntity socialMenuEntity;
    private LevelSelectEntity levelSelectEntity;
    private LoginEntity loginEntity;
    private RegisterEntity registerEntity;
    private ReactivateAccountEntity reactivateAccountEntity;
    private PerfilEntity perfilEntity;
    private AvatarSelectorEntity avatarSelector;
    private AudioConfigEntity audioConfigEntity;
    private SettingsEntity settingsEntity;
    private LanguageEntity languageEntity;
    private AmigosListEntity amigosListEntity;
    private ChallengeSelectEntity challengeSelectEntity;
    private StatsEntity statsEntity;

    public Scene() {
        seccion = new SessionManager();
        repo = new UsuarioRepo();
        nivelRepo = new NivelRepo();
        puzzleService = new PuzzleService(nivelRepo, seccion, new ActividadLogger());
        menus = new Menu(seccion, repo);
        audioManager = new Manager(menus, seccion);
    }

    public Model getModel() { return model; }
    public Menu getMenus() { return menus; }
    public Manager getAudioManager() { return audioManager; }
    public PuzzleService getPuzzleService() { return puzzleService; }
    public GameState getGameState() { return gameState; }
    public int getCurrentLevel() { return currentLevel; }
    public boolean isLevelPaused() { return levelPaused; }
    public boolean isFriendlyChallengeActive() { return friendlyChallengeActive; }
    public String getChallengeRival() { return challengeRival; }
    public String getLastChallengeResult() { return lastChallengeResult; }
    public void prepararMusicaFondo() { audioManager.prepararMusicaFondo(MUSIC_MENU); }
    public void reproducirSFX(String archivo) { audioManager.reproducirSFX(archivo); }

    public int getChallengeRemainingSeconds() {
        if (!friendlyChallengeActive) return 0;
        long remaining = challengeEndTimeMillis - System.currentTimeMillis();
        return (int) Math.max(0, (remaining + 999) / 1000);
    }

    public int getChallengeCompletedLevels() {
        int total = 0;
        for (int i = 1; i <= MAX_LEVEL; i++) {
            if (challengeCompletedLevels[i]) total++;
        }
        return total;
    }

    public int getChallengeStars() {
        int total = 0;
        for (int i = 1; i <= MAX_LEVEL; i++) {
            total += challengeStarsByLevel[i];
        }
        return total;
    }

    public void setLevelPaused(boolean levelPaused) {
        this.levelPaused = levelPaused && gameState == PLAYING;
        if (this.levelPaused) {
            model.getSlashTrail().clear();
        }
    }

    public void toggleLevelPaused() {
        setLevelPaused(!levelPaused);
    }

    public void setState(GameState gameState) {
        if (this.gameState != gameState) {
            Usuario usuario = seccion.getUsuarioActual();
            if (usuario != null) {
                I18n.setLanguage(usuario.getIdioma());
            }
            this.gameState = gameState;

            if (fadeEffect != null && shouldClearFade(gameState)) {
                fadeEffect.clear();
            }

            if (gameState != PLAYING) {
                levelPaused = false;
            }

            String music = musicForState(gameState);
            if (music != null) {
                audioManager.prepararMusicaFondo(music);
                audioManager.iniciarMusicaFondo(music);
            }

            for (Entity entity : levelEntities)
                entity.gameStateChanged(gameState);
            for (Entity entity : entities)
                entity.gameStateChanged(gameState);
        }
    }

    private boolean shouldClearFade(GameState gameState) {
        switch (gameState) {
            case MENU_PRINCIPAL:
            case LOGIN:
            case REGISTER:
            case REACTIVATE_ACCOUNT:
            case MENU_SESION:
            case SOCIAL_MENU:
            case PERFIL:
            case AVATAR_SELECTOR:
            case AUDIO_CONFIG:
            case SETTINGS:
            case LANGUAGE:
            case AMIGOS_LIST:
            case CHALLENGE_SELECT:
            case STATS:
            case LEVEL_SELECT:
            case READY:
            case PLAYING:
                return true;
            default:
                return false;
        }
    }

    private String musicForState(GameState gameState) {
        switch (gameState) {
            case TITLE:
                return MUSIC_INTRO;
            case MENU_PRINCIPAL:
            case LOGIN:
            case REGISTER:
            case REACTIVATE_ACCOUNT:
            case MENU_SESION:
            case SOCIAL_MENU:
            case PERFIL:
            case AVATAR_SELECTOR:
            case AUDIO_CONFIG:
            case SETTINGS:
            case LANGUAGE:
            case AMIGOS_LIST:
            case CHALLENGE_SELECT:
            case STATS:
            case LEVEL_SELECT:
            case READY:
                return MUSIC_MENU;
            case PLAYING:
                return MUSIC_GAMEPLAY;
            case LEVEL_CLEARED:
            case GAME_OVER:
                return MUSIC_RESULTS;
            default:
                return null;
        }
    }

    public void cambiarAState(GameState nuevoEstado) {
        setState(nuevoEstado);
    }

    public void start() {
        createAllEntities();
        createAllUserEntities();
        startAllEntities();
    }

    public void procesarEntradaTeclado(int keyCode, char keyChar) {
        if (textFieldFocused != null) {
            textFieldFocused.procesarTecla(keyCode, keyChar);
        }
    }

    private void actualizarFocusTextField() {
        textFieldFocused = null;

        if (loginEntity != null && loginEntity.visible) {
            if (loginEntity.getTxtUsername().isFocused()) {
                textFieldFocused = loginEntity.getTxtUsername();
            } else if (loginEntity.getTxtPassword().isFocused()) {
                textFieldFocused = loginEntity.getTxtPassword();
            }
        }
        if (reactivateAccountEntity != null && reactivateAccountEntity.isVisible()) {
            if (reactivateAccountEntity.getTxtUsername().isFocused()) {
                textFieldFocused = reactivateAccountEntity.getTxtUsername();
                return;
            } else if (reactivateAccountEntity.getTxtPassword().isFocused()) {
                textFieldFocused = reactivateAccountEntity.getTxtPassword();
                return;
            }
        }
        if (registerEntity != null && registerEntity.isVisible()) {
            if (registerEntity.getTxtUsername().isFocused()) {
                textFieldFocused = registerEntity.getTxtUsername();
                return;
            } else if (registerEntity.getTxtNombreCompleto().isFocused()) {
                textFieldFocused = registerEntity.getTxtNombreCompleto();
                return;
            } else if (registerEntity.getTxtPassword().isFocused()) {
                textFieldFocused = registerEntity.getTxtPassword();
                return;
            } else if (registerEntity.getTxtConfirmPassword().isFocused()) {
                textFieldFocused = registerEntity.getTxtConfirmPassword();
            }
        }
        if (amigosListEntity != null && amigosListEntity.isVisible()) {
            if (amigosListEntity.getTxtUsername().isFocused()) {
                textFieldFocused = amigosListEntity.getTxtUsername();
            }
        }
    }

    private void createAllEntities() {
        fadeEffect = new FadeEffectEntity(this);
        curtain = new CurtainEntity(this);
        entities.add(new InitializerEntity(this, fadeEffect));
        entities.add(new TitleEntity(this, fadeEffect, curtain));
        entities.add(curtain);
        entities.add(new GameOverEntity(this, fadeEffect, curtain));
        entities.add(new LevelClearedEntity(this, fadeEffect, curtain));
        entities.add(fadeEffect);
    }

    private void createAllUserEntities() {
        menuPrincipal = new MenuPrincipalEntity(this);
        menuSesionEntity = new MenuSesionEntity(this, repo, seccion);
        socialMenuEntity = new SocialMenuEntity(this, seccion);
        levelSelectEntity = new LevelSelectEntity(this, puzzleService, seccion);
        loginEntity = new LoginEntity(this, repo, seccion);
        registerEntity = new RegisterEntity(this, repo, seccion);
        reactivateAccountEntity = new ReactivateAccountEntity(this, repo, seccion);
        perfilEntity = new PerfilEntity(this, repo, seccion, audioManager);
        avatarSelector = new AvatarSelectorEntity(this, seccion);
        audioConfigEntity = new AudioConfigEntity(this, audioManager, menus, seccion);
        settingsEntity = new SettingsEntity(this, seccion, repo);
        languageEntity = new LanguageEntity(this, seccion, repo);
        amigosListEntity = new AmigosListEntity(this, menus, seccion);
        challengeSelectEntity = new ChallengeSelectEntity(this, menus, seccion);
        statsEntity = new StatsEntity(this, menus, seccion);

        entities.add(menuPrincipal);
        entities.add(menuSesionEntity);
        entities.add(socialMenuEntity);
        entities.add(levelSelectEntity);
        entities.add(loginEntity);
        entities.add(registerEntity);
        entities.add(reactivateAccountEntity);
        entities.add(perfilEntity);
        entities.add(avatarSelector);
        entities.add(audioConfigEntity);
        entities.add(settingsEntity);
        entities.add(languageEntity);
        entities.add(amigosListEntity);
        entities.add(challengeSelectEntity);
        entities.add(statsEntity);
    }

    private void createAllLevelEntities() {
        levelEntities.add(new BackgroundEntity(this));
        levelEntities.add(new PetEntity(this, curtain));
        for (AirCushion airCushion : model.getAirCushions())
            levelEntities.add(new AirCushionEntity(this, airCushion));
        for (Rope rope : model.getRopes())
            levelEntities.add(new RopeEntity(this, rope));
        for (PinRope pinRope : model.getPinRopes())
            levelEntities.add(new PinRopeEntity(this, pinRope));
        for (Spikes spikes : model.getSpikesList())
            levelEntities.add(new SpikesEntity(this, spikes));
        levelEntities.add(new CandyEntity(this));
        for (Star star : model.getStars())
            levelEntities.add(new StarEntity(this, star));
        for (Bubble bubble : model.getBubbles())
            levelEntities.add(new BubbleEntity(this, bubble));
        levelEntities.add(new LevelHudEntity(this, audioManager));
    }

    private void startAllEntities() {
        for (Entity entity : entities)
            entity.start();
    }

    private void startAllLevelEntities() {
        for (Entity entity : levelEntities)
            entity.start();
    }

    public void update() {
        actualizarFocusTextField();
        if (checkChallengeTimeout()) {
            processPendingSceneChange();
            return;
        }

        updatingEntities = true;
        try {
            for (Entity entity : levelEntities) {
                if (levelPaused && !(entity instanceof LevelHudEntity))
                    continue;
                entity.update();
            }
            for (Entity entity : entities)
                entity.update();
        } finally {
            updatingEntities = false;
        }
        processPendingSceneChange();
    }

    public void updateFixed() {
        if (checkChallengeTimeout()) {
            processPendingSceneChange();
            return;
        }
        if (levelPaused) {
            model.getSlashTrail().clear();
            updatingEntities = true;
            try {
                for (Entity entity : levelEntities) {
                    if (entity instanceof LevelHudEntity)
                        entity.updateFixed();
                }
                for (Entity entity : entities)
                    entity.updateFixed();
            } finally {
                updatingEntities = false;
            }
            processPendingSceneChange();
            return;
        }
        if (Mouse.pressed) {
            List<Point> trail = model.getSlashTrail().getTrail();
            if (trail.size() > 0) {
                Point p = trail.get(trail.size() - 1);
                if (p != null && p.x >= 0 && p.y >= 0)
                    model.addSlashTrail((int) (p.x + 0.5 * (Mouse.x - p.x)), (int) (p.y + 0.5 * (Mouse.y - p.y)));
                model.addSlashTrail((int) Mouse.x, (int) Mouse.y);
            }
        } else {
            model.addSlashTrail(-1, -1);
        }
        updatingEntities = true;
        try {
            for (Entity entity : levelEntities)
                entity.updateFixed();
            for (Entity entity : entities)
                entity.updateFixed();
            model.update();
        } finally {
            updatingEntities = false;
        }
        processPendingSceneChange();
    }

    public void draw(Graphics2D g) {
        for (Entity entity : levelEntities) {
            if (entity.isVisible())
                entity.draw(g);
        }
        for (Entity entity : entities) {
            if (entity.isVisible())
                entity.draw(g);
        }
        model.getSlashTrail().drawDebug(g);
    }

    public void startLevel(int level) {
        if (updatingEntities) {
            pendingStartLevel = level;
            pendingBackToLevelSelect = false;
            return;
        }
        if (!canPlayLevel(level)) {
            setState(LEVEL_SELECT);
            return;
        }

        currentLevel = level;
        levelPaused = false;
        model.startLevel("/res/level_" + level + ".txt");
        levelEntities.clear();
        createAllLevelEntities();
        startAllLevelEntities();
        if (gameState == PLAYING) {
            activateReloadedPlayingLevel();
        } else {
            setState(PLAYING);
        }
    }

    private void activateReloadedPlayingLevel() {
        if (fadeEffect != null) {
            fadeEffect.clear();
        }
        audioManager.iniciarMusicaFondo(MUSIC_GAMEPLAY);
        for (Entity entity : levelEntities) {
            entity.gameStateChanged(PLAYING);
        }
    }

    public void replayLevel() { startLevel(currentLevel); }
    public void backToTitle() {
        if (updatingEntities) {
            pendingStartLevel = null;
            pendingBackToLevelSelect = true;
            return;
        }
        levelPaused = false;
        setState(LEVEL_SELECT);
    }
    public void exitCurrentLevel() { backToTitle(); }
    public void nextLevel() { startLevel(currentLevel + 1); }

    public boolean hasNextLevel() {
        return currentLevel < MAX_LEVEL;
    }

    public boolean canPlayLevel(int level) {
        return level >= 1 && level <= MAX_LEVEL && (friendlyChallengeActive || puzzleService.puedeJugarNivel(level));
    }

    public void completeCurrentLevel(int estrellas) {
        Usuario usuario = seccion.getUsuarioActual();
        if (usuario == null) return;

        estrellas = Math.max(0, Math.min(MAX_STARS, estrellas));
        int puntaje = 0;

        puzzleService.completarNivel(currentLevel, puntaje, 0, estrellas);
        usuario.setNivelesCompletados(Math.max(usuario.getNivelesCompletados(), currentLevel));
        usuario.setNivelDesbloqueado(Math.max(usuario.getNivelDesbloqueado(), Math.min(MAX_LEVEL, currentLevel + 1)));
        if (friendlyChallengeActive) {
            challengeCompletedLevels[currentLevel] = true;
            challengeStarsByLevel[currentLevel] = Math.max(challengeStarsByLevel[currentLevel], estrellas);
            if (currentLevel == MAX_LEVEL) {
                pendingFinishChallenge = true;
            }
        }
        repo.guardar(usuario);
    }

    public void startFriendlyChallenge(String rival) {
        challengeRival = rival;
        friendlyChallengeActive = true;
        pendingFinishChallenge = false;
        lastChallengeResult = "";
        challengeEndTimeMillis = System.currentTimeMillis() + CHALLENGE_LIMIT_SECONDS * 1000L;
        for (int i = 0; i < challengeStarsByLevel.length; i++) {
            challengeStarsByLevel[i] = 0;
            challengeCompletedLevels[i] = false;
        }
        startLevel(1);
    }

    private boolean checkChallengeTimeout() {
        if (!friendlyChallengeActive || getChallengeRemainingSeconds() > 0) {
            return false;
        }
        pendingFinishChallenge = true;
        return true;
    }

    private void finishFriendlyChallenge() {
        if (!friendlyChallengeActive) return;

        Usuario usuario = seccion.getUsuarioActual();
        int niveles = getChallengeCompletedLevels();
        int estrellas = getChallengeStars();
        int rivalNiveles = menus.nivelesCompletados(challengeRival);
        int rivalEstrellas = menus.totalEstrellas(challengeRival);
        boolean gano = niveles > rivalNiveles || (niveles == rivalNiveles && estrellas > rivalEstrellas);
        boolean empate = niveles == rivalNiveles && estrellas == rivalEstrellas;

        if (usuario != null && gano) {
            usuario.setRetosGanados(usuario.getRetosGanados() + 1);
            repo.guardar(usuario);
        }

        String resultado = empate ? I18n.t("challenge_tie") : (gano ? I18n.t("challenge_win") : I18n.t("challenge_loss"));
        lastChallengeResult = resultado + " " + niveles + "/" + MAX_LEVEL + " " + I18n.t("level_selector").toLowerCase()
            + " | " + estrellas + " " + I18n.t("stars").toLowerCase()
            + " vs " + challengeRival + ": " + rivalNiveles + "/" + MAX_LEVEL
            + " | " + rivalEstrellas + " " + I18n.t("stars").toLowerCase();

        friendlyChallengeActive = false;
        challengeRival = null;
        levelPaused = false;
        setState(CHALLENGE_SELECT);
    }

    public void irAPerfil() { setState(PERFIL); }
    public void irAAmigos() { setState(AMIGOS_LIST); }
    public void irAChallenge() { setState(CHALLENGE_SELECT); }
    public void irAEstadisticas() { setState(STATS); }
    public void irASeleccionNivel() { setState(LEVEL_SELECT); }
    public void irAMenuSesion() { setState(MENU_SESION); }
    public void irASocialMenu() { setState(SOCIAL_MENU); }
    public void irASettings() { setState(SETTINGS); }

    private void processPendingSceneChange() {
        if (pendingFinishChallenge) {
            pendingFinishChallenge = false;
            finishFriendlyChallenge();
            return;
        }
        if (pendingBackToLevelSelect) {
            pendingBackToLevelSelect = false;
            backToTitle();
            return;
        }
        if (pendingStartLevel != null) {
            int level = pendingStartLevel;
            pendingStartLevel = null;
            startLevel(level);
        }
    }
}
