package me.zombieman.dev.sudoforce;

import me.zombieman.dev.sudoforce.commands.SudoCommand;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SudoForce extends JavaPlugin {


    private SudoCommand sudoForceCmd;

    @Override
    public void onEnable() {
        // Plugin startup logic


        PluginCommand plSudoCmd = this.getCommand("sudoforce");
        this.sudoForceCmd = new SudoCommand(this);
        if (plSudoCmd != null) plSudoCmd.setExecutor(this.sudoForceCmd);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
