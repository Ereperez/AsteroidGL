package com.ereperez.asteroidgl.render;


import com.ereperez.asteroidgl.Utils;

public class Point3D {
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;

    public Point3D(){}
    public Point3D(final float _x, final float _y, final float _z){
        set(_x, _y, _z);
    }

    public Point3D(final float[] p){
        set(p);
    }

    public void set(final float _x, final float _y, final float _z){
        x = _x;
        y = _y;
        z = _z;
    }

    public void set(final float[] p){
        Utils.require(p.length == 3);
        x = p[0];
        y = p[1];
        z = p[2];
    }

    public final  float distanceSquared(Point3D that){
        final float dx = this.x-that.x;
        final float dy = this.y-that.y;
        final float dz = this.z-that.z;
        return dx*dx+dy*dy+dz*dz;
    }
    public final float distance(Point3D that){
        final float dx = this.x-that.x;
        final float dy = this.y-that.y;
        final float dz = this.z-that.z;
        return (float) Math.sqrt(dx*dx+dy*dy+dz*dz);
    }
    public final  float distanceL1(Point3D that){
        return(Math.abs(this.x-that.x) + Math.abs(this.y-that.y) + Math.abs(this.z-that.z));
    }
}
