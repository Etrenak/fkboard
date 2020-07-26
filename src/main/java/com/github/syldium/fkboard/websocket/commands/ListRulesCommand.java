package com.github.syldium.fkboard.websocket.commands;

import com.github.syldium.fkboard.websocket.FkWebSocket;
import com.github.syldium.fkboard.websocket.responses.RulesList;
import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.FkPI;

class ListRulesCommand extends WSCommand {

    ListRulesCommand() {
        super("list rules", false);
    }

    @Override
    public boolean execute(Fk plugin, FkPI fkpi, FkWebSocket webSocket, JsonObject json) {
        webSocket.send(new RulesList(fkpi.getRulesManager().getRulesList()).toJSON());
        return true;
    }
}
