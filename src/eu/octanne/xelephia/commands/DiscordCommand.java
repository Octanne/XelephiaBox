package eu.octanne.xelephia.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.octanne.xelephia.XelephiaPlugin;

public class DiscordCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("discordCommand"));
		return true;
	}
}
