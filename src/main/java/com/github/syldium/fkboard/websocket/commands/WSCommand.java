package com.github.syldium.fkboard.websocket.commands;

import com.github.syldium.fkboard.websocket.FkWebSocket;
import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.FkPI;

public abstract class WSCommand {

    protected final String path;
    protected final boolean needScoreboardReload;
    protected final String[] requiredJsonKeys;

    WSCommand(String path, boolean needScoreboardReload, String... requiredJsonKeys) {
        this.path = path;
        this.needScoreboardReload = needScoreboardReload;
        this.requiredJsonKeys = requiredJsonKeys;
    }

    public boolean hasRequiredJsonKeys(JsonObject json) {
        for (String key : requiredJsonKeys) {
            if (!json.has(key)) {
                return false;
            }
        }
        return true;
    }

    public abstract boolean execute(Fk plugin, FkPI fkpi, FkWebSocket webSocket, JsonObject json);
}
