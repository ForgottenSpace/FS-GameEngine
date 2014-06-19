package com.ractoc.fs.components.ai;

import com.jme3.asset.AssetManager;
import com.ractoc.fs.ai.AiComponent;
import com.ractoc.fs.ai.AiScript;
import com.ractoc.fs.es.Entity;
import java.util.ArrayList;
import java.util.List;

public class SubScriptComponent extends AiComponent {

    private List<AiScript> scripts = new ArrayList<>();

    public SubScriptComponent(String id) {
        super(id);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{"scripts"};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{"exit"};
    }

    @Override
    public void initialise(Entity entity, AssetManager assetManager, AiScript aiScript) {
        super.initialise(entity, assetManager, aiScript);

        String scriptNames = (String) getProp("scripts");
        String[] scriptNameList = scriptNames.split(",");
        for (String scriptName : scriptNameList) {
            AiScript script = (AiScript) assetManager.loadAsset(scriptName.trim());
            script.initialise(entity, assetManager);
            script.setSubScript(true);
            script.getGlobalProps().putAll(aiScript.getGlobalProps());
            scripts.add(script);
        }

    }

    @Override
    public void update(float tpf) {
        boolean stillRunning = true;
        for (AiScript script : scripts) {
            script.update(tpf);
            stillRunning = !script.isFinished();
        }
        if (!stillRunning) {
            aiScript.setCurrentComponent((String) exits.get("exit"));
        }
    }
}
