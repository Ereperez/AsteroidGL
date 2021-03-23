package com.ereperez.asteroidgl.entities;

import android.graphics.PointF;
import android.opengl.Matrix;

import com.ereperez.asteroidgl.Game;
import com.ereperez.asteroidgl.GameSettings;
import com.ereperez.asteroidgl.Utils;
import com.ereperez.asteroidgl.render.GLManager;
import com.ereperez.asteroidgl.render.Mesh;

import java.util.Objects;

public class GLEntity {
    public static final float WORLD_WIDTH = GameSettings.WORLD_WIDTH;
    public static final float WORLD_HEIGHT = GameSettings.WORLD_HEIGHT;
    public static final float[] modelMatrix = new float[4*4];
    public static final float[] viewportModelMatrix = new float[4*4];
    public static final float[] rotationViewportModelMatrix = new float[4*4];
    public static Game game = null; //shared ref, managed by the Game-class!
    Mesh mesh = null;
    final float[] color = { 1.0f, 1.0f, 1.0f, 1.0f }; //default white
    final float depth = 0f; //we'll use _depth for z-axis if we need to entities
    float scale = 1f;
    float rotation = 0f; //angle in degrees
    public float x = 0.0f;
    public float y = 0.0f;
    public float velX = 0f;
    public float velY = 0f;
    public float velR = 0f;
    public float width = 0.0f;
    public float height = 0.0f;
    public boolean isAlive = true;

    public GLEntity(){}

    public void update(final double dt) {
        x += velX * dt;
        y += velY * dt;

        if(left() > Game.WORLD_WIDTH){
            setRight(0);
        }else if(right() < 0){
            setLeft(Game.WORLD_WIDTH);
        }
        if(top() > Game.WORLD_HEIGHT){
            setBottom(0);
        }else if(bottom() < 0){
            setTop(Game.WORLD_HEIGHT);
        }
    }

    public void render(final float[] viewportMatrix){
        final int OFFSET = 0;
        //reset the model matrix and then translate (move) it into world space
        Matrix.setIdentityM(modelMatrix, OFFSET); //reset model matrix
        Matrix.translateM(modelMatrix, OFFSET, x, y, depth);
        //viewportMatrix * modelMatrix combines into the viewportModelMatrix
        //NOTE: projection matrix on the left side and the model matrix on the right side.
        Matrix.multiplyMM(viewportModelMatrix, OFFSET, viewportMatrix, OFFSET, modelMatrix, OFFSET);
        //apply a rotation around the Z-axis to our modelMatrix. Rotation is in degrees.
        Matrix.setRotateM(modelMatrix, OFFSET, rotation, 0, 0, 1.0f);
        //apply scaling to our modelMatrix, on the x and y axis only.
        Matrix.scaleM(modelMatrix, OFFSET, scale, scale, 1f);
        //finally, multiply the rotated & scaled model matrix into the model-viewport matrix
        //creating the final rotationViewportModelMatrix that we pass on to OpenGL
        Matrix.multiplyMM(rotationViewportModelMatrix, OFFSET, viewportModelMatrix, OFFSET, modelMatrix, OFFSET);

        GLManager.draw(mesh, rotationViewportModelMatrix, color);
    }

    public boolean isDead(){
        return !isAlive;
    }

    public void onCollision(final GLEntity that) {
        isAlive = false;
    }

    public boolean isColliding(final GLEntity that) {
        if (this == that) {
            throw new AssertionError("isColliding: You shouldn't test Entities against themselves!");
        }
        return GLEntity.isAABBOverlapping(this, that);
    }

    static boolean areBoundingSpheresOverlapping(final GLEntity a, final GLEntity b) {
        final float dx = a.centerX()-b.centerX(); //delta x
        final float dy = a.centerY()-b.centerY();
        final float distanceSq = (dx*dx + dy*dy);
        final float minDistance = a.radius() + b.radius();
        final float minDistanceSq = minDistance*minDistance;
        return distanceSq < minDistanceSq;
    }

    //axis-aligned intersection test
    //returns true on intersection, and sets the least intersecting axis in the "overlap" output parameter
    static final PointF overlap = new PointF( 0 , 0 ); //Q&D PointF pool for collision detection. Assumes single threading.
    @SuppressWarnings("UnusedReturnValue")
    static boolean getOverlap(final GLEntity a, final GLEntity b, final PointF overlap) {
        overlap.x = 0.0f;
        overlap.y = 0.0f;
        final float centerDeltaX = a.centerX() - b.centerX();
        final float halfWidths = (a.width + b.width) * 0.5f;
        float dx = Math.abs(centerDeltaX); //cache the abs, we need it twice

        if (dx > halfWidths) return false ; //no overlap on x == no collision

        final float centerDeltaY = a.centerY() - b.centerY();
        final float halfHeights = (a.height + b.height) * 0.5f;
        float dy = Math.abs(centerDeltaY);

        if (dy > halfHeights) return false ; //no overlap on y == no collision

        dx = halfWidths - dx; //overlap on x
        dy = halfHeights - dy; //overlap on y
        if (dy < dx) {
            overlap.y = (centerDeltaY < 0 ) ? -dy : dy;
        } else if (dy > dx) {
            overlap.x = (centerDeltaX < 0 ) ? -dx : dx;
        } else {
            overlap.x = (centerDeltaX < 0 ) ? -dx : dx;
            overlap.y = (centerDeltaY < 0 ) ? -dy : dy;
        }
        return true ;
    }

    //Some good reading on bounding-box intersection tests:
    //https://gamedev.stackexchange.com/questions/586/what-is-the-fastest-way-to-work-out-2d-bounding-box-intersection
    static boolean isAABBOverlapping(final GLEntity a, final GLEntity b) {
        return !(a.right() <= b.left()
                || b.right() <= a.left()
                || a.bottom() <= b.top()
                || b.bottom() <= a.top());
    }

    public PointF[] getPointList(){
        return mesh.getPointList(x, y, rotation);
    }

    public float centerX() {
        return x; //assumes our mesh has been centered on [0,0] (normalized)
    }

    public float centerY() {
        return y; //assumes our mesh has been centered on [0,0] (normalized)
    }
    public float radius() {
        //use the longest side to calculate radius
        return (width > height) ? width * 0.5f : height * 0.5f;
    }
    public float left() {
        return x+mesh.left();
    }
    public  float right() {
        return x+mesh.right();
    }
    public void setLeft(final float leftEdgePosition) {
        x = leftEdgePosition - mesh.left();
    }

    public void setRight(final float rightEdgePosition) {
        x = rightEdgePosition - mesh.right();
    }
    public float top() {
        return y+mesh.top();
    }
    public float bottom() {
        return y + mesh.bottom();
    }
    public void setTop(final float topEdgePosition) {
        y = topEdgePosition - mesh.top();
    }
    public void setBottom(final float bottomEdgePosition) {
        y = bottomEdgePosition - mesh.bottom();
    }
    public void setColors(final float[] colors){
        Objects.requireNonNull(colors);
        Utils.require(colors.length >= 4);
        setColors(colors[0], colors[1], colors[2], colors[3]);
    }
    public void setColors(final float r, final float g, final float b, final float a){
        color[0] = r; //red
        color[1] = g; //green
        color[2] = b; //blue
        color[3] = a; //alpha (transparency)
    }
}