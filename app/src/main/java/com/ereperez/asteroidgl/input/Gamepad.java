package com.ereperez.asteroidgl.input;

import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.ereperez.asteroidgl.MainActivity;
import com.ereperez.asteroidgl.Utils;

public class Gamepad extends InputManager implements GamepadListener {
    public static final String TAG = "Gamepad";
    private MainActivity mActivity = null;

    public Gamepad(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mActivity != null) {
            mActivity.setGamepadListener(this);
        }
        Log.d(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mActivity != null) {
            mActivity.setGamepadListener(null);
        }
    }

    @Override
    public boolean dispatchGenericMotionEvent(final MotionEvent event) {
        if((event.getSource() & InputDevice.SOURCE_JOYSTICK) != InputDevice.SOURCE_JOYSTICK){
            return false; //we don't consume this event
        }
        horizontalFactor = getInputFactor(event, MotionEvent.AXIS_X, MotionEvent.AXIS_HAT_X);
        verticalFactor = getInputFactor(event, MotionEvent.AXIS_Y, MotionEvent.AXIS_HAT_Y);
        return true; //we did consume this event
    }

    private float getInputFactor(final MotionEvent event, final int axis, final int fallbackAxis){
        InputDevice device = event.getDevice();
        int source = event.getSource();
        float result = event.getAxisValue(axis);
        InputDevice.MotionRange range = device.getMotionRange(axis, source);
        if(Math.abs(result) <= range.getFlat()){
            result = event.getAxisValue(fallbackAxis);
            range = device.getMotionRange(fallbackAxis, source);
            if(Math.abs(result) <= range.getFlat()){
                result = 0.0f;
            }
        }
        return Utils.clamp(result, -1f, 1f);
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        boolean wasConsumed = false;
        if(action == MotionEvent.ACTION_DOWN){
            if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
                verticalFactor -= 1;
                wasConsumed = true;
            }else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                verticalFactor += 1;
                wasConsumed = true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                horizontalFactor -= 1;
                wasConsumed = true;
            } else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                horizontalFactor += 1;
                wasConsumed = true;
            }
            if(keyCode == KeyEvent.KEYCODE_BUTTON_A){
                pressingBoost = true;
                wasConsumed = true;
            }
            if(keyCode == KeyEvent.KEYCODE_BUTTON_B){
                pressingLaser = true;
                wasConsumed = true;
            }
            if(keyCode == KeyEvent.KEYCODE_BUTTON_X || keyCode == KeyEvent.KEYCODE_BUTTON_Y){
                pressingTeleport = true;
                wasConsumed = true;
            }
        } else if(action == MotionEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                verticalFactor += 1;
                wasConsumed = true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                verticalFactor -= 1;
                wasConsumed = true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                horizontalFactor += 1;
                wasConsumed = true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                horizontalFactor -= 1;
                wasConsumed = true;
            }
            if (keyCode == KeyEvent.KEYCODE_BUTTON_A) {
                pressingBoost = false;
                wasConsumed = true;
            }
            if (keyCode == KeyEvent.KEYCODE_BUTTON_B) {
                pressingLaser = false;
                wasConsumed = true;
            }
            if (keyCode == KeyEvent.KEYCODE_BUTTON_X || keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
                pressingTeleport = false;
                wasConsumed = true;
            }
        }
        return wasConsumed;
    }
}
