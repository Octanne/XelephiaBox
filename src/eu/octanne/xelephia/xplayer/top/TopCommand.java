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
				if(args.length >= 2) {
					if(args[0].equals("DEATH") || args[0].equals("KILL") || args[0].equals("KILLSTREAK") ||
							args[0].equals("COINS") ) {
						int nbEntry;
						try{
							nbEntry = Integer.parseInt(args[1]);
						}catch(NumberFormatException e) {
							sender.sendMessage(COMMAND_TAG+"§cErreur : §9"+args[1]+" §cn'est pas un nombre.");
							return false;
						}
						XelephiaPlugin.getTopManager().createTop(TopType.valueOf(args[0]), ((Player) sender).getLocation(), nbEntry);
						sender.sendMessage(COMMAND_TAG+"§aCréation d'un top §9"+args[0]+" §aavec succès.");
						return true;
					}else {
						sender.sendMessage(COMMAND_TAG+"§cErreur : §9"+args[0]+" §cn'est pas un type correct (DEATH, KILL, KILLSTREAK, COINS)");
						return false;
					}
				}else {
					sender.sendMessage(COMMAND_TAG+"§cUsage : /createtop <type> <nbEntry>");
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
