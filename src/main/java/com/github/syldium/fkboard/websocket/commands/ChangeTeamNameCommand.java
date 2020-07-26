package com.github.syldium.fkboard.websocket.commands;

import com.github.syldium.fkboard.websocket.FkWebSocket;
import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.api.ITeam;

class ChangeTeamNameCommand extends WSCommand {

    ChangeTeamNameCommand() {
        super("change team name", true, "previous", "newName");
    }

    @Override
    public boolean execute(Fk plugin, FkPI fkpi, FkWebSocket webSocket, JsonObject json) {
        ITeam team_ = fkpi.getTeamManager().getTeam(json.get("previous").getAsString());
        team_.setName(json.get("newName").getAsString());
        return true;
    }
}
