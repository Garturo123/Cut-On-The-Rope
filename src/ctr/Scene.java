package ctr;

import Audio.Manager;
import Usuarios.Menu;
import Usuarios.SessionManager;
import Usuarios.UsuarioRepo;
import static ctr.Scene.GameState.*;
import ctr.entity.*;
import ctr.model.*;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Scene
{
    private final Model model = new Model(800, 600, 10);
    protected List<Entity> entities = new ArrayList<Entity>();
    protected List<Entity> levelEntities = new ArrayList<Entity>();
    
    // Estados del juego
    public static enum GameState { 
        // Estados existentes
        INITIALIZING, OL_PRESENTS, TITLE, LEVEL_SELECT, READY, PLAYING, 
        LEVEL_CLEARED, GAME_OVER,
        
        // Estados del sistema de usuarios
        MENU_PRINCIPAL, LOGIN, REGISTER, REACTIVATE_ACCOUNT, PERFIL, 
        AVATAR_SELECTOR, AUDIO_CONFIG, AMIGOS_LIST, CHALLENGE_SELECT, STATS
    }
    
    private GameState gameState = GameState.INITIALIZING;
    private int currentLevel = 1;
    
    // Componentes existentes
    private FadeEffectEntity fadeEffect;
    private CurtainEntity curtain;
    
    // NUEVOS: Sistema de usuarios
    private final Menu menus;
    private final Manager audioManager;
    private final SessionManager seccion ;
    private final UsuarioRepo repo;
    // NUEVAS: Entidades de usuario
    private MenuPrincipalEntity menuPrincipal;
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
        // Inicializar sistemas de usuarios
        seccion =  new SessionManager();
        repo = new UsuarioRepo();
        menus = new Menu(seccion, repo);
        audioManager = new Manager(menus, seccion);
    }
    
    public Model getModel() { return model; }
    public Menu getMenus() { return menus; }
    public Manager getAudioManager() { return audioManager; }
    public GameState getGameState() { return gameState; }
    
    // Método para cambiar estado (ya lo tienes)
    public void setState(GameState gameState) {
        if(this.gameState != gameState) {
            this.gameState = gameState;
            
            // Manejo especial al entrar a PLAYING
            if (gameState == PLAYING) {
                audioManager.iniciarMusicaPartida();
            }
            
            // Notificar a todas las entidades
            for (Entity entity : levelEntities)
                entity.gameStateChanged(gameState);
            for (Entity entity : entities)
                entity.gameStateChanged(gameState);
        }
    }
    
    // Método para cambiar estado desde entidades (conveniencia)
    public void cambiarAState(GameState nuevoEstado) {
        setState(nuevoEstado);
    }
    
    public void start() {
        createAllEntities();
        createAllUserEntities();  // NUEVO
        startAllEntities();
    }
    
    // Crear entidades existentes (tú ya tenías esto)
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
    
    // NUEVO: Crear entidades del sistema de usuarios
    private void createAllUserEntities() {
        menuPrincipal = new MenuPrincipalEntity(this);
        loginEntity = new LoginEntity(this,repo ,seccion);
        registerEntity = new RegisterEntity(this, repo, seccion);
        reactivateAccountEntity = new ReactivateAccountEntity(this, repo, seccion);
        perfilEntity = new PerfilEntity(this, repo, seccion, audioManager);
        avatarSelector = new AvatarSelectorEntity(this, seccion.getUsuarioActual());
        audioConfigEntity = new AudioConfigEntity(this, audioManager, menus, seccion);
        amigosListEntity = new AmigosListEntity(this, menus);
        challengeSelectEntity = new ChallengeSelectEntity(this, menus);
        statsEntity = new StatsEntity(this, menus);
        
        // Agregar a entities (no a levelEntities, son persistentes)
        entities.add(menuPrincipal);
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
    
    // Crear entidades del nivel (tú ya tenías esto)
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
        for(Entity entity : levelEntities)
            entity.start();
    }

    public void update() {
        for(Entity entity : levelEntities)
            entity.update();
        for(Entity entity : entities)
            entity.update();
    }
    
    public void updateFixed() {
        if(Mouse.pressed) {
            List<Point> trail = model.getSlashTrail().getTrail();
            if(trail.size() > 0) {
                Point p = trail.get(trail.size() - 1);
                if (p != null && p.x >= 0 && p.y >= 0)
                    model.addSlashTrail((int) (p.x + 0.5 * (Mouse.x - p.x)), (int) (p.y + 0.5 * (Mouse.y - p.y)));
                model.addSlashTrail((int) Mouse.x, (int) Mouse.y);
            }
        }
        else
            model.addSlashTrail(-1, -1);
        for(Entity entity : levelEntities)
            entity.updateFixed();
        for(Entity entity : entities)
            entity.updateFixed();
        model.update();
    }
    
    public void draw(Graphics2D g) {
        for(Entity entity : levelEntities) {
            if(entity.isVisible())
                entity.draw(g);
        }
        for(Entity entity : entities) {
            if(entity.isVisible())
                entity.draw(g);
        }
        model.getSlashTrail().drawDebug(g);
    }
    
    // Inicia control de gameflow (tú ya tenías esto)
    public void startLevel(int level) {
        currentLevel = level;
        model.startLevel("/res/level_" + level + ".txt");
        levelEntities.clear();
        createAllLevelEntities();
        startAllLevelEntities();
        setState(PLAYING);
    }

    public void replayLevel() { startLevel(currentLevel); }
    public void backToTitle() { setState(MENU_PRINCIPAL); }  // CAMBIADO: Ahora va al menú principal
    public void nextLevel() { startLevel(currentLevel + 1); }
    
    // NUEVOS: Métodos de conveniencia para navegación
    public void irAPerfil() { setState(PERFIL); }
    public void irAAmigos() { setState(AMIGOS_LIST); }
    public void irAChallenge() { setState(CHALLENGE_SELECT); }
    public void irAEstadisticas() { setState(STATS); }
    public void irASeleccionNivel() { setState(LEVEL_SELECT); }
}