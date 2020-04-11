package eu.octanne.xelephia.warp;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class SetWarpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (sender.hasPermission("xelephia.commands.setwarp")) {
				if (args.length > 0) {
					if (!p.getInventory().getItemInHand().getType().equals(Material.AIR)) {
						XelephiaPlugin.getWarpManager().createWarp(args[0], p.getLocation(),
								p.getInventory().getItemInHand());
						sender.sendMessage("§aCreation du warp validée !");
						return true;
					} else {
						sender.sendMessage("§4Vous n'avez pas d'item en main !");
						return false;
					}
				} else {
					sender.sendMessage("§4Invalid usage: /setwarp <warpName>");
					return false;
				}
			} else {
				sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
				return false;
			}
		} else {
			sender.sendMessage("§cRéservé au client...");
			return false;
		}
	}

}
