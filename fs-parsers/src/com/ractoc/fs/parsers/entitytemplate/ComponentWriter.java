package com.ractoc.fs.parsers.entitytemplate;

import com.ractoc.fs.parsers.ParserException;
import com.ractoc.fs.es.EntityComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ComponentWriter {

    protected Map<String, String> properties = new HashMap<String, String>();
    protected final List<String> mandatoryProperties = new ArrayList<String>();

    public Map<String, String> getPropertiesFromComponent(EntityComponent component) {
        try {
            pullPropertiesFromComponent(component);
        } catch(ClassCastException ex) {
            throw new ParserException("Invalid component type for parser.", ex);
        }
        return properties;
    }

    protected abstract void pullPropertiesFromComponent(EntityComponent component);
}
