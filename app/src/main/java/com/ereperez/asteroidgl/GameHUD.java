package com.ereperez.asteroidgl;

import com.ereperez.asteroidgl.entities.Text;

import java.util.ArrayList;

/**
 * Created by edwin on 01,December,2020
 */
public class GameHUD {
    private static final String HEALTH_HUD = GameSettings.HEALTH_HUD;
    private static final String POINTS_HUD = GameSettings.POINTS_HUD;
    private static final String FPS_HUD = GameSettings.FPS_HUD;
    private static final String POWER_READY = GameSettings.POWER_READY_HUD;
    private static final String POWER_CD = GameSettings.POWER_COOLDOWN_HUD;
    private static final String POWER_HUD = GameSettings.POWER_HUD;
    private static final String LEVEL_HUD = GameSettings.LEVEL_HUD;
    private static final float HEALTH_HUD_X = GameSettings.HEALTH_HUD_X;
    private static final float HEALTH_HUD_Y = GameSettings.HEALTH_HUD_Y;
    private static final float POINTS_HUD_X = GameSettings.POINTS_HUD_X;
    private static final float POINTS_HUD_Y = GameSettings.POINTS_HUD_Y;
    private static final float FPS_HUD_X = GameSettings.FPS_HUD_X;
    private static final float FPS_HUD_Y = GameSettings.FPS_HUD_Y;
    private static final float POWER_HUD_X = GameSettings.POWER_HUD_X;
    private static final float POWER_HUD_Y = GameSettings.POWER_HUD_Y;
    private static final float LEVEL_HUD_X = GameSettings.LEVEL_HUD_X;
    private static final float LEVEL_HUD_Y = GameSettings.LEVEL_HUD_Y;

    private final ArrayList<Text> texts = new ArrayList<>();

    private String health = "";
    private String pointsCollected = "";
    private String level = "";
    private String powerStatus = "";
    private int cooldownInt = 0;
    private String fps = "";

    public GameHUD() {
        texts.add(new Text(health, HEALTH_HUD_X, HEALTH_HUD_Y));
        texts.add(new Text(pointsCollected, POINTS_HUD_X, POINTS_HUD_Y));
        texts.add(new Text(level, LEVEL_HUD_X, LEVEL_HUD_Y));
        texts.add(new Text(powerStatus, POWER_HUD_X, POWER_HUD_Y));
        texts.add(new Text(fps, FPS_HUD_X, FPS_HUD_Y));
    }

    public void statsHUD(final int mHealth, final int mPointsCollected, final int mLevel, final float mTeleportCooldown, final String mFps){
        health = String.valueOf(mHealth);
        pointsCollected = String.valueOf(mPointsCollected);
        level = String.valueOf(mLevel);
        fps = mFps;
        cooldownInt = (int) mTeleportCooldown;
        if (cooldownInt < 0){
            powerStatus = POWER_READY;
        }else{
            powerStatus = POWER_CD;
        }
        updateHUD();
    }

    private void updateHUD(){
        texts.set(0, new Text(String.format("%s%s", HEALTH_HUD, health), HEALTH_HUD_X, HEALTH_HUD_Y));
        texts.set(1, new Text(String.format("%s%s", POINTS_HUD, pointsCollected), POINTS_HUD_X, POINTS_HUD_Y));
        texts.set(2, new Text(String.format("%s%s", LEVEL_HUD, level), LEVEL_HUD_X, LEVEL_HUD_Y));
        texts.set(3, new Text(String.format("%s%s", POWER_HUD, powerStatus), POWER_HUD_X, POWER_HUD_Y));
        texts.set(4, new Text(String.format("%s%s", FPS_HUD, fps), FPS_HUD_X, FPS_HUD_Y));
    }

    public void renderHUD(final float[] viewportMatrix){
        for(final Text t : texts){
            t.render(viewportMatrix);
        }
    }
}
