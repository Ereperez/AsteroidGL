package com.ereperez.asteroidgl.input;

public class InputManager {
    public float verticalFactor = 0.0f;
    public float horizontalFactor = 0.0f;
    public boolean pressingLaser = false;
    public boolean pressingBoost = false;
    public boolean pressingTeleport = false;

    public void onStart() {}
    public void onStop() {}
    public void onPause() {}
    public void onResume() {}
    public void update(float dt) {}

    public interface InputDeviceListener extends android.hardware.input.InputManager.InputDeviceListener {
        void onInputDeviceAdded(final int deviceId);
        void onInputDeviceRemoved(final int deviceId);
        void onInputDeviceChanged(final int deviceId);
    }
}
