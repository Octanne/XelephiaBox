package eu.octanne.xelephia.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class SetSpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("xelephia.commands.setspawn") && sender instanceof Player) {
			Player p = (Player) sender;
			XelephiaPlugin.getTeleportConfig().set("Spawn", p.getLocation());
			XelephiaPlugin.getTeleportConfig().save();
			sender.sendMessage("Le spawn viens d'être configuré.");
			return true;
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
			return false;
		}
	}

}
