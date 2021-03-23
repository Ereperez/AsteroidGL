package com.ereperez.asteroidgl.entities;

import android.graphics.Color;
import android.opengl.GLES20;

import com.ereperez.asteroidgl.render.Mesh;

public class Star extends GLEntity {
    private static Mesh m = null; //Q&D pool

    public Star(final float _x, final float _y){
        super();
        x = _x;
        y = _y;
        color[0] = Color.red(Color.YELLOW) / 255f;
        color[1] = Color.green(Color.YELLOW) / 255f;
        color[2] = Color.blue(Color.YELLOW) / 255f;
        color[3] = 1f;
        if(m == null) {
            final float[] vertices = {0, 0, 0};
            m = new Mesh(vertices, GLES20.GL_POINTS);
        }
        mesh = m; //all Stars use the exact same Mesh instance.
    }
}
