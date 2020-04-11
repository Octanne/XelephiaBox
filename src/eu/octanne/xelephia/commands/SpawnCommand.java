package eu.octanne.xelephia.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class SpawnCommand implements CommandExecutor {
	int task;
	int sec = 5;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			Location spawn = (Location) XelephiaPlugin.getTeleportConfig().getConfig().get("Spawn",
					p.getWorld().getSpawnLocation());
			if (p.hasPermission("xelephia.bypass.tp")) {
				if (args.length < 1) {
					sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("spawnTeleport"));
					p.teleport(spawn);
					return true;
				} else {
					if (Bukkit.getPlayer(args[0]) != null) {
						Bukkit.getPlayer(args[0])
								.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("spawnTeleport"));
						Bukkit.getPlayer(args[0]).teleport(spawn);
						sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
								.getString("spawnPlayerTeleport").replace("{PLAYER}", args[0]));
						return true;
					} else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("incorrectPlayer")
								.replace("{PLAYER}", args[0]));
						return false;
					}
				}
			} else {
				int x = p.getLocation().getBlockX(), y = p.getLocation().getBlockY(), z = p.getLocation().getBlockZ();

				sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("spawnPreTeleport"));
				task = Bukkit.getScheduler().scheduleSyncRepeatingTask(XelephiaPlugin.getInstance(), new Runnable() {

					@Override
					public void run() {
						if (sec == 0) {
							sender.sendMessage(
									XelephiaPlugin.getMessageConfig().getConfig().getString("spawnTeleport"));
							p.teleport(spawn);
							sec = 5;
							Bukkit.getScheduler().cancelTask(task);
						} else {
							if (x != p.getLocation().getBlockX() || y != p.getLocation().getBlockY()
									|| z != p.getLocation().getBlockZ()) {
								sec = 5;
								sender.sendMessage(
										XelephiaPlugin.getMessageConfig().getConfig().getString("CancelTeleport"));
								Bukkit.getScheduler().cancelTask(task);
							} else {
								sec--;
							}
						}
					}
				}, 0, 20);
				return true;
			}
		} else {
			sender.sendMessage("§4Réservé au client...");
			return false;
		}
	}
}
