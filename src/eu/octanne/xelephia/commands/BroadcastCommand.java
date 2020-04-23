package eu.octanne.xelephia.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.octanne.xelephia.XelephiaPlugin;

public class BroadcastCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("xelephia.commands.broadcast")) {
			if (args.length <= 0) {
				sender.sendMessage(ChatColor.RED + "Merci de préciser un message.");
				return false;
			} else {
				String message = "";
				for (int nbr = 0; nbr < args.length; nbr++) {
					message += " " + args[nbr];
				}
				Bukkit.broadcastMessage("§e[§6Annonce§e]§b" + ChatColor.translateAlternateColorCodes('&', message));
				return true;
			}
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("noPermission"));
			return false;
		}
	}
}
