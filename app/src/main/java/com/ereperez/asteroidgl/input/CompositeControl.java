package com.ereperez.asteroidgl.input;

import com.ereperez.asteroidgl.Utils;

import java.util.ArrayList;

public class CompositeControl extends InputManager {
    private final ArrayList<InputManager> inputs = new ArrayList<>();
    private int count = 0;

    public CompositeControl(InputManager... mInputs) {
        for(InputManager im : mInputs){
            inputs.add(im);
        }
        count = inputs.size();
    }

    public void addInput(InputManager im){
        inputs.add(im);
        count = inputs.size();
    }

    @Override
    public void update(float dt) {
        InputManager temp;
        pressingLaser = false;
        pressingBoost = false;
        pressingTeleport = false;
        horizontalFactor = 0.0f;
        verticalFactor = 0.0f;
        for(int i = 0; i < count; i++){
            temp = inputs.get(i);
            temp.update(dt);
            pressingBoost = pressingBoost || temp.pressingBoost;
            pressingLaser = pressingLaser || temp.pressingLaser;
            pressingTeleport = pressingTeleport || temp.pressingTeleport;
            horizontalFactor += temp.horizontalFactor;
            verticalFactor += temp.verticalFactor;
        }
        horizontalFactor = Utils.clamp(horizontalFactor, -1f, 1f);
        verticalFactor = Utils.clamp(verticalFactor, -1f, 1f);
    }

    @Override
    public void onStart() {
        for(InputManager im : inputs){
            im.onStart();
        }
    }

    @Override
    public void onStop() {
        for(InputManager im : inputs){
            im.onStop();
        }
    }

    @Override
    public void onPause() {
        for(InputManager im : inputs){
            im.onPause();
        }
    }

    @Override
    public void onResume() {
        for(InputManager im : inputs){
            im.onResume();
        }
    }
}