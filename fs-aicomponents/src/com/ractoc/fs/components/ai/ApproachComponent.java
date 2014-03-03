package com.ractoc.fs.components.ai;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.ractoc.fs.ai.AiComponent;
import com.ractoc.fs.ai.AiScript;
import com.ractoc.fs.components.es.CanMoveComponent;
import com.ractoc.fs.components.es.ControlledComponent;
import com.ractoc.fs.components.es.LocationComponent;
import com.ractoc.fs.components.es.MovementComponent;
import com.ractoc.fs.components.es.SpeedComponent;
import com.ractoc.fs.es.ComponentTypeCriteria;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.es.EntityResultSet;
import java.util.List;

public class ApproachComponent extends AiComponent {

    private EntityResultSet controlledResultSet;
    private Entity controlledEntity;
    private Float range;
    private Entity shipEntity;
    private MovementComponent movementComponent;

    public ApproachComponent(String id) {
        super(id);
        queryControlledResultSet();
    }

    private void queryControlledResultSet() {
        ComponentTypeCriteria criteria = new ComponentTypeCriteria(LocationComponent.class, ControlledComponent.class);
        controlledResultSet = Entities.getInstance().queryEntities(criteria);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{"range"};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{"arived"};
    }

    @Override
    public void initialise(Entity entity, AssetManager assetManager, AiScript aiScript) {
        super.initialise(entity, assetManager, aiScript);
        range = Float.valueOf((String) getProp("range"));
        shipEntity = (Entity) getProp("shipEntity");
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        determineControlledEntity();
        LocationComponent shipLocationComponent = Entities.getInstance().loadComponentForEntity(shipEntity, LocationComponent.class);
        if (controlledEntity != null && shipLocationComponent != null) {
            LocationComponent controlledLocationComponent = Entities.getInstance().loadComponentForEntity(controlledEntity, LocationComponent.class);
            turnTowardsControlledEntity(shipLocationComponent, controlledLocationComponent);
            moveTowardsControlledEntity(shipLocationComponent, controlledLocationComponent);
        }
    }

    private float determineTurnDirection(LocationComponent playerLocationComponent, LocationComponent controlledLocationComponent) {
        Vector3f relPosition = controlledLocationComponent.getTranslation().subtract(playerLocationComponent.getTranslation()).normalize();
        Vector3f myDirection = playerLocationComponent.getRotation().mult(Vector3f.UNIT_Z).normalize();
        Vector3f myLeftDirection = myDirection.cross(Vector3f.UNIT_Y).normalize();
        return myLeftDirection.dot(relPosition);
    }

    private void determineControlledEntity() {
        EntityResultSet.UpdateProcessor updateProcessor = controlledResultSet.getUpdateProcessor();
        updateRemovedEntities(updateProcessor.getRemovedEntities());
        updateAddedEntities(updateProcessor.getAddedEntities());
        updateProcessor.finalizeUpdates();
    }

    private void updateRemovedEntities(List<Entity> removedEntities) {
        if (removedEntities.size() > 0) {
            controlledEntity = null;
        }
    }

    private void updateAddedEntities(List<Entity> addedEntities) {
        if (addedEntities.size() > 0) {
            controlledEntity = addedEntities.get(0);
        }
    }

    private void rotateLeft() {
        Entities.getInstance().changeComponentsForEntity(shipEntity, new MovementComponent(
                movementComponent.isMoveForward(),
                movementComponent.isMoveBackwards(),
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                true,
                false));
    }

    private void rotateRight() {
        Entities.getInstance().changeComponentsForEntity(shipEntity, new MovementComponent(
                movementComponent.isMoveForward(),
                movementComponent.isMoveBackwards(),
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                false,
                true));
    }

    private void moveForward() {
        Entities.getInstance().changeComponentsForEntity(shipEntity, new MovementComponent(
                true,
                false,
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                movementComponent.isRotateLeft(),
                movementComponent.isRotateRight()));
    }

    private void stopMoving() {
        Entities.getInstance().changeComponentsForEntity(shipEntity, new MovementComponent(
                false,
                false,
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                movementComponent.isRotateLeft(),
                movementComponent.isRotateRight()));
    }

    private boolean entityHasMovementComponent() {
        return shipEntity.matches(new ComponentTypeCriteria(MovementComponent.class));
    }

    private boolean entityHasSpeedComponent() {
        return shipEntity.matches(new ComponentTypeCriteria(SpeedComponent.class));
    }

    private void loadMovementComponent() {
        if (entityHasMovementComponent()) {
            movementComponent = Entities.getInstance().loadComponentForEntity(shipEntity, MovementComponent.class);
        } else {
            setDefaultComponents();
        }
    }

    private void setDefaultComponents() {
        movementComponent = new MovementComponent(false, false, false, false, false, false);
        Entities.getInstance().addComponentsToEntity(shipEntity, movementComponent);
        Entities.getInstance().addComponentsToEntity(shipEntity, new SpeedComponent(0F, 0F, 0F));
    }

    private void stopRotating() {
        Entities.getInstance().changeComponentsForEntity(shipEntity, new MovementComponent(
                movementComponent.isMoveForward(),
                movementComponent.isMoveBackwards(),
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                false,
                false));
    }

    private void turnTowardsControlledEntity(LocationComponent playerLocationComponent, LocationComponent controlledLocationComponent) {
        loadMovementComponent();
        float turn = determineTurnDirection(playerLocationComponent, controlledLocationComponent);
        if (turn < 0) {
            rotateLeft();
        } else if (turn > 0) {
            rotateRight();
        } else {
            stopRotating();
        }
    }

    private void moveTowardsControlledEntity(LocationComponent playerLocationComponent, LocationComponent controlledLocationComponent) {
        loadMovementComponent();
        if (determineBrakingDistance(playerLocationComponent, controlledLocationComponent) > range) {
            System.out.println("forward");
            moveForward();
        } else {
            System.out.println("brake");
            stopMoving();
        }
    }

    private float determineBrakingDistance(LocationComponent shipLocationComponent, LocationComponent controlledLocationComponent) {
        float distance = shipLocationComponent.getTranslation().distance(controlledLocationComponent.getTranslation());
        float distanceTraveled = 0f;
        if (entityHasSpeedComponent()) {
            System.out.println("calculating brake distance");
            SpeedComponent controlledSpeedComponent = Entities.getInstance().loadComponentForEntity(shipEntity, SpeedComponent.class);
            CanMoveComponent controlledCanMoveComponent = Entities.getInstance().loadComponentForEntity(shipEntity, CanMoveComponent.class);
            float startingVelocity = controlledSpeedComponent.getMoveSpeed();
            System.out.println("startingVelocity = " + startingVelocity);
            float deceleration = controlledCanMoveComponent.getBrake();
            System.out.println("deceleration = " + deceleration);
            float averageVelocity = startingVelocity / 2;
            System.out.println("averageVelocity = " + averageVelocity);
            float brakingTime = startingVelocity / deceleration;
            System.out.println("brakingTime = " + brakingTime);
            distanceTraveled = averageVelocity * brakingTime;
        }
        System.out.println("distance = " + distance);
        System.out.println("distanceTraveled = " + distanceTraveled);
        return distance - distanceTraveled;
    }
}