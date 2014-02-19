package com.ractoc.fs.components.ai;

import com.jme3.asset.AssetManager;
import com.ractoc.fs.ai.AiComponent;
import com.ractoc.fs.ai.AiScript;
import com.ractoc.fs.components.es.LocationComponent;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.parsers.entitytemplate.EntityTemplate;

public class SpawnShipComponent extends AiComponent {

    private Entity shipEntity;

    public SpawnShipComponent(String id) {
        super(id);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{"shipTemplate"};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{"spawned"};
    }

    @Override
    public void initialise(Entity entity, AssetManager assetManager, AiScript aiScript) {
        super.initialise(entity, assetManager, aiScript);
        EntityTemplate template = (EntityTemplate) assetManager.loadAsset((String) getProp("shipTemplate"));
        shipEntity = Entities.getInstance().createEntity(template.getComponentsAsArray());
        LocationComponent lc = Entities.getInstance().loadComponentForEntity(entity, LocationComponent.class);
        Entities.getInstance().addComponentsToEntity(shipEntity, lc);
        aiScript.setGlobalProp("shipEntity", shipEntity);
        aiScript.setCurrentComponent((String) exits.get("spawned"));
    }
}
