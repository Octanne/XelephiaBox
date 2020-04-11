package eu.octanne.xelephia.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class StatsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (args.length >= 1) {
				if (XelephiaPlugin.getXPlayer(args[0]) != null) {
					XelephiaPlugin.getXPlayer(args[0]).openStats((Player) sender);
					return true;
				} else {
					sender.sendMessage("§9Stats §8| §cLe joueur §a" + args[0] + " §cn'a pas été trouvé.");
					return false;
				}

			} else {
				XelephiaPlugin.getXPlayer(((Player) sender).getUniqueId()).openStats((Player) sender);
				return true;
			}
		} else {
			sender.sendMessage("Commande réservé au client.");
			return false;
		}
	}

}
