package me.zombieman.dev.sudoforce.commands;

import me.zombieman.dev.sudoforce.SudoForce;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SudoCommand implements CommandExecutor, TabCompleter {

    private final SudoForce plugin;

    public SudoCommand(SudoForce plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check for permission
        if (!sender.hasPermission("sudoforce.command.sudo")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
            return false;
        }

        // Validate arguments
        if (args.length < 2 || args[1].isBlank()) {
            sender.sendMessage(ChatColor.RED + "Usage: /sudoforce <*, player> <command, message>");
            if (sender instanceof Player player) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
            return false;
        }

        String targetName = args[0];
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String message = sb.toString().trim();

        if (!targetName.equals("*") && Bukkit.getPlayer(targetName) == null) {
            sender.sendMessage(ChatColor.RED + "%s is not online.".formatted(targetName));
            if (sender instanceof Player player) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
            return false;
        }

        if (targetName.equals("*")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                messageOrCommand(sender, onlinePlayer, message);
            }
        } else {
            Player target = Bukkit.getPlayer(targetName);
            if (target != null) {
                messageOrCommand(sender, target, message);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (sender.hasPermission("sudoforce.command.sudo")) {
            if (args.length == 1) {
                completions.add("*");
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    completions.add(onlinePlayer.getName());
                }
            } else if (args.length == 2) {
                completions.add("<message>");
                completions.add("<command>");
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream().filter(s -> s.toLowerCase().startsWith(lastArg)).collect(Collectors.toList());
    }

    private void messageOrCommand(CommandSender sender, Player target, String str) {
        if (str.startsWith("/")) {
            sender.sendMessage(ChatColor.GREEN + "You made: " + target.getName() + " run: " + ChatColor.GRAY + str);
            target.performCommand(str.substring(1));
        } else {
            sender.sendMessage(ChatColor.GREEN + "You made: " + target.getName() + " say: " + ChatColor.GRAY + str);
            target.chat(str);
        }
    }
}
