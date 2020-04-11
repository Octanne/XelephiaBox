package eu.octanne.xelephia.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class SunCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender.hasPermission("xelephia.commands.sun") && sender instanceof Player) {
			Player p = (Player) sender;
			p.getWorld().setThundering(false);
			p.getWorld().setStorm(false);

			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("sunCommand"));
			return true;
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
			return false;
		}
	}
}
