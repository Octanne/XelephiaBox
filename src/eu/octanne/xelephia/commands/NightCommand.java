package eu.octanne.xelephia.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class NightCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("xelephia.commands.night") && sender instanceof Player) {
			Player p = (Player) sender;
			p.getWorld().setTime(13000);
			sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("nightCommand"));
			return true;
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("noPermission"));
			return false;
		}
	}
}
