package com.ractoc.fs.parsers.ai;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.ractoc.fs.ai.AiScript;
import java.io.IOException;
import java.io.InputStream;

public class AiScriptLoader implements AssetLoader {

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        AiScriptParser parser = new AiScriptParser();
        InputStream is = assetInfo.openStream();
        AiScript script = parser.parse(is);
        is.close();
        return script;
    }

}
