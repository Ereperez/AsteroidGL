package com.ereperez.asteroidgl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ereperez.asteroidgl.input.CompositeControl;
import com.ereperez.asteroidgl.input.Gamepad;
import com.ereperez.asteroidgl.input.GamepadListener;
import com.ereperez.asteroidgl.input.InputManager;
import com.ereperez.asteroidgl.input.TouchController;

public class MainActivity extends AppCompatActivity implements InputManager.InputDeviceListener {//implements android.hardware.input.InputManager.InputDeviceListener
    private static final String TAG = "MainActivity";
    private GameSettings gameSettings = null;
    private Game game = null;
    GamepadListener gamepadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        gameSettings = new GameSettings(this);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //InputManager controls = (InputManager) new TouchController(findViewById(R.id.gamepad));
        InputManager controls = new CompositeControl(
                new TouchController(findViewById(R.id.gamepad)),
                new Gamepad(this)
        );
        game = findViewById(R.id.game);
        game.setControls(controls);
    }

    public void setGamepadListener(GamepadListener listener) {
        gamepadListener = listener;
    }

    @Override
    public boolean dispatchGenericMotionEvent(final MotionEvent ev) {
        if(gamepadListener != null){
            if(gamepadListener.dispatchGenericMotionEvent(ev)){
                return true;
            }
        }
        return super.dispatchGenericMotionEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent ev) {
        if(gamepadListener != null){
            if(gamepadListener.dispatchKeyEvent(ev)){
                return true;
            }
        }
        return super.dispatchKeyEvent(ev);
    }

    @Override
    public void onInputDeviceAdded(final int deviceId) {
        InputDevice dev = InputDevice.getDevice(deviceId);
        int sources = dev.getSources();
        if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
                ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
            Toast.makeText(this, String.format("%s Added!", InputDevice.getDevice(deviceId).getName()), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Input Device Added!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInputDeviceRemoved(final int deviceId) {
        Toast.makeText(this, "Input Device Removed!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInputDeviceChanged(final int deviceId) {
        Toast.makeText(this, "Input Device Changed!", Toast.LENGTH_LONG).show();
    }

    public boolean isGameControllerConnected() {
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
                    ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
                Log.d("Controller:", InputDevice.getDevice(deviceId).getName());
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
        if(isGameControllerConnected()){
            Toast.makeText(this, "Gamepad detected!", Toast.LENGTH_LONG).show();
        }
        game.onResume();
    }

    @Override
    protected void onPause(){
        Log.d(TAG, "onPause");
        super.onPause();
        game.onPause();
    }

    @Override
    protected void onDestroy(){
        Log.d(TAG, "onDestroy");
        game.onDestroy();
        super.onDestroy();
    }

    //@SuppressWarnings("deprecation")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            return;
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}