package com.ractoc.fs.components.ai;

import com.jme3.asset.AssetManager;
import com.ractoc.fs.ai.AiComponent;
import com.ractoc.fs.ai.AiScript;
import com.ractoc.fs.components.es.LocationComponent;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.parsers.ai.AiComponentProperty;
import com.ractoc.fs.parsers.entitytemplate.EntityTemplate;

public class SpawnShipComponent extends AiComponent {

    private Entity shipEntity;
    @AiComponentProperty(name = "shipTemplate", displayName = "Ship Template", type = String.class, shortDescription = "Fully Qualified name for the ship template file.")
    private String shipTemplate;

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
    public void initialiseProperties() {
        shipTemplate = (String) getProp("shipTemplate");
        EntityTemplate template = (EntityTemplate) assetManager.loadAsset(shipTemplate);
        shipEntity = Entities.getInstance().createEntity(template.getComponentsAsArray());
        LocationComponent lc = Entities.getInstance().loadComponentForEntity(entity, LocationComponent.class);
        Entities.getInstance().addComponentsToEntity(shipEntity, lc);
        aiScript.setGlobalProp("shipEntity", shipEntity.getId());
        aiScript.setCurrentComponent((String) exits.get("spawned"));
    }

    @Override
    public void updateProperties() {
        props.put("shipTemplate", shipTemplate);
    }
}
