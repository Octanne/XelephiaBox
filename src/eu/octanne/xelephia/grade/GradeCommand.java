package eu.octanne.xelephia.grade;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.octanne.xelephia.XelephiaPlugin;

public class GradeCommand implements CommandExecutor{

	private String COMMAND_TAG = "§9Grade §8|§r ";
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission("xelephia.commands.grade")) {
			if(args.length > 1) {
				if(XelephiaPlugin.getXPlayer(args[0]) != null) {
					if(XelephiaPlugin.getGradeManager().getGradeWithNull(args[1]) != null) {
						Grade grade = XelephiaPlugin.getGradeManager().getGrade(args[1]);
						sender.sendMessage(COMMAND_TAG+"§aLe joueur §9"+args[0]+" §aa desormais le grade §9"+grade.getName()+"§a.");
						XelephiaPlugin.getXPlayer(args[0]).setGrade(grade);
						return true;
					}else {
						sender.sendMessage(COMMAND_TAG+"§cErreur : Le grade §9"+args[1]+" §cest incorrect.");
						return true;
					}
				}else {
					sender.sendMessage(COMMAND_TAG+"§cErreur : Le joueur §9"+args[0]+" §cest incorrect.");
					return true;
				}
			}else {
				sender.sendMessage(COMMAND_TAG+"Usage : /grade <joueur> <grade>");
				return false;
			}
		}else{
			sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("noPermission"));
			return false;
		}
	}

}