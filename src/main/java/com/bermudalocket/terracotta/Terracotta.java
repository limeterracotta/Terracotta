package com.bermudalocket.terracotta;

import com.bermudalocket.terracotta.command.ButcherCommand;
import com.bermudalocket.terracotta.command.EntityCountCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Terracotta extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("terrabutcher").setExecutor(ButcherCommand.INSTANCE);
        this.getCommand("terracounter").setExecutor(EntityCountCommand.INSTANCE);
    }

    public static int getViewDistance() {
        return Bukkit.getServer().getViewDistance() * 16;
    }

}
