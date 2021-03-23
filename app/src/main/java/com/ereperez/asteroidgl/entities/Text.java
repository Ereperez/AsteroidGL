package com.ereperez.asteroidgl.entities;

import android.opengl.Matrix;

import com.ereperez.asteroidgl.GameSettings;
import com.ereperez.asteroidgl.render.GLManager;
import com.ereperez.asteroidgl.render.GLPixelFont;
import com.ereperez.asteroidgl.render.Mesh;

public class Text extends GLEntity {
    public static final GLPixelFont FONT = new GLPixelFont();
    public static final float GLYPH_WIDTH = FONT.WIDTH;
    public static final float GLYPH_HEIGHT = FONT.HEIGHT;
    public static final float GLYPH_SPACING = 1f;
    public static final float TEXT_SCALE = GameSettings.TEXT_SCALE;
    Mesh[] meshes = null;
    private float spacing = GLYPH_SPACING; //spacing between characters
    private float glyphWidth = GLYPH_WIDTH;
    private float glyphHeight = GLYPH_HEIGHT;

    public Text(final String s, final float _x, final float _y) {
        setString(s);
        x = _x;
        y = _y;
        setScale(TEXT_SCALE);
    }

    @Override
    public void render(final float[] viewportMatrix){
        final int OFFSET = 0;
        for(int i = 0; i < meshes.length; i++){
            if(meshes[i] == null){ continue; }
            Matrix.setIdentityM(modelMatrix, OFFSET); //reset model matrix
            Matrix.translateM(modelMatrix, OFFSET, x + (glyphWidth + spacing)*i, y, depth);
            Matrix.scaleM(modelMatrix, OFFSET, scale, scale, 1f);
            Matrix.multiplyMM(viewportModelMatrix, OFFSET, viewportMatrix, OFFSET, modelMatrix, OFFSET);
            GLManager.draw(meshes[i], viewportModelMatrix, color);
        }
    }

    public void setScale(float factor){
        scale = factor;
        spacing = GLYPH_SPACING*scale;
        glyphWidth = GLYPH_WIDTH*scale;
        glyphHeight = GLYPH_HEIGHT*scale;
        height = glyphHeight;
        width = (glyphWidth + spacing)* meshes.length;
    }

    public void setString(final String s){
        meshes = FONT.getString(s);
    }
}