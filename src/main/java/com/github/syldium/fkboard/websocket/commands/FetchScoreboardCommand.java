package com.github.syldium.fkboard.websocket.commands;

import com.github.syldium.fkboard.websocket.FkWebSocket;
import com.github.syldium.fkboard.websocket.responses.ScoreboardContent;
import com.google.gson.JsonObject;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.scoreboard.PlaceHolder;
import fr.devsylone.fkpi.FkPI;

class FetchScoreboardCommand extends WSCommand {

    FetchScoreboardCommand() {
        super("fetch scoreboard", false);
    }

    @Override
    public boolean execute(Fk plugin, FkPI fkpi, FkWebSocket webSocket, JsonObject json) {
        ScoreboardContent response = new ScoreboardContent(PlaceHolder.values(), Fk.getInstance().getScoreboardManager().getSidebar());
        webSocket.send(response.toJSON());
        return true;
    }
}
