package com.ereperez.asteroidgl.entities;

import android.graphics.PointF;
import android.opengl.GLES20;

import com.ereperez.asteroidgl.CollisionDetection;
import com.ereperez.asteroidgl.GameEvent;
import com.ereperez.asteroidgl.GameSettings;
import com.ereperez.asteroidgl.Utils;
import com.ereperez.asteroidgl.render.Mesh;

public class Player extends GLEntity {
    private static final String TAG = "Player";
    private static final int PLAYER_HEALTH = GameSettings.PLAYER_HEALTH;
    private static final int IMMUNITY_TIME = GameSettings.PLAYER_IMMUNITY_TIME;
    public static final float TIME_BETWEEN_SHOTS = GameSettings.TIME_BETWEEN_SHOTS; //seconds.
    public static final float TIME_BETWEEN_TELEPORT = GameSettings.TIME_BETWEEN_TELEPORT;
    public static final float TIME_BETWEEN_BOOST = GameSettings.TIME_BETWEEN_BOOST;
    public static final float ROTATION_VELOCITY = GameSettings.ROTATION_VELOCITY;
    public static final float THRUST = GameSettings.THRUST;//8f;
    public static final float DRAG = GameSettings.DRAG;
    public static final float PLAYER_WIDTH = GameSettings.PLAYER_WIDTH;
    public static final float PLAYER_HEIGHT = GameSettings.PLAYER_HEIGHT;
    private float bulletCooldown = 0;
    public float teleportCooldown = 0;
    private float boostCooldown = 0;
    public int health = PLAYER_HEALTH;
    int updateCount = 0;
    boolean immunity = false;

    public Player(final float _x, final float _y){
        super();
        x = _x;
        y = _y;
        width = PLAYER_WIDTH;
        height = PLAYER_HEIGHT;
        float[] vertices = { // in counterclockwise order:
                0.0f,  0.5f, 0.0f, 	// top
                -0.5f, -0.5f, 0.0f,	// bottom left
                0.5f, -0.5f, 0.0f  	// bottom right
        };
        mesh = new Mesh(vertices, GLES20.GL_TRIANGLES);
        mesh.setWidthHeight(width, height);
        mesh.flipY();
    }

    @Override
    public void render(final float[] viewportMatrix) {
        //ask the super class (GLEntity) to render us
        super.render(viewportMatrix);
    }

    @Override
    public void update(final double dt) {
        rotation += (dt*ROTATION_VELOCITY) * game.inputs.horizontalFactor;

        boostCooldown -= dt;
        if(game.inputs.pressingBoost && boostCooldown <= 0) {
            final float theta = (float) (rotation* Utils.TO_RAD);
            velX += (float)Math.sin(theta) * THRUST;
            velY -= (float)Math.cos(theta) * THRUST;
            boostCooldown = TIME_BETWEEN_BOOST;
            game.onGameEvent(GameEvent.Powerup, null);
            game.getFlame(this);
        }
        velX *= DRAG;
        velY *= DRAG;

        bulletCooldown -= dt;
        if(game.inputs.pressingLaser && bulletCooldown <= 0){
            //setColors(1, 0, 1, 1); //PURPLE
            setColors(1f, 1f, 1f,1f);
            game.onGameEvent(GameEvent.Laser, null);
            if(game.maybeFireBullet(this)){
                bulletCooldown = TIME_BETWEEN_SHOTS;
            }
        }else{
            setColors(1f, 1f, 1f,1f);
        }

        teleportCooldown -= dt;
        if(game.inputs.pressingTeleport && teleportCooldown <= 0){
            x = game.r.nextInt((int) (WORLD_WIDTH-width));
            y = game.r.nextInt((int) (WORLD_HEIGHT-height));
            teleportCooldown = TIME_BETWEEN_TELEPORT;
            game.onGameEvent(GameEvent.Teleport, null);
        }

        super.update(dt);
        updateCount++;
        checkImmunity();
    }

    @Override
    public boolean isColliding(final GLEntity that){
        if(!areBoundingSpheresOverlapping(this, that)){ //quick rejection test
            return false;
        }
        final PointF[] shipHull = getPointList();
        final PointF[] asteroidHull =  that.getPointList();
        if(CollisionDetection.polygonVsPolygon(shipHull, asteroidHull)){
            return true;
        }
        return CollisionDetection.polygonVsPoint(asteroidHull, x, y); //finally, check if we're inside the asteroid
    }

    @Override
    public void onCollision(final GLEntity that) {
        super.onCollision(that);
        if (that.getClass().equals(Asteroid.class)){
            if (updateCount > IMMUNITY_TIME) {
                updateCount = 0;
                immunity = true;
                health--;
                game.onGameEvent(GameEvent.TakeDamage, this);
            }
        }
    }

    private void checkImmunity(){
        if (immunity && updateCount > 0){
            if (updateCount %2 == 0 && updateCount < IMMUNITY_TIME) {
                setColors(0, 0, 0, 1);
            }
            else {
                setColors(1, 1, 1, 1);
            }
            if (updateCount >= IMMUNITY_TIME){
                immunity = false;
            }
        }
    }
}