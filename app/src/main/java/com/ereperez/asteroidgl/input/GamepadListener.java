package com.ereperez.asteroidgl.input;

import android.view.KeyEvent;
import android.view.MotionEvent;

public interface GamepadListener {
    boolean dispatchGenericMotionEvent(MotionEvent event);
    boolean dispatchKeyEvent(KeyEvent event);
}
