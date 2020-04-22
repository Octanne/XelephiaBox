package eu.octanne.xelephia.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.xplayer.XPlayer;

public class ResetPlayerCommand implements CommandExecutor{

	private String COMMAND_TAG = "§9Joueur §8|§r ";
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission("xelephia.resetplayer")) {
			if(args.length > 0) {
				XPlayer xP = XelephiaPlugin.getXPlayer(args[0]);
				if(xP != null) {
					xP.resetPlayer();
					sender.sendMessage(COMMAND_TAG+"§aLe joueur §9"+xP.getName()+" §aviens d'être réinitialisé.");
				}
			}else {
				sender.sendMessage(COMMAND_TAG+"§cUsage : /resetplayer <player>");
				return false;
			}
		}else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("noPermission"));
		}
		return false;
	}

}
