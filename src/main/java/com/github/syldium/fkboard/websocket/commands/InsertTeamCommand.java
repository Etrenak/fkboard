package com.github.syldium.fkboard.websocket.commands;

import com.github.syldium.fkboard.websocket.FkWebSocket;
import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.FkPI;

class InsertTeamCommand extends WSCommand {

    InsertTeamCommand() {
        super("insert team", true, "team");
    }

    @Override
    public boolean execute(Fk plugin, FkPI fkpi, FkWebSocket webSocket, JsonObject json) {
        fkpi.getTeamManager().createTeam(json.get("team").getAsString());
        return true;
    }
}
