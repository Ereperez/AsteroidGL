package com.ereperez.asteroidgl.render;

import android.graphics.PointF;
import android.opengl.GLES20;

import com.ereperez.asteroidgl.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Mesh {
    private static final String TAG = "Mesh";
    public static final float[] POINT = new float[]{0, 0, 0};
    // find the size of the float type, in bytes
    public static final int SIZE_OF_FLOAT = Float.SIZE/Byte.SIZE; //32bit/8bit = 4 bytes
    // number of coordinates per vertex in our meshes
    public static final int COORDS_PER_VERTEX = 3; //X, Y, Z
    // number of bytes per vertex
    public static final int VERTEX_STRIDE = COORDS_PER_VERTEX * SIZE_OF_FLOAT;

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    public FloatBuffer vertexBuffer = null;
    public int vertexCount = 0;
    public int drawMode = GLES20.GL_TRIANGLES;
    public float width = 0f;
    public float height = 0f;
    public float depth = 0f;
    public float radius = 0f;
    public final Point3D min = new Point3D();
    public final Point3D max = new Point3D();

    public Mesh(final float[] geometry){
        init(geometry, GLES20.GL_TRIANGLES);
    }
    public Mesh(final float[] geometry, final int drawMode){
        init(geometry, drawMode);
    }
    private void init(final float[] geometry, final int drawMode){
        setVertices(geometry);
        setDrawmode(drawMode);
    }

    public static float[] generateLinePolygon(final int numPoints, final double radius) {
        Utils.require(numPoints > 2, "a polygon requires at least 3 points.");
        final int numVerts = numPoints * 2; //we render lines, and each line requires 2 points
        final float[] verts = new float[numVerts * Mesh.COORDS_PER_VERTEX];
        double step = 2.0 * Math.PI / numPoints;
        int i = 0, point = 0;
        while (point < numPoints) { //generate verts on circle, 2 per point
            double theta = point * step;
            verts[i++] = (float) (Math.cos(theta) * radius); //X
            verts[i++] = (float) (Math.sin(theta) * radius); //Y
            verts[i++] = 0f;                                //Z
            point++;
            theta = point * step;
            verts[i++] = (float) (Math.cos(theta) * radius); //X
            verts[i++] = (float) (Math.sin(theta) * radius); //Y
            verts[i++] = 0f;                                 //Z
        }
        return verts;
    }

    public void setDrawmode(int _drawMode){
        Utils.require(drawMode == GLES20.GL_TRIANGLES
                || drawMode == GLES20.GL_LINES
                || drawMode == GLES20.GL_POINTS);
        drawMode = _drawMode;
    }

    public void setVertices(final float[] geometry){
        // create a floating point buffer from a ByteBuffer
        vertexBuffer = ByteBuffer.allocateDirect(geometry.length * SIZE_OF_FLOAT)
                .order(ByteOrder.nativeOrder()) // use the device hardware's native byte order
                .asFloatBuffer();
        vertexBuffer.put(geometry); //add the coordinates to the FloatBuffer
        vertexBuffer.position(0); // set the buffer to read the first coordinate
        vertexCount = geometry.length / COORDS_PER_VERTEX;
        updateBounds();
        normalize(); //center the mesh on [0,0,0] and scale to [-1, -1, -1][1, 1, 1]
    }

    //scale mesh to normalized device coordinates [-1.0, 1.0]
    public void normalize() {
        final double inverseW = (width  == 0.0) ? 0.0 : 1/width;
        final double inverseH = (height == 0.0) ? 0.0 : 1/height;
        final double inverseD = (depth  == 0.0) ? 0.0 : 1/depth;
        for (int i = 0; i < vertexCount * COORDS_PER_VERTEX; i += COORDS_PER_VERTEX) {
            final double dx = vertexBuffer.get(i + X) - min.x; //"d" for "delta" or "difference"
            final double dy = vertexBuffer.get(i + Y) - min.y;
            final double dz = vertexBuffer.get(i + Z) - min.z;
            final double xNorm = 2.0 * (dx * inverseW) - 1.0; //(dx * inverseW) is equivalent to (dx / _width)
            final double yNorm = 2.0 * (dy * inverseH) - 1.0; //but avoids the risk of division-by-zero.
            final double zNorm = 2.0 * (dz * inverseD) - 1.0;
            vertexBuffer.put(i+X, (float)xNorm);
            vertexBuffer.put(i+Y, (float)yNorm);
            vertexBuffer.put(i+Z, (float)zNorm);
        }
        updateBounds();
        Utils.require(width <= 2.0f, "x-axis is out of range!");
        Utils.require(height <= 2.0f, "y-axis is out of range!");
        Utils.require(depth <= 2.0f, "z-axis is out of range!");
        Utils.expect((min.x >= -1.0f && max.x <= 1.0f), TAG, "normalized x["+min.x+", "+max.x+"] expected x[-1.0, 1.0]");
        Utils.expect((min.y >= -1.0f && max.y <= 1.0f), TAG, "normalized y["+min.y+", "+max.y+"] expected y[-1.0, 1.0]");
        Utils.expect((min.z >= -1.0f && max.z <= 1.0f), TAG, "normalized z["+min.z+", "+max.z+"] expected z[-1.0, 1.0]");
    }

    public void updateBounds(){
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for(int i = 0; i < vertexCount*COORDS_PER_VERTEX; i+=COORDS_PER_VERTEX) {
            final float x = vertexBuffer.get(i+X);
            final float y = vertexBuffer.get(i+Y);
            final float z = vertexBuffer.get(i+Z);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }
        min.set(minX, minY, minZ);
        max.set(maxX, maxY, maxZ);
        width = maxX - minX;
        height = maxY - minY;
        depth = maxZ - minZ;
        radius = Math.max(Math.max(width, height), depth) * 0.5f;
    }

    public void setWidthHeight(final double w, final double h){
        normalize();  //a normalized mesh is centered at [0,0] and ranges from [-1,1]
        scale(w*0.5, h*0.5, 1.0); //meaning we now scale from the center, so *0.5 (radius)
        Utils.require(Math.abs(w-width) < Float.MIN_NORMAL && Math.abs(h-height) < Float.MIN_NORMAL,
                "incorrect width / height after scaling!");
    }

    public void scale(final double xFactor, final double yFactor, final double zFactor){
        for(int i = 0; i < vertexCount*COORDS_PER_VERTEX; i+=COORDS_PER_VERTEX) {
            vertexBuffer.put(i+X, (float)(vertexBuffer.get(i+X) * xFactor));
            vertexBuffer.put(i+Y, (float)(vertexBuffer.get(i+Y) * yFactor));
            vertexBuffer.put(i+Z, (float)(vertexBuffer.get(i+Z) * zFactor));
        }
        updateBounds();
    }

    private void rotate(final int axis, final double theta) {
        Utils.require(axis == X || axis == Y || axis == Z);
        final double sinTheta = Math.sin(theta);
        final double cosTheta = Math.cos(theta);
        for (int i = 0; i < vertexCount * COORDS_PER_VERTEX; i += COORDS_PER_VERTEX) {
            final double x = vertexBuffer.get(i + X);
            final double y = vertexBuffer.get(i + Y);
            final double z = vertexBuffer.get(i + Z);
            if (axis == Z) {
                vertexBuffer.put(i + X, (float) (x * cosTheta - y * sinTheta));
                vertexBuffer.put(i + Y, (float) (y * cosTheta + x * sinTheta));
            } else if (axis == Y) {
                vertexBuffer.put(i + X, (float) (x * cosTheta - z * sinTheta));
                vertexBuffer.put(i + Z, (float) (z * cosTheta + x * sinTheta));
            } else if (axis == X) {
                vertexBuffer.put(i + Y, (float) (y * cosTheta - z * sinTheta));
                vertexBuffer.put(i + Z, (float) (z * cosTheta + y * sinTheta));
            }
        }
        updateBounds();
    }

    public void flip(final int axis) {
        Utils.require (axis == X || axis == Y || axis == Z);
        vertexBuffer.position(0);
        for (int i = 0; i < vertexCount; i++) {
            final int index = i * COORDS_PER_VERTEX + axis;
            final float invertedCoordinate = vertexBuffer.get(index) * -1;
            vertexBuffer.put(index, invertedCoordinate);
            updateBounds();
        }
    }

    public PointF[] getPointList(final float offsetX, final float offsetY, final float facingAngleDegrees){
        final double sinTheta = Math.sin(facingAngleDegrees*Utils.TO_RAD);
        final double cosTheta = Math.cos(facingAngleDegrees*Utils.TO_RAD);
        float[] verts = new float[vertexCount*COORDS_PER_VERTEX];
        vertexBuffer.position(0);
        vertexBuffer.get(verts);
        vertexBuffer.position(0);
        PointF[] out = new PointF[vertexCount];
        int index = 0;
        for (int i = 0; i < vertexCount * COORDS_PER_VERTEX; i += COORDS_PER_VERTEX) {
            final float x = verts[i + X];
            final float y = verts[i + Y];
            final float rotatedX = (float) (x * cosTheta - y * sinTheta) + offsetX;
            final float rotatedY = (float) (y * cosTheta + x * sinTheta) + offsetY;
            //final float z = verts[i + Z];
            out[index++] = new PointF(rotatedX, rotatedY);
        }
        return out;
    }

    public void scale(final double factor) { scale(factor, factor, factor); }
    public void scaleX(final double factor){ scale(factor, 1.0, 1.0); }
    public void scaleY(final double factor){ scale(1.0, factor, 1.0); }
    public void scaleZ(final double factor){ scale(1.0, 1.0, factor); }
    public void flipX(){ scaleX(-1.0); }
    public void flipY(){ scaleY(-1.0); }
    public void flipZ(){ scaleZ(-1.0); }

    public void rotateX(final double theta) {    rotate(X, theta); }
    public void rotateY(final double theta) {    rotate(Y, theta); }
    public void rotateZ(final double theta) {    rotate(Z, theta); }

    public float left() {
        return min.x;
    }
    public float right() {
        return max.x;
    }
    public float top() {
        return min.y;
    }
    public float bottom() {
        return max.y;
    }
    public float centerX() {
        return min.x + (width * 0.5f);
    }
    public float centerY() {
        return min.y + (height * 0.5f);
    }

}