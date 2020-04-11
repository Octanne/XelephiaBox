package eu.octanne.xelephia.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class HealCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("xelephia.commands.heal")) {
			if (args.length >= 1) {
				if (Bukkit.getPlayer(args[0]) != null
						&& Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[0]))) {
					Player pt = Bukkit.getPlayer(args[0]);
					pt.setHealth(20);
					pt.setFoodLevel(20);
					if (sender != pt)
						sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("healOtherCommand")
								.replace("{PLAYER}", pt.getName()));
					pt.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("healMeCommand"));
					return true;
				} else {
					sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("incorrectPlayer")
							.replace("{PLAYER}", args[1]));
					return false;
				}
			} else if (args.length == 0 && sender instanceof Player) {
				Player p = ((Player) sender);
				p.setHealth(20);
				p.setFoodLevel(20);
				p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("healMeCommand"));
				return true;
			} else {
				sender.sendMessage("§4Invalid usage: /heal <joueur>");
				return false;
			}
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
			return false;
		}
	}

}
