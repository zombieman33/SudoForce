package me.zombieman.dev.sudoforce.commands;

import me.zombieman.dev.sudoforce.SudoForce;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SudoCommand  implements CommandExecutor, TabCompleter {

    private SudoForce plugin;

    public SudoCommand(SudoForce plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (!player.hasPermission("sudoforce.command.sudo")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
            return false;
        }

        if (args.length > 1) {

            String targetName = args[0];

            if (args[1].isBlank()) {
                player.sendMessage(ChatColor.RED + "Usage: /sudoforce <*, player> <command, message>");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return false;
            }

            StringBuilder sb = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }

            String message = sb.toString().trim();

            if (Bukkit.getPlayer(targetName) == null) {
                if (!args[0].equalsIgnoreCase("*")) {
                    player.sendMessage(ChatColor.RED + "%s is not online.".formatted(targetName));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return false;
                }
            }


            Player target = player.getPlayer();

            if (!targetName.equals("*")) {
                target = Bukkit.getPlayer(targetName);
            }

            if (targetName.equals("*")) {

                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                    messageOrCommand(player, onlinePlayers, message);
                }

            } else {

                messageOrCommand(player, target, message);

            }

        } else {
            player.sendMessage(ChatColor.RED + "Usage: /sudoforce <*, player> <command, message>");
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        Player player = (Player) sender;

        if (player.hasPermission("sudoforce.command.sudo")) {
            if (args.length == 1) {
                completions.add("*");
                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                    completions.add(onlinePlayers.getName());
                }
            } else if (args.length <= 2) {
                completions.add("<message>");
                completions.add("<command>");
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream().filter(s -> s.toLowerCase().startsWith(lastArg)).collect(Collectors.toList());
    }

    public void messageOrCommand(Player sender, Player player, String str) {

        if (str.startsWith("/")) {
            sender.sendMessage(ChatColor.GREEN + "You made: " + player.getName() + " run: " + ChatColor.GRAY + str);
            player.performCommand(str.replace("/", ""));
        } else {
            sender.sendMessage(ChatColor.GREEN + "You made: " + player.getName() + " say: " + ChatColor.GRAY + str);
            player.chat(str);
        }
    }

}
