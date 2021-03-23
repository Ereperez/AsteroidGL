package com.ereperez.asteroidgl.entities;

import com.ereperez.asteroidgl.GameSettings;
import com.ereperez.asteroidgl.shapes.Triangle;


@SuppressWarnings("SuspiciousNameCombination")
public class BoostFlame extends GLEntity{
    private static final float TO_RADIANS = (float)Math.PI/180.0f;
    private static final float SPEED = GameSettings.FLAME_SPEED;//120f;
    private static final float TIME_TO_LIVE = GameSettings.FLAME_TTL;//3.0f; //seconds
    public static final float FLAME_WIDTH = GameSettings.FLAME_WIDTH;//120f;
    public float ttl = TIME_TO_LIVE;

    public BoostFlame(){
        width = FLAME_WIDTH;
        height = width;
        setColors(1f, 0f, 0f, 1f);//RED
        mesh = new Triangle();
        mesh.setWidthHeight(width, height);
    }

    public void flameFrom(final GLEntity source){
        final float theta = source.rotation*TO_RADIANS;
        x = source.x - (float)Math.sin(theta) * (source.width*0.5f);
        y = source.y + (float)Math.cos(theta) * (source.height*0.5f);
        velX = source.velX;
        velY = source.velY;
        velX -= (float)Math.sin(theta) * SPEED;
        velY += (float)Math.cos(theta) * SPEED;
        ttl = TIME_TO_LIVE;
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
        //scale = 10f; //render at 20x the size
    }
}
