package eu.octanne.xelephia.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class DayCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender.hasPermission("xelephia.commands.day") && sender instanceof Player) {
			Player p = (Player) sender;
			p.getWorld().setTime(1000);
			sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("dayCommand"));
			return true;
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("noPermission"));
			return false;
		}
	}

}
