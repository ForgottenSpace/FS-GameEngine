package com.ractoc.fs.ai;

import com.jme3.asset.AssetManager;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import java.util.HashMap;
import java.util.Map;

public class AiScript {

    private final String name;
    private String entry;
    private Entity entity;
    private final Map<String, AiComponent> components = new HashMap<>();
    private AiComponent currentComponent;
    private AssetManager assetManager;
    private Map<String, Object> globalProps = new HashMap<>();
    private boolean subScript;
    private boolean finished = false;

    public AiScript(String name) {
        this(name, false);
    }

    public AiScript(String name, boolean subScript) {
        this.name = name;
        this.subScript = subScript;
    }

    public void addComponent(AiComponent component) {
        components.put(component.getId(), component);
    }

    public AiComponent getComponent(String alias) {
        return components.get(alias);
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void initialise(Entity entity, AssetManager assetManager) {
        this.entity = entity;
        this.assetManager = assetManager;
        this.finished = false;
        setCurrentComponent(entry);
    }

    public void update(float tpf) {
        currentComponent.update(tpf);
    }

    public void setCurrentComponent(String componentName) {
        if (componentName.equalsIgnoreCase("exit")) {
            if (subScript) {
                finished = true;
            } else {
                Entities.getInstance().destroyEntity(entity);
            }
        } else {
            currentComponent = components.get(componentName);
            currentComponent.initialise(getEntity(), assetManager, this);
        }
    }

    public void setGlobalProp(String key, Object value) {
        globalProps.put(key, value);
    }

    public Object getGlobalProp(String key) {
        return globalProps.get(key);
    }

    public Map<String, Object> getGlobalProps() {
        return globalProps;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setSubScript(boolean subScript) {
        this.subScript = subScript;
    }

    public String getName() {
        return name;
    }
}
