package com.ractoc.fs.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.ractoc.fs.components.es.*;
import com.ractoc.fs.es.ComponentTypeCriteria;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.es.EntityResultSet;
import com.ractoc.fs.parsers.entitytemplate.EntityTemplate;

public class ShootingAppState extends AbstractAppState {

    private SimpleApplication application;
    private EntityResultSet resultSet;

    public ShootingAppState() {
        queryEntityResultSet();
    }

    private void queryEntityResultSet() {
        Entities entities = Entities.getInstance();
        ComponentTypeCriteria criteria = new ComponentTypeCriteria(ShootMainComponent.class, LocationComponent.class);
        resultSet = entities.queryEntities(criteria);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        application = (SimpleApplication) app;
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
        EntityResultSet.UpdateProcessor updateProcessor = resultSet.getUpdateProcessor();
        updateProcessor.finalizeUpdates();
        for (Entity shootingEntity : resultSet) {
            updateShootingEntity(shootingEntity, tpf);
        }
        updateProcessor.finalizeUpdates();
    }

    // TODO: clean up with actual bullets and guns linked to the ships instead of hardcoded in this ugly fashion.
    private void updateShootingEntity(Entity shootingEntity, float tpf) {
        ShootMainComponent shootMainComponent = Entities.getInstance().loadComponentForEntity(shootingEntity, ShootMainComponent.class);
        if (shootMainComponent.getInterval() <= 0) {
            LocationComponent locationComponent = Entities.getInstance().loadComponentForEntity(shootingEntity, LocationComponent.class);
            LocationComponent bulletLocation = new LocationComponent(locationComponent.getTranslation(), locationComponent.getRotation(), new Vector3f(0.25f, 0.25f, 0.25f));
            EntityTemplate bulletTemplate = (EntityTemplate) application.getAssetManager().loadAsset("/Templates/Entity/BasicShipTemplate.etpl");
            Entity bulletEntity = Entities.getInstance().createEntity(bulletTemplate.getComponentsAsArray());
            Entities.getInstance().addComponentsToEntity(bulletEntity, bulletLocation, new OriginComponent(shootingEntity.getId()), new SpeedComponent(15f, 0f, 0f), new MovementComponent(true, false, false, false, false, false), new DamageComponent(150));
            Entities.getInstance().changeComponentsForEntity(shootingEntity, new ShootMainComponent(0.5f));
        } else {
            Entities.getInstance().changeComponentsForEntity(shootingEntity, new ShootMainComponent(shootMainComponent.getInterval() - tpf));
        }
    }
}
