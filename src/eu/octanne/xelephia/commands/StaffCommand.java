package eu.octanne.xelephia.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class StaffCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (sender.hasPermission("xelephia.commands.staff")) {
			if (args.length > 0) {
				String message = "";
				for (int nbr = 0; nbr <= args.length - 1; nbr++) {
					if (nbr == 0) {
						message = " §r§b" + sender.getName() + ":§d ";
					}
					message = message + args[nbr];
					if (nbr != args.length - 1) {
						message = message + " ";
					}
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.hasPermission("xelephia.commands.staff")) {
						p.sendMessage("§8>§e>§1Staff§r§e>§8>§r " + message);
					}
				}
				return true;
			} else {
				sender.sendMessage("Invalid usage : /staff <message>");
				return false;
			}
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
			return false;
		}
	}

}
