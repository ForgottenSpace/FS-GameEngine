package com.ractoc.fs.templates.editor;

import com.jme3.gde.core.assets.AssetData;
import com.ractoc.fs.parsers.entitytemplate.EntityTemplate;
import com.ractoc.fs.templates.editor.filetype.EtplDataObject;
import org.openide.cookies.*;
import org.openide.loaders.OpenSupport;
import org.openide.util.Lookup;
import org.openide.windows.CloneableTopComponent;

public class EtplOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {

    private EtplDataObject dataObject;
    private AssetData assetData;
    private EtplTopComponent tc;

    public EtplOpenSupport(EtplDataObject etpl) {
        super(etpl.getPrimaryEntry());
        dataObject = etpl;
        Lookup lookup = dataObject.getLookup();
        assetData = lookup.lookup(AssetData.class);
    }

    @Override
    public void open() {
        super.open();
        loadEntityTemplateIntoDataObject();
    }

    private void loadEntityTemplateIntoDataObject() {
        assetData.loadAsset();
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        if (hasNoTopComponent()) {
            createNewTopComponent();
        }
        return tc;
    }

    private boolean hasNoTopComponent() {
        return tc == null;
    }

    private void createNewTopComponent() {
        tc = new EtplTopComponent();
        tc.setEtplDataObject(dataObject);
    }
}
