package eu.octanne.xelephia.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class TpAllCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("xelephia.commands.tpall") && sender instanceof Player) {
			Player senderP = (Player) sender;
			String reason = "non précisé";
			if (args.length > 0) {
				reason = "";
				for (int nbr = 0; nbr < args.length; nbr++) {
					if (nbr == args.length - 1) {
						reason += args[nbr];
					} else
						reason += args[nbr] + " ";
				}
			}
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!p.hasPermission("xelephia.tpall.bypass") && p != senderP) {
					p.teleport(senderP);
					p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("tpallPlayerTeleport")
							.replace("{PLAYER}", sender.getName()).replace("{REASON}", reason));
				}
			}
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("tpallPlayerExecutor")
					.replace("{REASON}", reason));
			return true;
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
			return false;
		}
	}
}