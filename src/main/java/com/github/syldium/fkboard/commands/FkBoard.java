package com.github.syldium.fkboard.commands;

import java.net.URI;
import java.net.URISyntaxException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.syldium.fkboard.websocket.FkWebSocket;

public class FkBoard implements CommandExecutor
{

    private com.github.syldium.fkboard.FkBoard plugin;

    public FkBoard(com.github.syldium.fkboard.FkBoard plugin)
    {
        super();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(args.length < 1)
        {
            sender.sendMessage("/fkboard connect <id>");
            return true;
        }

        else if(plugin.getOptionalFkWebSocket().isPresent())
        {
            sender.sendMessage("Already connected");
            return true;
        }

//       Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try
            {
                plugin.setFkWebSocket(new FkWebSocket(new URI("ws://localhost:50000/socket"), args[0], () -> sender.sendMessage("§cId invalide")));
            }catch(URISyntaxException e)
            {
                e.printStackTrace();
            }
//        });

        return true;
    }

}
