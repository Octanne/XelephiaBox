package eu.octanne.xelephia.warp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class WarpCommand implements CommandExecutor {
	int task;
	int sec = 5;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length > 0) {
				if (args.length == 1) {
					if (XelephiaPlugin.getWarpManager().isExist(args[0])) {
						if (p.hasPermission("xelephia.bypass.tp")) {
							XelephiaPlugin.getWarpManager().getWarp(args[0]).teleportByPass(p);
							return true;
						} else {
							XelephiaPlugin.getWarpManager().getWarp(args[0]).teleport(p);
							return true;
						}
					} else {
						p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("inexistantWarp")
								.replace("{WARP}", args[0]));
						return false;
					}
				} else {
					if (p.hasPermission("xelephia.commands.warp.otherplayertp")) {
						if (Bukkit.getPlayer(args[1]) != null && XelephiaPlugin.getWarpManager().isExist(args[0])) {
							Player pT = Bukkit.getPlayer(args[1]);
							XelephiaPlugin.getWarpManager().getWarp(args[0]).teleportByPass(pT);
							p.sendMessage(
									XelephiaPlugin.getMessageConfig().getConfig().getString("playerWarpTp")
											.replace("{WARP}",
													XelephiaPlugin.getWarpManager().getWarp(args[0]).getItem()
															.getItemMeta().getDisplayName())
											.replace("{PLAYER}", pT.getName()));
							return true;
						}
					} else {
						XelephiaPlugin.getWarpManager().openWarps(p);
						return false;
					}
				}
			} else {
				XelephiaPlugin.getWarpManager().openWarps(p);
				return true;
			}
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("playerOnly"));
			return false;
		}
		return false;
	}
}
