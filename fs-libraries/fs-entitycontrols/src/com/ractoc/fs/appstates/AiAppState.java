package com.ractoc.fs.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.ractoc.fs.ai.AiScript;
import com.ractoc.fs.components.es.AiComponent;
import com.ractoc.fs.components.es.LocationComponent;
import com.ractoc.fs.es.ComponentTypeCriteria;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.es.EntityResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiAppState extends AbstractAppState {

    private EntityResultSet aiResultSet;
    private Map<Entity, AiScript> aiEntities = new HashMap<Entity, AiScript>();
    private SimpleApplication app;

    public AiAppState() {
        queryAiResultSet();
    }

    private void queryAiResultSet() {
        ComponentTypeCriteria criteria = new ComponentTypeCriteria(LocationComponent.class, AiComponent.class);
        aiResultSet = Entities.getInstance().queryEntities(criteria);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
        setEnabled(true);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
        setEnabled(false);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        determineAiEntities();

        for (AiScript aiScript : aiEntities.values()) {
            aiScript.update(tpf);
        }
    }

    private void determineAiEntities() {
        EntityResultSet.UpdateProcessor updateProcessor = aiResultSet.getUpdateProcessor();
        updateRemovedEntities(updateProcessor.getRemovedEntities());
        updateAddedEntities(updateProcessor.getAddedEntities());
        updateProcessor.finalizeUpdates();
    }

    private void updateRemovedEntities(List<Entity> removedEntities) {
        for (Entity entity : removedEntities) {
            aiEntities.remove(entity);
        }
    }

    private void updateAddedEntities(List<Entity> addedEntities) {
        for (Entity entity : addedEntities) {
            AiComponent aiComponent = Entities.getInstance().loadComponentForEntity(entity, AiComponent.class);
            AiScript aiScript = (AiScript) app.getAssetManager().loadAsset(aiComponent.getScript());
            aiScript.initialise(entity, app.getAssetManager());
            aiEntities.put(entity, aiScript);
        }
    }
}
