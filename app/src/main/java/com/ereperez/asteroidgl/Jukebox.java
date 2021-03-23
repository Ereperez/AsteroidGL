package com.ereperez.asteroidgl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Edwin on 01,December,2020
 */
public class Jukebox {
    public static final String TAG = "Jukebox";
    private static final float SFX_LEFT_VOLUME = GameSettings.SFX_LEFT_VOLUME;
    private static final float SFX_RIGHT_VOLUME = GameSettings.SFX_RIGHT_VOLUME;
    private static final float SFX_RATE = GameSettings.SFX_RATE;
    private static final int SFX_PRIORITY = GameSettings.SFX_PRIORITY;
    private static final int SFX_LOOP = GameSettings.SFX_LOOP;
    private static final float DEFAULT_MUSIC_VOLUME = GameSettings.DEFAULT_MUSIC_VOLUME;
    private static final int MAX_STREAMS = GameSettings.MAX_STREAMS;
    public static final String SOUNDS_PREF_KEY = "sounds_pref_key";
    public static final String MUSIC_PREF_KEY = "music_pref_key";

    SoundPool soundPool = null;
    MediaPlayer mBgPlayer = null;
    private HashMap<GameEvent, Integer> soundsMap = null;
    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    private final Context context;
    public SharedPreferences prefs = null;

    public Jukebox(Context mContext) {
        context = mContext;
        prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        soundEnabled = prefs.getBoolean(SOUNDS_PREF_KEY, true);
        musicEnabled = prefs.getBoolean(MUSIC_PREF_KEY, true);
        loadIfNeeded();
    }

    private void loadIfNeeded(){
        if(soundEnabled){
            loadSounds();
        }
        if(musicEnabled){
            loadMusic();
        }
    }

    public boolean getSoundStatus(){
        return prefs.getBoolean(SOUNDS_PREF_KEY, true);
    }

    public boolean getMusicStatus(){
        return prefs.getBoolean(MUSIC_PREF_KEY, true);
    }

    public void toggleSoundStatus(){
        soundEnabled = !soundEnabled;
        if(soundEnabled){
            loadSounds();
        }else{
            unloadSounds();
        }
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SOUNDS_PREF_KEY, soundEnabled)
                .apply();
    }

    public void toggleMusicStatus(){
        musicEnabled = !musicEnabled;
        if(musicEnabled){
            loadMusic();
            mBgPlayer.start();
        }else{
            unloadMusic();
        }
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(MUSIC_PREF_KEY, musicEnabled)
                .apply();
    }

    public void playSoundForGameEvent(GameEvent event){
        if(!soundEnabled){return;}
        final Integer soundID = (Integer) soundsMap.get(event);
        if(soundID != null){
            soundPool.play(soundID, SFX_LEFT_VOLUME, SFX_RIGHT_VOLUME, SFX_PRIORITY, SFX_LOOP, SFX_RATE);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("deprecation")
    private void createSoundPool() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }
        else {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    .setMaxStreams(MAX_STREAMS)
                    .build();
        }
    }

    private void loadEventSound(final GameEvent event, final String fileName){
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(fileName);
            int soundId = soundPool.load(afd, 1);
            soundsMap.put(event, soundId);
            Log.d(TAG, "soundMap: " + soundId);
        }catch(IOException e){
            Log.e(TAG, "loadEventSound: error loading sound " + e.toString());
        }
    }

    private void loadSounds(){
        createSoundPool();
        soundsMap = new HashMap<>();
        loadEventSound(GameEvent.GameOver, "sfx/gameOver.wav");
        loadEventSound(GameEvent.GameStart, "sfx/gameStart.wav");
        loadEventSound(GameEvent.Laser, "sfx/laser.wav");
        loadEventSound(GameEvent.Powerup, "sfx/powerup.wav");
        loadEventSound(GameEvent.TakeDamage, "sfx/takeDamage.wav");
        loadEventSound(GameEvent.Teleport, "sfx/teleport.wav");
        loadEventSound(GameEvent.Explosion, "sfx/explosion.wav");
    }

    private void unloadSounds(){
        if(soundPool != null) {
            soundPool.release();
            soundPool = null;
            soundsMap.clear();
        }
    }

    private void loadMusic(){
        try{
            mBgPlayer = new MediaPlayer();
            AssetFileDescriptor afd = context
                    .getAssets().openFd("sfx/backgroundMusic.mp3");
            mBgPlayer.setDataSource(
                    afd.getFileDescriptor(),
                    afd.getStartOffset(),
                    afd.getLength());
            mBgPlayer.setLooping(true);
            mBgPlayer.setVolume(DEFAULT_MUSIC_VOLUME, DEFAULT_MUSIC_VOLUME);
            mBgPlayer.prepare();
            Log.d(TAG, "music loaded");
        }catch(IOException e){
            mBgPlayer = null;
            musicEnabled = false;
            Log.e(TAG, "loadMusic: error loading music " + e.toString());
        }
    }

    private void unloadMusic(){
        if(mBgPlayer != null) {
            mBgPlayer.stop();
            mBgPlayer.release();
        }
    }

    public void pauseBgMusic(){
        if(!musicEnabled){ return; }
        mBgPlayer.pause();
    }
    public void resumeBgMusic(){
        if(!musicEnabled){ return; }
        mBgPlayer.start();
    }
}
