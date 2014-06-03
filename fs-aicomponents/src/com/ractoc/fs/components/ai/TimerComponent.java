package com.ractoc.fs.components.ai;

import com.jme3.asset.AssetManager;
import com.ractoc.fs.ai.AiComponent;
import com.ractoc.fs.ai.AiScript;
import com.ractoc.fs.components.es.LocationComponent;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;

public class TimerComponent extends AiComponent {

    private float interval;
    private float expiredTime = 0;

    public TimerComponent(String id) {
        super(id);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{"interval"};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{"time"};
    }

    @Override
    public void initialise(Entity entity, AssetManager assetManager, AiScript aiScript) {
        super.initialise(entity, assetManager, aiScript);
        interval = Float.valueOf((String) getProp("interval"));
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        expiredTime += tpf;
        if (expiredTime >= interval) {
            expiredTime = 0;
            aiScript.setCurrentComponent((String) exits.get("time"));
        }
    }
}
