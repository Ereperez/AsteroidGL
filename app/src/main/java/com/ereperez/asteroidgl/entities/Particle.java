package com.ereperez.asteroidgl.entities;

import android.opengl.GLES20;
import com.ereperez.asteroidgl.GameSettings;
import com.ereperez.asteroidgl.Utils;
import com.ereperez.asteroidgl.render.Mesh;

@SuppressWarnings("SuspiciousNameCombination")
public class Particle extends GLEntity{
    private static final float MAX_ROTATION = GameSettings.PARTICLE_MAX_ROTATION;
    private static final float MIN_ROTATION = GameSettings.PARTICLE_MIN_RATION;
    private static final float TO_RADIANS = (float)Math.PI/180.0f;
    private static final float SPEED = GameSettings.PARTICLE_SPEED;//120f
    private static final float TIME_TO_LIVE = GameSettings.PARTICLE_TTL;//3.0f; //seconds
    public static final float PARTICLE_WIDTH = GameSettings.PARTICLE_WIDTH;//1.5f
    public float ttl = TIME_TO_LIVE;

    public Particle(int points){
        //3 points = triangle, 4 points = square, 5 points = pentagon etc
        if(points < 3){ points = 3; } //triangles or more, please. :)
        width = PARTICLE_WIDTH;
        height = width;
        final double radius = width*0.5;
        final float[] verts = Mesh.generateLinePolygon(points, radius);
        setColors(0f,1f,0f,1f);//WHITE
        mesh = new Mesh(verts, GLES20.GL_LINES);
        mesh.setWidthHeight(width, height);
    }

    public void explodeFrom(final GLEntity source){
        final float theta = source.rotation*TO_RADIANS;
        source.rotation = Utils.between(MIN_ROTATION, MAX_ROTATION);
        x = source.x + (source.width*0.5f) * (float)Math.sin(theta);
        y = source.y - (source.height*0.5f) * (float)Math.cos(theta);
        velX = source.velX;
        velY = source.velY;
        velX += (float)Math.sin(theta) * SPEED;
        velY -= (float)Math.cos(theta) * SPEED;
        ttl = TIME_TO_LIVE;
    }

    @Override
    public boolean isDead(){
        return ttl < 1;
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
}
