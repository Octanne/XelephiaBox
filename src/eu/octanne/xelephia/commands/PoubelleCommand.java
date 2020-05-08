package eu.octanne.xelephia.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import eu.octanne.xelephia.XelephiaPlugin;

public class PoubelleCommand implements CommandExecutor, Listener{

	private HashMap<UUID,Inventory> trashs = new HashMap<UUID,Inventory>();
	
	public PoubelleCommand() {
		Bukkit.getPluginManager().registerEvents(this, XelephiaPlugin.getInstance());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Inventory inv = Bukkit.createInventory(null, 27, "§cPoubelle");
			((Player) sender).openInventory(inv);
			return true;
		}else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("playerOnly"));
			return false;
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		trashs.remove(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		trashs.remove(e.getPlayer().getUniqueId());
	}
}
