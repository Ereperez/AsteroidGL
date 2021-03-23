package com.ereperez.asteroidgl.entities;

import android.opengl.GLES20;

import com.ereperez.asteroidgl.Utils;
import com.ereperez.asteroidgl.render.Mesh;

public class Border extends GLEntity {
    public Border(final float _x, final float _y, final float worldWidth, final float worldHeight){
        super();
        x = _x;
        y = _y;
        width = worldWidth-1.0f; //-1 so the border isn't obstructed by the screen edge
        height = worldHeight-1.0f;
        setColors(0f, 0f, 0f, 1f); //BLACK to blend into background
        mesh = new Mesh(Mesh.generateLinePolygon(4, 10.0), GLES20.GL_LINES);
        mesh.rotateZ(45* Utils.TO_RAD);
        mesh.setWidthHeight(width, height); //will automatically normalize the mesh!
    }
}