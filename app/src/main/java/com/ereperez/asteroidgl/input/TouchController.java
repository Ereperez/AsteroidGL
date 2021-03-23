package com.ereperez.asteroidgl.input;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import com.ereperez.asteroidgl.R;

public class TouchController extends InputManager implements View.OnTouchListener{
    public TouchController(View view){
        view.findViewById(R.id.keypad_left).setOnTouchListener(this);
        view.findViewById(R.id.keypad_right).setOnTouchListener(this);
        view.findViewById(R.id.keypad_laser).setOnTouchListener(this);
        view.findViewById(R.id.keypad_boost).setOnTouchListener(this);
        view.findViewById(R.id.keypad_teleport).setOnTouchListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getActionMasked();
        final int id = v.getId();
        if(action == MotionEvent.ACTION_DOWN){
            // User started pressing a key
            if (id == R.id.keypad_left) {
                horizontalFactor -= 1;
            } else if(id == R.id.keypad_right) {
                horizontalFactor += 1;
            }
            if (id == R.id.keypad_laser) {
                pressingLaser = true;
            }
            if (id == R.id.keypad_boost) {
                pressingBoost = true;
            }
            if (id == R.id.keypad_teleport) {
                pressingTeleport = true;
            }
        } else if(action == MotionEvent.ACTION_UP) {
            // User released a key
            if (id == R.id.keypad_left) {
                horizontalFactor += 1;
            } else if (id == R.id.keypad_right) {
                horizontalFactor -= 1;
            }
            if (id == R.id.keypad_laser) {
                pressingLaser = false;
            }
            if (id == R.id.keypad_boost) {
                pressingBoost = false;
            }
            if (id == R.id.keypad_teleport) {
                pressingTeleport = false;
            }
        }
        return false;
    }
}