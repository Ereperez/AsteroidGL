package com.ereperez.asteroidgl.entities;

import android.opengl.GLES20;

import com.ereperez.asteroidgl.GameSettings;
import com.ereperez.asteroidgl.Utils;
import com.ereperez.asteroidgl.render.Mesh;

@SuppressWarnings("SuspiciousNameCombination")
public class Asteroid extends GLEntity {
    private static final float SMALL_MAX_VEL = GameSettings.SMALL_MAX_VEL;
    private static final float SMALL_MIN_VEL = GameSettings.SMALL_MIN_VEL;
    private static final float MEDIUM_MAX_VEL = GameSettings.MEDIUM_MAX_VEL;
    private static final float MEDIUM_MIN_VEL = GameSettings.MEDIUM_MIN_VEL;
    private static final float LARGE_MAX_VEL = GameSettings.LARGE_MAX_VEL;
    private static final float LARGE_MIN_VEL = GameSettings.LARGE_MIN_VEL;
    private static final float SMALL_WIDTH = GameSettings.SMALL_WIDTH;
    private static final float MEDIUM_WIDTH = GameSettings.MEDIUM_WIDTH;
    private static final float LARGE_WIDTH = GameSettings.LARGE_WIDTH;
    //default values if something goes wrong
    private static float MAX_VEL = 0f;//14f;//8f;
    private static float MIN_VEL = 0f;//-14f;//-8f;

    public Asteroid(final int size, final float _x, final float _y, int points){
        //3 points = triangle, 4 points = square, 5 points = pentagon etc
        if(points < 3){ points = 3; } //triangles or more, please. :)
        x = _x;
        y = _y;
        switch (size){
            case 0:
                width = SMALL_WIDTH;
                height = width;
                MAX_VEL = SMALL_MAX_VEL;
                MIN_VEL = SMALL_MIN_VEL;
                setColors(1f,0f,1f,1f);//Purple
                break;
            case 1:
                width = MEDIUM_WIDTH;
                height = width;
                MAX_VEL = MEDIUM_MAX_VEL;
                MIN_VEL = MEDIUM_MIN_VEL;
                break;
            case 2:
                width = LARGE_WIDTH;
                height = width;
                MAX_VEL = LARGE_MAX_VEL;
                MIN_VEL = LARGE_MIN_VEL;
                setColors(0f,0f,1f,1f);//Blue
                break;
/*            default:
                width = LARGE_WIDTH;
                height = width;
                MAX_VEL = LARGE_MAX_VEL;
                MIN_VEL = LARGE_MIN_VEL;
                setColors(0f,0f,1f,1f);//Blue
                break;*/
        }
        velX = Utils.between(MIN_VEL*2, MAX_VEL*2);
        velY = Utils.between(MIN_VEL*2, MAX_VEL*2);
        velR = Utils.between(MIN_VEL*4, MAX_VEL*4);
        final double radius = width*0.5;
        final float[] verts = Mesh.generateLinePolygon(points, radius);
        mesh = new Mesh(verts, GLES20.GL_LINES);
        mesh.setWidthHeight(width, height);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        rotation++;
    }

    @Override
    public void onCollision(GLEntity that) {
        game.getParticles(this);
        super.onCollision(that);
    }
}