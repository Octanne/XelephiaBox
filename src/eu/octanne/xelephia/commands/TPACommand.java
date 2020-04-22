package eu.octanne.xelephia.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class TPACommand implements CommandExecutor {

	int task, sec = 5;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (label.equalsIgnoreCase("tpa") && sender.hasPermission("xelephia.commands.tpa")) {
				if (args.length > 0) {
					if (Bukkit.getPlayer(args[0]) != null) {
						// PUT REQUEST IN HASHMAP
						XelephiaPlugin.requestTPA.put(Bukkit.getPlayer(args[0]), p);
						p.sendMessage("§7Votre demande de téléporation à §c" + Bukkit.getPlayer(args[0]).getName()
								+ "§7 a été envoyée.");
						// SEND REQUEST
						Bukkit.getPlayer(args[0])
								.sendMessage("§c" + p.getName() + " §7vous a envoyé une demande de téléportation");
						Bukkit.getPlayer(args[0])
								.sendMessage("§7Faites \"§c/tpyes§7\" pour l'accepter ou \"§c/tpno§7\" pour refuser");
						Bukkit.getPlayer(args[0]).sendMessage("§7La requête est valide pendant encore 120 secondes");

						// REQUEST DELETER
						Bukkit.getScheduler().scheduleSyncDelayedTask(XelephiaPlugin.getInstance(), new Runnable() {
							@Override
							public void run() {
								XelephiaPlugin.requestTPA.remove(Bukkit.getPlayer(args[0]), p);
							}

						}, 2400);
						return true;
					} else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("incorrectPlayer")
								.replace("{PLAYER}", args[0]));
						return false;
					}
				} else {
					sender.sendMessage("§4Invalid usage: /tpa <player>");
					return false;
				}
			} else if ((label.equalsIgnoreCase("tpyes") || label.equalsIgnoreCase("tpaccept"))
					&& sender.hasPermission("xelephia.commands.tpa")) {
				if (XelephiaPlugin.requestTPA.containsKey(p)) {
					Player pTarget = XelephiaPlugin.requestTPA.get(p);
					int x = pTarget.getLocation().getBlockX(), y = pTarget.getLocation().getBlockY(),
							z = pTarget.getLocation().getBlockZ();
					// DELETE REQUEST
					XelephiaPlugin.requestTPA.remove(p);
					// SEND MESSAGE
					p.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("acceptTeleportRequest")
							.replace("{PLAYER}", pTarget.getName()));
					pTarget.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("tpaPreTeleport")
							.replace("{PLAYER}", p.getName()));
					// TELEPORT
					task = Bukkit.getScheduler().scheduleSyncRepeatingTask(XelephiaPlugin.getInstance(),
							new Runnable() {

								@Override
								public void run() {
									if (sec == 0) {
										pTarget.sendMessage(XelephiaPlugin.getMessageConfig().get()
												.getString("tpaTeleport").replace("{PLAYER}", p.getName()));
										pTarget.teleport(p.getLocation());
										sec = 5;
										Bukkit.getScheduler().cancelTask(task);
									} else {
										if (x != pTarget.getLocation().getBlockX()
												|| y != pTarget.getLocation().getBlockY()
												|| z != pTarget.getLocation().getBlockZ()) {
											sec = 5;
											pTarget.sendMessage(XelephiaPlugin.getMessageConfig().get()
													.getString("CancelTeleport"));
											Bukkit.getScheduler().cancelTask(task);
										} else {
											sec--;
										}
									}
								}
							}, 0, 20);
					return true;
				} else {
					sender.sendMessage(
							XelephiaPlugin.getMessageConfig().get().getString("noTeleportRequestFound"));
					return false;
				}
			} else if ((label.equalsIgnoreCase("tpno") || label.equalsIgnoreCase("tpdenny"))
					&& sender.hasPermission("xelephia.commands.tpa")) {
				if (XelephiaPlugin.requestTPA.containsKey(p)) {
					// SEND MESSAGE
					XelephiaPlugin.requestTPA.get(p).sendMessage(XelephiaPlugin.getMessageConfig().get()
							.getString("denyYouTeleportRequest").replace("{PLAYER}", sender.getName()));
					sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("denyTeleportRequest")
							.replace("{PLAYER}", XelephiaPlugin.requestTPA.get(p).getName()));
					// DELETE REQUEST
					XelephiaPlugin.requestTPA.remove(p);
					return true;
				} else {
					sender.sendMessage(
							XelephiaPlugin.getMessageConfig().get().getString("noTeleportRequestFound"));
					return false;
				}
			} else {
				sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("noPermission"));
				return false;
			}
		} else {
			sender.sendMessage("§4Réservé au client...");
			return false;
		}
	}

}
