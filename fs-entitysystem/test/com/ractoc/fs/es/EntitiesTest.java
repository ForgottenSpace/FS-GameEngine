package com.ractoc.fs.es;

import static org.easymock.EasyMock.*;
import static junit.framework.Assert.*;
import org.easymock.Capture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EntitiesTest {

    private static Entities entities = null;
    
    private ComponentStorage mockedComponentStorage;

    public EntitiesTest() {
    }

    @BeforeClass
    public static void setupClass() {
        entities = Entities.getInstance();
    }
    
    @Before
    public void setupTest() {
        mockedComponentStorage = createMock(ComponentStorage.class);
        entities.registerComponentTypesWithComponentStorage(mockedComponentStorage, MockEntityComponent.class, SecondMockEntityComponent.class);
    }
    
    @After
    public void breakdownTest() {
        entities.unregisterComponentTypesWithComponentStorage(MockEntityComponent.class, SecondMockEntityComponent.class);
        entities.closeAllResultSets();
    }

    @Test(expected=EntityException.class)
    public void registerComponentTypesWithComponentStorageNoComponentTypes() {
        entities.registerComponentTypesWithComponentStorage(mockedComponentStorage);
    }

    @Test(expected=EntityException.class)
    public void registerComponentTypesWithComponentStorageNullComponentType() {
        entities.registerComponentTypesWithComponentStorage(mockedComponentStorage, (Class<? extends EntityComponent>) null);
    }

    @Test(expected=EntityException.class)
    public void createEntityNoComponents() {
        entities.createEntity();
    }

    @Test(expected=EntityException.class)
    public void createEntityNullComponent() {
        entities.createEntity(new MockEntityComponent(), null);
    }

    @Test
    public void createEntityNoResultSet() {
        Entity entity = createMockedEntityForComponents(setupMockedEntity());
        destroyMockedEntity(entity);
    }

    @Test
    public void createEntityWithResultSet() {
        EntityResultSet ers = entities.queryEntities(new ComponentTypeCriteria(MockEntityComponent.class));
        Entity entity = createMockedEntityForComponents(setupMockedEntity());
        EntityResultSet.UpdateProcessor updProc = ers.getUpdateProcessor();
        assertFalse(updProc.getAddedEntities().isEmpty());
        entities.closeResultSet(ers);
        destroyMockedEntity(entity);
    }

    private MockEntityComponent setupMockedEntity() {
        MockEntityComponent mockedComponent = new MockEntityComponent();
        Capture<Long> captureEntityId = new Capture();
        mockedComponentStorage.storeComponentForEntity(capture(captureEntityId), eq(mockedComponent));
        expectLastCall();
        mockedComponentStorage.removeComponentForEntity(capture(captureEntityId));
        expectLastCall();
        return mockedComponent;
    }

    private Entity createMockedEntityForComponents(MockEntityComponent... mockedComponents) {
        replay(mockedComponentStorage);
        Entity entity = entities.createEntity(mockedComponents);
        assertNotNull(entity);
        return entity;
    }

    private void destroyMockedEntity(Entity entity) {
        entities.destroyEntity(entity);
        verify(mockedComponentStorage);
    }

    @Test(expected=EntityException.class)
    public void closeResultSetAlreadyClosed() {
        EntityResultSet ers = entities.queryEntities(new ComponentTypeCriteria(MockEntityComponent.class));
        entities.closeResultSet(ers);
        entities.closeResultSet(ers);
    }

    @Test
    public void closeResultSet() {
        EntityResultSet ers = entities.queryEntities(new ComponentTypeCriteria(MockEntityComponent.class));
        EntityResultSet.UpdateProcessor updProc = ers.getUpdateProcessor();
        entities.closeResultSet(ers);
        Entity entity = createMockedEntityForComponents(setupMockedEntity());
        assertEquals(0, updProc.getAddedEntities().size());
        assertEquals(0, ers.size());
        destroyMockedEntity(entity);
        assertEquals(0, updProc.getRemovedEntities().size());
    }

    @Test
    public void destroyEntityWithResultset() {
        EntityResultSet ers = entities.queryEntities(new ComponentTypeCriteria(MockEntityComponent.class));
        Entity entity = createMockedEntityForComponents(setupMockedEntity());
        destroyMockedEntity(entity);
        EntityResultSet.UpdateProcessor updProc = ers.getUpdateProcessor();
        assertFalse(updProc.getRemovedEntities().isEmpty());
        entities.closeResultSet(ers);
    }

    @Test(expected=EntityException.class)
    public void addComponentsToEntityNoComponents() {
        Entity entity = createMockedEntityForComponents(setupMockedEntity());
        entities.addComponentsToEntity(entity);
    }

    @Test(expected=EntityException.class)
    public void addComponentsToEntityNullComponent() {
        Entity entity = createMockedEntityForComponents(setupMockedEntity());
        entities.addComponentsToEntity(entity, new SecondMockEntityComponent(), null);
    }

    @Test
    public void addComponentsToEntityNoResultSet() {
        MockEntityComponent mockEntityComponent = setupMockedEntity();
        Capture<Long> captureEntityId = new Capture();
        SecondMockEntityComponent secondMockedComponent = new SecondMockEntityComponent();
        mockedComponentStorage.storeComponentForEntity(capture(captureEntityId), eq(secondMockedComponent));
        expectLastCall();
        mockedComponentStorage.removeComponentForEntity(capture(captureEntityId));
        expectLastCall();
        Entity entity = createMockedEntityForComponents(mockEntityComponent);
        entities.addComponentsToEntity(entity, secondMockedComponent);
        destroyMockedEntity(entity);
    }

    @Test
    public void addedResultSet() {
        EntityResultSet ers = entities.queryEntities(new ComponentTypeCriteria(SecondMockEntityComponent.class));
        MockEntityComponent mockEntityComponent = setupMockedEntity();
        Capture<Long> captureEntityId = new Capture();
        SecondMockEntityComponent secondMockedComponent = new SecondMockEntityComponent();
        mockedComponentStorage.storeComponentForEntity(capture(captureEntityId), eq(secondMockedComponent));
        expectLastCall();
        mockedComponentStorage.removeComponentForEntity(capture(captureEntityId));
        expectLastCall();
        Entity entity = createMockedEntityForComponents(mockEntityComponent);
        entities.addComponentsToEntity(entity, secondMockedComponent);
        EntityResultSet.UpdateProcessor updProc = ers.getUpdateProcessor();
        assertFalse(updProc.getAddedEntities().isEmpty());
        entities.closeResultSet(ers);
        destroyMockedEntity(entity);
    }

    @Test
    public void changedResultSet() {
        EntityResultSet ers = entities.queryEntities(new ComponentTypeCriteria(MockEntityComponent.class));
        MockEntityComponent mockEntityComponent = setupMockedEntity();
        Capture<Long> captureEntityId = new Capture();
        SecondMockEntityComponent secondMockedComponent = new SecondMockEntityComponent();
        mockedComponentStorage.storeComponentForEntity(capture(captureEntityId), eq(secondMockedComponent));
        expectLastCall();
        mockedComponentStorage.removeComponentForEntity(capture(captureEntityId));
        expectLastCall();
        Entity entity = createMockedEntityForComponents(mockEntityComponent);
        entities.addComponentsToEntity(entity, secondMockedComponent);
        EntityResultSet.UpdateProcessor updProc = ers.getUpdateProcessor();
        assertFalse(updProc.getChangedEntities().isEmpty());
        entities.closeResultSet(ers);
        destroyMockedEntity(entity);
    }

    @Test
    public void removedResultSet() {
        EntityResultSet ers = entities.queryEntities(new ComponentTypeCriteria(MockEntityComponent.class));
        Entity entity = createMockedEntityForComponents(setupMockedEntity());
        destroyMockedEntity(entity);
        EntityResultSet.UpdateProcessor updProc = ers.getUpdateProcessor();
        assertFalse(updProc.getRemovedEntities().isEmpty());
        entities.closeResultSet(ers);
    }

    @Test(expected=EntityException.class)
    public void removeComponentsFromEntityNoComponents() {
        SecondMockEntityComponent secMock = new SecondMockEntityComponent();
        Entity entity = createMockedEntityForComponents(setupMockedEntity());
        entities.removeComponentsFromEntity(entity);
    }

    @Test(expected=EntityException.class)
    public void removeComponentsFromEntityNullComponent() {
        EntityResultSet ers = entities.queryEntities(new ComponentTypeCriteria(MockEntityComponent.class));
        MockEntityComponent mockEntityComponent = setupMockedEntity();
        Capture<Long> captureEntityId = new Capture();
        SecondMockEntityComponent secondMockedComponent = new SecondMockEntityComponent();
        mockedComponentStorage.storeComponentForEntity(capture(captureEntityId), eq(secondMockedComponent));
        expectLastCall();
        mockedComponentStorage.removeComponentForEntity(capture(captureEntityId));
        expectLastCall();
        Entity entity = createMockedEntityForComponents(mockEntityComponent);
        entities.addComponentsToEntity(entity, secondMockedComponent, null);
    }

    @Test
    public void removeComponentsFromEntityNoResultSet() {
        MockEntityComponent mockEntityComponent = setupMockedEntity();
        Capture<Long> captureEntityId = new Capture();
        SecondMockEntityComponent secondMockedComponent = new SecondMockEntityComponent();
        mockedComponentStorage.storeComponentForEntity(capture(captureEntityId), eq(secondMockedComponent));
        expectLastCall();
        mockedComponentStorage.removeComponentForEntity(capture(captureEntityId));
        expectLastCall();
        Entity entity = createMockedEntityForComponents(mockEntityComponent);
        entities.addComponentsToEntity(entity, secondMockedComponent);
        entities.removeComponentsFromEntity(entity, secondMockedComponent);
        destroyMockedEntity(entity);
    }

    private class SecondMockEntityComponent implements EntityComponent{

    }
}
