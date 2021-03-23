package com.ereperez.asteroidgl.render;

public class FPSCounter {
    public static final long SECOND_IN_NANOSECONDS = 1000000000;
    public static final float NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS;

    private double lastTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;
    private int fps_current = 0;
    private String fps = "";//null

    public FPSCounter() {
    }

    //GLPixelFont requires String
    public String fpsCount() {
        final double currentTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;
        fps_current++;
        if (currentTime - lastTime >= 1) {//0.01
            fps = String.valueOf(fps_current);
            fps_current = 0;
            lastTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;
        }
        return fps;
    }
}
