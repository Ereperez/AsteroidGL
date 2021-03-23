package com.ereperez.asteroidgl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import com.ereperez.asteroidgl.entities.Asteroid;
import com.ereperez.asteroidgl.entities.BoostFlame;
import com.ereperez.asteroidgl.entities.Border;
import com.ereperez.asteroidgl.entities.Bullet;
import com.ereperez.asteroidgl.entities.GLEntity;
import com.ereperez.asteroidgl.entities.Particle;
import com.ereperez.asteroidgl.entities.Player;
import com.ereperez.asteroidgl.entities.Star;
import com.ereperez.asteroidgl.input.InputManager;
import com.ereperez.asteroidgl.render.FPSCounter;
import com.ereperez.asteroidgl.render.GLManager;

import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by edwin on 02,March,2021
 */

public class Game extends GLSurfaceView implements GLSurfaceView.Renderer {
    public static final String TAG = "Game";
    public static final int GAME_LEVEL_ONE = GameSettings.GAME_LEVEL_ONE;
    public static final long SECOND_IN_NANOSECONDS = 1000000000;
    public static final long MILLISECOND_IN_NANOSECONDS = 1000000;
    public static float NANOSECONDS_TO_MILLISECONDS = 1.0f / MILLISECOND_IN_NANOSECONDS;
    public static final float NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS;
    private static final float[] BG_COLOR = {GameSettings.BG_COLOR/255f, GameSettings.BG_COLOR/255f, GameSettings.BG_COLOR/255f, 1f}; //RGBA
    public static final float WORLD_WIDTH = GameSettings.WORLD_WIDTH;
    public static final float WORLD_HEIGHT = GameSettings.WORLD_HEIGHT;
    public static final float METERS_TO_SHOW_X = WORLD_WIDTH;//the entire game world in view
    public static final float METERS_TO_SHOW_Y = WORLD_HEIGHT;
    private static final int STAR_COUNT = GameSettings.STAR_COUNT;
    private static final int ASTEROID_COUNT = GameSettings.ASTEROID_COUNT;
    private final ArrayList<Star> stars = new ArrayList<>();
    private final ArrayList<Asteroid> asteroids = new ArrayList<>();
    private GameHUD gameHUD = null;
    private static final int BULLET_COUNT = (int)(Bullet.BULLET_TIME_TO_LIVE/Player.TIME_BETWEEN_SHOTS)+1;
    final Bullet[] bullets = new Bullet[BULLET_COUNT];
    private static final int PARTICLE_COUNT = GameSettings.PARTICLE_COUNT;
    public final ArrayList<Particle> particles = new ArrayList<>();
    private static final int FLAME_COUNT = GameSettings.FLAME_COUNT;
    public final ArrayList<BoostFlame> boostFlames = new ArrayList<>();

    private Border border;
    private Player player;
    private FPSCounter fpsCounter = null;
    public final Random r = new Random();
    public Jukebox jukebox = null;
    public InputManager inputs = new InputManager(); //empty but valid default
    public int pointsCollected = 0;
    private boolean gameOver = false;

    public Game(Context context) {
        super(context);
        ctxInit(context);
        init();
    }
    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctxInit(context);
        init();
    }

    private void ctxInit(Context context){
        jukebox = new Jukebox(context);
    }

    private void init(){
        GLEntity.game = this;
        setEGLContextClientVersion(2); //select OpenGL ES 2.0
        setPreserveEGLContextOnPause(true); //context *may* be preserved and thus *may* avoid slow reloads when switching apps.
        // we always re-create the OpenGL context in onSurfaceCreated, so we're safe either way.
        fpsCounter = new FPSCounter();
        gameHUD = new GameHUD();

        for(int i = 0; i < BULLET_COUNT; i++){
            bullets[i] = new Bullet();
        }
        for(int i = 0; i < PARTICLE_COUNT; i++){
            particles.add(new Particle(i));
        }
        for(int i = 0; i < FLAME_COUNT; i++){
            boostFlames.add(new BoostFlame());
        }
        setRenderer(this);
    }

    public InputManager getControls(){
        return inputs;
    }

    public void setControls(final InputManager input){
        inputs.onPause();
        inputs.onStop();
        inputs = input;
        inputs.onStart();

    }

    public void onGameEvent(GameEvent gameEvent, GLEntity e /*can be null!*/) {
        jukebox.playSoundForGameEvent(gameEvent);
    }

    // Create the projection Matrix. This is used to project the scene onto a 2D viewport.
    private final float[] viewportMatrix = new float[4*4]; //In essence, it is our our Camera

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        GLManager.buildProgram(); //compile, link and upload our GL program
        GLES20.glClearColor(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], BG_COLOR[3]); //set clear color
        // spawn Border at the center of the world now!
        border = new Border(WORLD_WIDTH/2, WORLD_HEIGHT/2, WORLD_WIDTH, WORLD_HEIGHT);

        spawnEntities();
        jukebox.resumeBgMusic();
    }

    private void spawnEntities(){
        // center the player in the world.
        player = new Player(WORLD_WIDTH/2f, WORLD_HEIGHT/2f);//old version

        //Add background stars
        for(int i = 0; i < STAR_COUNT; i++){
            stars.add(new Star(r.nextInt((int)WORLD_WIDTH), r.nextInt((int)WORLD_HEIGHT)));
        }
        spawnAsteroids();
        onGameEvent(GameEvent.GameStart, null);
    }

    private void spawnAsteroids(){
        //Add asteroids
        for(int i = 0; i < ASTEROID_COUNT; i++){
            asteroids.add(new Asteroid(r.nextInt(3), r.nextInt((int)WORLD_WIDTH), r.nextInt((int)WORLD_HEIGHT), i));//bound 3 for the 0, 1 and 2 type of asteroid
        }
    }

    @Override
    public void onSurfaceChanged(final GL10 unused, final int width, final int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);
    }
    @Override
    public void onDrawFrame(final GL10 unused) {
        update();
        render();
    }

    //trying a fixed time-step with accumulator, courtesy of
    //https://gafferongames.com/post/fix_your_timestep/
    final double dt = 0.01;
    double accumulator = 0.0;
    double currentTime = System.nanoTime()*NANOSECONDS_TO_SECONDS;
    private void update(){
        inputs.update((float) dt);
        final double newTime = System.nanoTime()*NANOSECONDS_TO_SECONDS;
        final double frameTime = newTime - currentTime;
        currentTime = newTime;
        accumulator += frameTime;
        checkGameOver();
        while(accumulator >= dt){
            for(final Asteroid a : asteroids){
                a.update(dt);
            }
            for(final Bullet b : bullets){
                if(b.isDead()){ continue; } //skip
                b.update(dt);
            }
            for(final Particle p : particles){
                if(p.isDead()){ continue; } //skip
                p.update(dt);
            }
            for(final BoostFlame f : boostFlames){
                if(f.isDead()){ continue; } //skip
                f.update(dt);
            }
            player.update(dt);
            collisionDetection();
            removeDeadEntities();
            accumulator -= dt;
        }
        checkPlayerHealth();
    }

    private void render(){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); //clear buffer to background color
        //setup a projection matrix by passing in the range of the game world that will be mapped by OpenGL to the screen.
        //encapsulate this in a Camera-class, let it "position" itself relative to an entity
        final int offset = 0;
        final float left = 0;
        final float right = METERS_TO_SHOW_X;
        final float bottom = METERS_TO_SHOW_Y;
        final float top = 0;
        final float near = 0f;
        final float far = 1f;
        Matrix.orthoM(viewportMatrix, offset, left, right, bottom, top, near, far);

        border.render(viewportMatrix);
        for(final Asteroid a : asteroids){
            a.render(viewportMatrix);
        }
        for(final Star s : stars){
            s.render(viewportMatrix);
        }
        for(final Bullet b : bullets){
            if(b.isDead()){ continue; } //skip
            b.render(viewportMatrix);
        }
        for(final Particle p : particles){
            if(p.isDead()){ continue; } //skip
            p.render(viewportMatrix);
        }
        for(final BoostFlame f : boostFlames){
            if(f.isDead()){ continue; } //skip
            f.render(viewportMatrix);
        }
        player.render(viewportMatrix);
        gameHUD.statsHUD(player.health, pointsCollected, GAME_LEVEL_ONE, player.teleportCooldown, fpsCounter.fpsCount());
        gameHUD.renderHUD(viewportMatrix);
    }

    private void checkPlayerHealth(){
        if (player.health < 1){
            onGameEvent(GameEvent.GameOver, null);
            gameOver = true;
        }
    }

    private void checkGameOver(){
        if (gameOver){
            asteroids.clear();
            stars.clear();
            pointsCollected = 0;
            spawnEntities();
            gameOver = false;
        }
        else if(asteroids.size() <= 0){
            spawnAsteroids();//respawn asteroids when all are dead to keep going
        }
    }

    public void getFlame(final GLEntity source){
        for(final BoostFlame f : boostFlames) {
            if(f.isDead()) {
                f.flameFrom(source);
            }
        }
    }

    public void getParticles(final GLEntity source){
        for(final Particle p : particles) {
            p.explodeFrom(source);
        }
    }

    public boolean maybeFireBullet(final GLEntity source){
        for(final Bullet b : bullets) {
            if(b.isDead()) {
                b.fireFrom(source);
                return true;
            }
        }
        return false;
    }

    private void collisionDetection(){
        for(final Bullet b : bullets) {
            if(b.isDead()){ continue; } //skip dead bullets
            for(final Asteroid a : asteroids) {
                if(b.isColliding(a)){
                    if(a.isDead()){continue;}
                    b.onCollision(a); //notify each entity so they can decide what to do
                    a.onCollision(b);
                    b.ttl = 0;
                }
            }
        }
        for(final Asteroid a : asteroids) {
            if(a.isDead()){
                continue;
            }
            if(player.isColliding(a)){
                player.onCollision(a);
                a.onCollision(player);
            }
        }
    }

    public void removeDeadEntities(){
        Asteroid temp;
        final int count = asteroids.size();
        for(int i = count-1; i >= 0; i--){
            temp = asteroids.get(i);
            if(temp.isDead()){
                asteroids.remove(i);
            }
        }
    }

    public void onResume(){
        Log.d(TAG, "onResume");
        inputs.onResume();
        jukebox.resumeBgMusic();
    }

    public void onPause(){
        Log.d(TAG, "onPause");
        inputs.onPause();
        jukebox.pauseBgMusic();
    }

    public void onDestroy(){
        Log.d(TAG, "onDestroy");
        inputs = null;
        //jukebox = null;//remove?
        GLEntity.game = null;
    }
}
