package com.ereperez.asteroidgl;

import android.content.Context;
import android.content.res.Resources;

/**
 * Loads values from res/values/game_settings and strings
 */
public class GameSettings {
    public static String HEALTH_HUD;
    public static float HEALTH_HUD_X;
    public static float HEALTH_HUD_Y;
    public static float POINTS_HUD_X;
    public static float POINTS_HUD_Y;
    public static float FPS_HUD_X;
    public static float FPS_HUD_Y;
    public static float POWER_HUD_X;
    public static float POWER_HUD_Y;
    public static float LEVEL_HUD_X;
    public static float LEVEL_HUD_Y;
    public static float SFX_LEFT_VOLUME;
    public static float SFX_RIGHT_VOLUME;
    public static float SFX_RATE;
    public static int SFX_PRIORITY;
    public static int SFX_LOOP;
    public static float DEFAULT_MUSIC_VOLUME;
    public static int MAX_STREAMS;

    public static int PLAYER_HEALTH;
    public static int PLAYER_IMMUNITY_TIME;
    public static int BG_COLOR;
    public static int GAME_LEVEL_ONE;
    public static float WORLD_WIDTH;
    public static float WORLD_HEIGHT;
    public static float TIME_BETWEEN_SHOTS;
    public static float TIME_BETWEEN_TELEPORT;
    public static float TIME_BETWEEN_BOOST;
    public static String POINTS_HUD;
    public static String FPS_HUD;
    public static String POWER_READY_HUD;
    public static String POWER_COOLDOWN_HUD;
    public static String POWER_HUD;
    public static String LEVEL_HUD;
    public static int STAR_COUNT;
    public static int PARTICLE_COUNT;
    public static int ASTEROID_COUNT;
    public static int FLAME_COUNT;

    public static float TEXT_SCALE;
    public static float ROTATION_VELOCITY;
    public static float THRUST;
    public static float DRAG;
    public static float PARTICLE_MAX_ROTATION;
    public static float PARTICLE_MIN_RATION;
    public static float PARTICLE_SPEED;
    public static float PARTICLE_TTL;
    public static float BULLET_SPEED;
    public static float BULLET_TTL;
    public static float FLAME_SPEED;
    public static float FLAME_TTL;
    public static float SMALL_MAX_VEL;
    public static float SMALL_MIN_VEL;
    public static float MEDIUM_MAX_VEL;
    public static float MEDIUM_MIN_VEL;
    public static float LARGE_MAX_VEL;
    public static float LARGE_MIN_VEL;
    public static float SMALL_WIDTH;
    public static float MEDIUM_WIDTH;
    public static float LARGE_WIDTH;
    public static float FLAME_WIDTH;
    public static float PARTICLE_WIDTH;
    public static float PLAYER_WIDTH;
    public static float PLAYER_HEIGHT;

    GameSettings(Context ctx) {
        loadSettings(ctx);
    }

    public void loadSettings(Context ctx){
        Resources res = ctx.getResources();

        PLAYER_HEALTH = res.getInteger(R.integer.player_health);
        PLAYER_IMMUNITY_TIME = res.getInteger(R.integer.player_immunity_time);

        POINTS_HUD = res.getString(R.string.points_collected_hud);
        HEALTH_HUD = res.getString(R.string.health_hud);
        FPS_HUD = res.getString(R.string.fps_hud);
        POWER_READY_HUD = res.getString(R.string.power_ready_hud);
        POWER_COOLDOWN_HUD = res.getString(R.string.power_cooldown_hud);
        POWER_HUD = res.getString(R.string.power_hud);
        LEVEL_HUD = res.getString(R.string.level_hud);

        HEALTH_HUD_X = Float.parseFloat(res.getString(R.string.health_hud_x));
        HEALTH_HUD_Y = Float.parseFloat(res.getString(R.string.health_hud_y));
        POINTS_HUD_X = Float.parseFloat(res.getString(R.string.points_hud_x));
        POINTS_HUD_Y = Float.parseFloat(res.getString(R.string.points_hud_y));
        FPS_HUD_X = Float.parseFloat(res.getString(R.string.fps_hud_x));
        FPS_HUD_Y = Float.parseFloat(res.getString(R.string.fps_hud_y));
        POWER_HUD_X = Float.parseFloat(res.getString(R.string.power_hud_x));
        POWER_HUD_Y = Float.parseFloat(res.getString(R.string.power_hud_y));
        LEVEL_HUD_X = Float.parseFloat(res.getString(R.string.level_hud_x));
        LEVEL_HUD_Y = Float.parseFloat(res.getString(R.string.level_hud_y));

        SFX_LEFT_VOLUME = Float.parseFloat(res.getString(R.string.sfx_left_volume));
        SFX_RIGHT_VOLUME = Float.parseFloat(res.getString(R.string.sfx_right_volume));
        SFX_RATE = Float.parseFloat(res.getString(R.string.sfx_rate));
        SFX_PRIORITY = res.getInteger(R.integer.sfx_priority);
        SFX_LOOP = res.getInteger(R.integer.sfx_loop);
        DEFAULT_MUSIC_VOLUME = Float.parseFloat(res.getString(R.string.default_music_volume));
        MAX_STREAMS = res.getInteger(R.integer.max_streams);

        BG_COLOR = res.getInteger(R.integer.bg_color);
        GAME_LEVEL_ONE = res.getInteger(R.integer.game_level_one);
        WORLD_WIDTH = Float.parseFloat(res.getString(R.string.world_width));
        WORLD_HEIGHT = Float.parseFloat(res.getString(R.string.world_height));
        TIME_BETWEEN_SHOTS = Float.parseFloat(res.getString(R.string.laser_cd));
        TIME_BETWEEN_TELEPORT = Float.parseFloat(res.getString(R.string.teleport_cd));
        TIME_BETWEEN_BOOST = Float.parseFloat(res.getString(R.string.boost_cd));
        STAR_COUNT = res.getInteger(R.integer.star_count);
        ASTEROID_COUNT = res.getInteger(R.integer.asteroid_count);
        PARTICLE_COUNT = res.getInteger(R.integer.particle_count);
        FLAME_COUNT = res.getInteger(R.integer.flame_count);

        TEXT_SCALE = Float.parseFloat(res.getString(R.string.text_scale));
        ROTATION_VELOCITY = Float.parseFloat(res.getString(R.string.rotation_velocity));
        THRUST = Float.parseFloat(res.getString(R.string.thrust));
        DRAG = Float.parseFloat(res.getString(R.string.drag));
        PARTICLE_MAX_ROTATION = Float.parseFloat(res.getString(R.string.min_particle_rotation));
        PARTICLE_MIN_RATION = Float.parseFloat(res.getString(R.string.max_particle_rotation));
        PARTICLE_SPEED = Float.parseFloat(res.getString(R.string.particle_speed));
        PARTICLE_TTL = Float.parseFloat(res.getString(R.string.particle_time_to_live));
        BULLET_SPEED = Float.parseFloat(res.getString(R.string.bullet_speed));
        BULLET_TTL = Float.parseFloat(res.getString(R.string.bullet_time_to_live));
        FLAME_SPEED = Float.parseFloat(res.getString(R.string.flame_speed));
        FLAME_TTL = Float.parseFloat(res.getString(R.string.flame_time_to_live));

        SMALL_MAX_VEL = Float.parseFloat(res.getString(R.string.small_max_vel));
        SMALL_MIN_VEL = Float.parseFloat(res.getString(R.string.small_min_vel));
        MEDIUM_MAX_VEL = Float.parseFloat(res.getString(R.string.medium_max_vel));
        MEDIUM_MIN_VEL = Float.parseFloat(res.getString(R.string.medium_min_vel));
        LARGE_MAX_VEL = Float.parseFloat(res.getString(R.string.large_max_vel));
        LARGE_MIN_VEL = Float.parseFloat(res.getString(R.string.large_min_vel));
        SMALL_WIDTH = Float.parseFloat(res.getString(R.string.small_width));
        MEDIUM_WIDTH = Float.parseFloat(res.getString(R.string.medium_width));
        LARGE_WIDTH = Float.parseFloat(res.getString(R.string.large_width));
        FLAME_WIDTH = Float.parseFloat(res.getString(R.string.flame_width));
        PARTICLE_WIDTH = Float.parseFloat(res.getString(R.string.particle_width));
        PLAYER_WIDTH = Float.parseFloat(res.getString(R.string.player_width));
        PLAYER_HEIGHT = Float.parseFloat(res.getString(R.string.player_height));
    }
}
