package com.github.syldium.fkboard.websocket.responses;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

public class Identification implements Response {

    private final String id;

    public Identification(String id) {
        this.id = id;
    }
    
    @Override
    public int getStatusCode()
    {
        return 950;
    }

    @Override
    public @NotNull String toJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("code", getStatusCode());
        object.addProperty("id", id);
        return object.toString();
    }
}
