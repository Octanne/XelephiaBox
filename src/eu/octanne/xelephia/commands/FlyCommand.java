package eu.octanne.xelephia.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class FlyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (sender.hasPermission("xelephia.commands.fly")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player senderPlayer = (Player) sender;
					if (senderPlayer.getAllowFlight()) {
						senderPlayer.setAllowFlight(false);
						senderPlayer.sendMessage(
								XelephiaPlugin.getMessageConfig().getConfig().getString("FLY_DISABLE_PLAYER_ME"));
						return true;
					} else {
						senderPlayer.setAllowFlight(true);
						senderPlayer.sendMessage(
								XelephiaPlugin.getMessageConfig().getConfig().getString("FLY_ENABLE_PLAYER_ME"));
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "Invalid usage : /fly <player>");
					return false;
				}

			} else if (args.length >= 1) {
				if (sender.hasPermission("xelephia.commands.fly.bypass")) {
					if (Bukkit.getPlayer(args[0]) != null) {
						Player p = Bukkit.getPlayer(args[0]);
						if (p.getAllowFlight()) {
							p.setAllowFlight(false);
							p.sendMessage(
									XelephiaPlugin.getMessageConfig().getConfig().getString("FLY_DISABLE_PLAYER_ME"));
							if (sender != p)
								sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("FLY_DISABLE_PLAYER_YOU").replace("{PLAYER}", p.getName()));
							return true;
						} else {
							p.setAllowFlight(true);
							p.sendMessage(
									XelephiaPlugin.getMessageConfig().getConfig().getString("FLY_ENABLE_PLAYER_ME"));
							if (sender != p)
								sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("FLY_ENABLE_PLAYER_YOU").replace("{PLAYER}", p.getName()));
							return true;
						}
					} else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("incorrectPlayer")
								.replace("{PLAYER}", args[0]));
						return false;
					}
				} else {
					return false;

				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid usage : /fly [player]");
				return false;

			}
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
			return false;
		}

	}

}
