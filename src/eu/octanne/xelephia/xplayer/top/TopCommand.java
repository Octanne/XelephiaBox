package eu.octanne.xelephia.xplayer.top;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.xplayer.top.TopManager.TopType;

public class TopCommand implements CommandExecutor {

	private String COMMAND_TAG = "§6Top §8|§r ";
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			if(sender.hasPermission("xelephia.commands.createtop")) {
				if(args.length >= 3) {
					if(args[1].equals("DEATH") || args[1].equals("KILL") || args[1].equals("HIGHSTREAK") ||
							args[1].equals("COINS") ) {
						int nbEntry;
						try{
							nbEntry = Integer.parseInt(args[2]);
						}catch(NumberFormatException e) {
							sender.sendMessage(COMMAND_TAG+"§cErreur : §9"+args[2]+" §cn'est pas un nombre.");
							return false;
						}
						XelephiaPlugin.getTopManager().createTop(TopType.valueOf(args[1]), ((Player) sender).getLocation(), nbEntry, args[0]);
						sender.sendMessage(COMMAND_TAG+"§aCréation d'un top §9"+args[1]+" §aavec succès.");
						return true;
					}else {
						sender.sendMessage(COMMAND_TAG+"§cErreur : §9"+args[1]+" §cn'est pas un type correct (DEATH, KILL, KILLSTREAK, COINS)");
						return false;
					}
				}else {
					sender.sendMessage(COMMAND_TAG+"§cUsage : /createtop <name> <type> <nbEntry>");
					return false;
				}
			}else {
				sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("noPermission"));
				return false;
			}
		}else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("playerOnly"));
			return false;
		}
	}
}
