package com.github.syldium.fkboard;

import java.util.Optional;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.syldium.fkboard.listeners.FallenKingdomListener;
import com.github.syldium.fkboard.listeners.JoinLeftListener;
import com.github.syldium.fkboard.status.PlayerStatus;
import com.github.syldium.fkboard.websocket.FkWebSocket;

public final class FkBoard extends JavaPlugin {

    private FkWebSocket fkWebSocket;
    private PlayerStatus playerStatus;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new JoinLeftListener(this), this);
        getServer().getPluginManager().registerEvents(new FallenKingdomListener(this), this);
        
        getCommand("fkboard").setExecutor(new com.github.syldium.fkboard.commands.FkBoard(this));
    }

    @Override
    public void onDisable()
    {
        getOptionalFkWebSocket().ifPresent(fkws -> fkws.close());
    }
    
    public Optional<FkWebSocket> getOptionalFkWebSocket()
    {
        return fkWebSocket != null ? Optional.of(fkWebSocket) : Optional.empty();
    }
    
    public void setFkWebSocket(FkWebSocket fkWebSocket)
    {
        getOptionalFkWebSocket().ifPresent(fkws -> fkws.close());
        this.fkWebSocket = fkWebSocket;
    }
    
    public PlayerStatus getPlayerStatus()
    {
        return playerStatus;
    }
}
