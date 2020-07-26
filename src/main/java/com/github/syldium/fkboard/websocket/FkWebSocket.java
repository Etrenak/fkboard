package com.github.syldium.fkboard.websocket;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.github.syldium.fkboard.status.PlayerStatus;
import com.github.syldium.fkboard.websocket.commands.CommandsManager;
import com.github.syldium.fkboard.websocket.responses.Identification;
import com.github.syldium.fkboard.websocket.responses.ServerInfo;
import com.github.syldium.fkboard.websocket.responses.TeamsList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fkpi.FkPI;

public class FkWebSocket extends WebSocketClient
{
    private final Fk plugin;
    private final FkPI fkpi;
    private final PlayerStatus playerStatus = new PlayerStatus();
    private final CommandsManager commandsManager = new CommandsManager();
    private final String connectionID;
    private final Runnable invalidIdCallBack;
    private final static int CODE_BOUND = 951;
    private final static int CODE_WAITING_FOR_BIND = 952;

    public FkWebSocket(URI serverUri, String connectionID, Runnable invalidIdCallBack)
    {
        super(serverUri);
        
        this.connectionID = connectionID;
        this.plugin = Fk.getInstance();
        this.fkpi = this.plugin.getFkPI();
        this.invalidIdCallBack = invalidIdCallBack;
        connect();
    }

    @Override
    public void onOpen(ServerHandshake handshake)
    {
        send(new Identification(connectionID).toJSON());
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        plugin.getLogger().info(String.format("Websocket disconnected. (Code=%d, Reason=%s, remote=%b)", code, reason, remote));
    }
    
    @Override
    public void onMessage(ByteBuffer bytes)
    {
        plugin.getLogger().warning("Got ByteBuffer message : " + new String(bytes.array()));
        super.onMessage(bytes);
    }

    @Override
    public void onMessage(String message)
    {
        JsonElement element;
        try
        {
            element = new JsonParser().parse(message);
        }catch(JsonSyntaxException exception)
        {
            plugin.getLogger().warning("Got malformated message : " + message);
            return;
        }

        JsonObject json = element.getAsJsonObject();
        String action = json.has("action") ? json.get("action").getAsString() : "";
        
        int code = json.get("code").getAsInt();
        
        if(code == CODE_BOUND)
        {
            plugin.getLogger().info("Sucessfully bound to the fkboard app via proxy");
            send(new ServerInfo(plugin).toJSON());
            send(new TeamsList(FkPI.getInstance().getTeamManager().getTeams(), playerStatus).toJSON());
            return;
        }

        else if(code == CODE_WAITING_FOR_BIND)
        {
            plugin.getLogger().info("No fkboard is waiting for bind with this id ! Closing socket...");
            close();
            Bukkit.getScheduler().runTask(Fk.getInstance(), invalidIdCallBack);
            return;
        }

        plugin.getLogger().info("Got message: " + message);
        commandsManager.executeCommand(plugin, fkpi, this, action, json);
    }

    @Override
    public void onError(Exception ex)
    {

    }

    public void runSync(Consumer<BukkitTask> task)
    {
        plugin.getServer().getScheduler().runTaskLater(plugin, task, 1L);
    }

    public PlayerStatus getPlayerStatus()
    {
        return playerStatus;
    }
}
