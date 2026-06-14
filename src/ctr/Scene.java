package ctr;

import Audio.Manager;
import Usuarios.ActividadLogger;
import Usuarios.Menu;
import Usuarios.NivelRepo;
import Usuarios.PuzzleService;
import Usuarios.SessionManager;
import Usuarios.Usuario;
import Usuarios.UsuarioRepo;
import static ctr.Scene.GameState.*;
import ctr.entity.*;
import ctr.model.*;
import ctr.ui.TextField;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    private static final int MAX_LEVEL = 5;

    private final Model model = new Model(800, 600, 10);
    protected List<Entity> entities = new ArrayList<Entity>();
    protected List<Entity> levelEntities = new ArrayList<Entity>();
    private TextField textFieldFocused = null;

    public static enum GameState {
        INITIALIZING, OL_PRESENTS, TITLE, LEVEL_SELECT, READY, PLAYING,
        LEVEL_CLEARED, GAME_OVER,
        MENU_PRINCIPAL, LOGIN, REGISTER, REACTIVATE_ACCOUNT, MENU_SESION, PERFIL,
        AVATAR_SELECTOR, AUDIO_CONFIG, AMIGOS_LIST, CHALLENGE_SELECT, STATS
    }

    private GameState gameState = GameState.INITIALIZING;
    private int currentLevel = 1;

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
    private LevelSelectEntity levelSelectEntity;
    private LoginEntity loginEntity;
    private RegisterEntity registerEntity;
    private ReactivateAccountEntity reactivateAccountEntity;
    private PerfilEntity perfilEntity;
    private AvatarSelectorEntity avatarSelector;
    private AudioConfigEntity audioConfigEntity;
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

    public void setState(GameState gameState) {
        if (this.gameState != gameState) {
            this.gameState = gameState;

            if (gameState == PLAYING) {
                audioManager.iniciarMusicaPartida();
            }

            for (Entity entity : levelEntities)
                entity.gameStateChanged(gameState);
            for (Entity entity : entities)
                entity.gameStateChanged(gameState);
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
        levelSelectEntity = new LevelSelectEntity(this, puzzleService, seccion);
        loginEntity = new LoginEntity(this, repo, seccion);
        registerEntity = new RegisterEntity(this, repo, seccion);
        reactivateAccountEntity = new ReactivateAccountEntity(this, repo, seccion);
        perfilEntity = new PerfilEntity(this, repo, seccion, audioManager);
        avatarSelector = new AvatarSelectorEntity(this, seccion);
        audioConfigEntity = new AudioConfigEntity(this, audioManager, menus, seccion);
        amigosListEntity = new AmigosListEntity(this, menus, seccion);
        challengeSelectEntity = new ChallengeSelectEntity(this, menus, seccion);
        statsEntity = new StatsEntity(this, menus, seccion);

        entities.add(menuPrincipal);
        entities.add(menuSesionEntity);
        entities.add(levelSelectEntity);
        entities.add(loginEntity);
        entities.add(registerEntity);
        entities.add(reactivateAccountEntity);
        entities.add(perfilEntity);
        entities.add(avatarSelector);
        entities.add(audioConfigEntity);
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

        for (Entity entity : levelEntities)
            entity.update();
        for (Entity entity : entities)
            entity.update();
    }

    public void updateFixed() {
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
        for (Entity entity : levelEntities)
            entity.update();
        for (Entity entity : entities)
            entity.update();
        model.update();
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
        if (!canPlayLevel(level)) {
            setState(LEVEL_SELECT);
            return;
        }

        currentLevel = level;
        model.startLevel("/res/level_" + level + ".txt");
        levelEntities.clear();
        createAllLevelEntities();
        startAllLevelEntities();
        setState(PLAYING);
    }

    public void replayLevel() { startLevel(currentLevel); }
    public void backToTitle() { setState(LEVEL_SELECT); }
    public void nextLevel() { startLevel(currentLevel + 1); }

    public boolean canPlayLevel(int level) {
        return level >= 1 && level <= MAX_LEVEL && puzzleService.puedeJugarNivel(level);
    }

    public void completeCurrentLevel(int estrellas) {
        Usuario usuario = seccion.getUsuarioActual();
        if (usuario == null) return;

        estrellas = Math.max(0, Math.min(3, estrellas));
        int puntaje = Math.max(1, estrellas) * 100;

        puzzleService.completarNivel(currentLevel, puntaje, 0, estrellas);
        usuario.setNivelesCompletados(Math.max(usuario.getNivelesCompletados(), currentLevel));
        usuario.setNivelDesbloqueado(Math.max(usuario.getNivelDesbloqueado(), Math.min(MAX_LEVEL, currentLevel + 1)));
        usuario.setPuntuacionGeneral(Math.max(usuario.getPuntuacionGeneral(), puntaje));
        repo.guardar(usuario);
    }

    public void irAPerfil() { setState(PERFIL); }
    public void irAAmigos() { setState(AMIGOS_LIST); }
    public void irAChallenge() { setState(CHALLENGE_SELECT); }
    public void irAEstadisticas() { setState(STATS); }
    public void irASeleccionNivel() { setState(LEVEL_SELECT); }
    public void irAMenuSesion() { setState(MENU_SESION); }
}