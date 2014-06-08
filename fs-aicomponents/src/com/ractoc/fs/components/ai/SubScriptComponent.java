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
            script.getGlobalProps().putAll(aiScript.getGlobalProps());
            scripts.add(script);
        }

    }

    @Override
    public void update(float tpf) {
        boolean allFinished = true;
        for (AiScript script : scripts) {
            if (!script.isFinished()) {
                allFinished = false;
                script.update(tpf);
            }
        }
        if (allFinished) {
            aiScript.setCurrentComponent((String) exits.get("exit"));
        }
    }
}
