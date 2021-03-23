package com.ereperez.asteroidgl.entities;

import android.graphics.PointF;
import android.opengl.GLES20;

import com.ereperez.asteroidgl.CollisionDetection;
import com.ereperez.asteroidgl.GameEvent;
import com.ereperez.asteroidgl.GameSettings;
import com.ereperez.asteroidgl.render.Mesh;

public class Bullet extends GLEntity {
    private static final Mesh BULLET_MESH = new Mesh(Mesh.POINT, GLES20.GL_POINTS); //Q&D pool, Mesh.POINT is just [0,0,0] float array
    private static final float TO_RADIANS = (float)Math.PI/180.0f;
    private static final float SPEED = GameSettings.BULLET_SPEED;
    public static final float BULLET_TIME_TO_LIVE = GameSettings.BULLET_TTL;//3.0f; //seconds
    public float ttl = BULLET_TIME_TO_LIVE;
    private int pointWorth = 0;

    public Bullet() {
        setColors(1f,1f,1f,1f);//WHITE
        mesh = BULLET_MESH; //all bullets use the exact same mesh
    }

    public void fireFrom(GLEntity source){
        final float theta = source.rotation*TO_RADIANS;
        x = source.x + (float)Math.sin(theta) * (source.width*0.5f);
        y = source.y - (float)Math.cos(theta) * (source.height*0.5f);
        velX = source.velX;
        velY = source.velY;
        velX += (float)Math.sin(theta) * SPEED;
        velY -= (float)Math.cos(theta) * SPEED;
        ttl = BULLET_TIME_TO_LIVE;
    }

    @Override
    public boolean isDead(){
        return ttl < 1;
        //return ttl > 0; //old v
    }

    @Override
    public void update(double dt) {
        if(ttl > 0) {
            ttl -= dt;
            super.update(dt);
        }
    }
    @Override
    public void render(final float[] viewportMatrix){
        if(ttl > 0) {
            super.render(viewportMatrix);
        }
    }

    @Override
    public boolean isColliding(final GLEntity that){
        if(!areBoundingSpheresOverlapping(this, that)){ //quick rejection
            return false;
        }
        final PointF[] asteroidVerts = that.getPointList();
        return CollisionDetection.polygonVsPoint(asteroidVerts, x, y);
    }

    @Override
    public void onCollision(final GLEntity that) {
        super.onCollision(that);
        if (that.getClass().equals(Asteroid.class)){
            if (that.width == GameSettings.SMALL_WIDTH){
                pointWorth = 4;
            }
            else if (that.width == GameSettings.MEDIUM_WIDTH){
                pointWorth = 2;
            }
            else if (that.width == GameSettings.LARGE_WIDTH){
                pointWorth = 1;
            }
            game.onGameEvent(GameEvent.Explosion, this);
            game.pointsCollected += pointWorth;
        }
    }
}