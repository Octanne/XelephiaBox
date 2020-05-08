package eu.octanne.xelephia.airdrop;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.util.AnvilGUI;
import eu.octanne.xelephia.util.AnvilGUI.AnvilCloseEvent;
import eu.octanne.xelephia.util.AnvilGUI.AnvilSlot;
import eu.octanne.xelephia.util.Utils;

public class AirDropManager implements Listener {
	
	static public String CMD_TAG = "§fAirDrop §8| ";
	
	protected ArrayList<DropPoint> dropPoints = new ArrayList<>();

	private HashMap<String,PointEditor> pointEditors = new HashMap<>();
	private ArrayList<String> pointsInEditor = new ArrayList<>();

	private class PointEditor {
		public DropPoint point;
		public Inventory inv;
		public int scroll;
		public boolean willClose = false;
		public boolean inAnvil = false;

		public PointEditor(DropPoint point, Inventory inv) {
			this.point = point;
			this.inv = inv;
			scroll = 0;
		}

		public int getScrollMax() {
			int scrollMax = 0;
			scrollMax = (point.getLoots().size()-9);
			if (scrollMax <= 0) scrollMax = 0;
			return scrollMax;
		}
	}

	protected File pointsFolder = new File("plugins/Xelephia/airdrop/");

	public AirDropManager() {
		// Serialization
		ConfigurationSerialization.registerClass(Loot.class, "Loot");
		
		// LootZone load
		load();

		// Register Listener
		Bukkit.getPluginManager().registerEvents(this, XelephiaPlugin.getInstance());
	}

	protected void load() {
		if(pointsFolder.isDirectory() && pointsFolder.listFiles() != null) {
			FilenameFilter filter = (dir, name) -> name.endsWith(".yml");
			for(File file : pointsFolder.listFiles(filter)) {
				if(file.getName().contains(".yml")) {
					DropPoint airdrop = new DropPoint(file.getName().split(".yml")[0]);
					if(airdrop.getName() != null)dropPoints.add(airdrop);
				}
			}
		}
	}

	protected void save() {
		for(DropPoint point : dropPoints) {
			point.save();
		}
	}

	public List<DropPoint> getDropPoints() {
		return dropPoints;
	}

	public DropPoint getDropPoint(String name) {
		for (DropPoint point : dropPoints) {
			if (point.getName().equalsIgnoreCase(name))
				return point;
		}
		return null;
	}

	public boolean hasDropPoint(String name) {
		if (getDropPoint(name) != null)
			return true;
		else
			return false;
	}

	public boolean createPoint(String name, int respawnTime, Location loc) {
		if(!hasDropPoint(name)) {
			DropPoint point = new DropPoint(name, loc, respawnTime);
			dropPoints.add(point);
			return true;
		}else return false;
	}

	public boolean editDropPoint(Player p, String name) {
		if(hasDropPoint(name)) {
			if(pointsInEditor.contains(name)) {
				if(pointEditors.containsKey(p.getName())) p.openInventory(pointEditors.get(p.getName()).inv);
				else p.sendMessage(CMD_TAG+"§cErreur : Le point "+name+" est déjà en édition.");
				return true;
			}else {
				DropPoint point = getDropPoint(name);
				Inventory inv = createOrUpdateEditMenu(point, 0, null);
				pointsInEditor.add(name);
				pointEditors.put(p.getName(), new PointEditor(point, inv));
				p.openInventory(inv);
				return true;
			}
		}else return false;
	}

	public void reOpenEditor(Player p) {
		if(pointEditors.containsKey(p.getName())) {
			PointEditor editor = pointEditors.get(p.getName());
			createOrUpdateEditMenu(editor.point, editor.scroll, editor.inv);
			p.openInventory(editor.inv);
		}
	}

	public boolean removeDropPoint(String name) {
		if(hasDropPoint(name)) {
			DropPoint point = getDropPoint(name);
			point.remove();
			dropPoints.remove(point);
			return true;
		}else return false;
	}

	@EventHandler
	public void onInMenu(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(e.getInventory() != null && pointEditors.isEmpty() ? false : pointEditors.containsKey(p.getName())) {
				PointEditor editor = pointEditors.get(p.getName());
				if(editor.inv.equals(e.getView().getTopInventory())) {
					if(e.getRawSlot() <= 26) {
						if(!(e.getRawSlot() < 9 || e.getRawSlot() > 17)) {
							// Delete Loot, Edit % or Edit max
							if(e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
								e.setCancelled(true);
								int lootNb = e.getSlot() - 9 + editor.scroll;
								Loot eLoot = editor.point.getLoots().get(lootNb);
								// Delete
								if(e.isShiftClick()) {
									editor.point.getLoots().remove(eLoot);
									editor.point.save();

									if(editor.scroll > editor.getScrollMax()) 
										editor.scroll = editor.getScrollMax();

									createOrUpdateEditMenu(editor.point, editor.scroll, editor.inv);
								}
								// Edit QTE Max
								else if(e.getClick().equals(ClickType.DOUBLE_CLICK)){
									AnvilGUI menu = new AnvilGUI(p, new AnvilGUI.AnvilEventHandler() {

										Loot loot = eLoot;

										@Override
										public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
											event.setWillClose(true);
											event.setWillDestroy(true);
											if(event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
												int quantity;
												try {
													quantity = Integer.parseInt(event.getName());
												}catch(NumberFormatException exp) {
													event.getPlayer().sendMessage(CMD_TAG+"§cErreur : La valeur §9" + event.getName() + " §cest invalide.");
													return;
												}
												loot.max = quantity <= 64 ? quantity : 64;
												loot.max = quantity < loot.item.getAmount() ? loot.item.getAmount() : quantity;
												p.sendMessage(CMD_TAG+"§aLa quantité max est défini à §9" + loot.max + "§.");
											}
										}

										@Override
										public void onAnvilClose(AnvilCloseEvent event) {
											Bukkit.getScheduler().scheduleSyncDelayedTask(XelephiaPlugin.getInstance(), new Runnable() {
												@Override
												public void run() {
													reOpenEditor(p);
												}
											}, 8);
											editor.inAnvil = false;
										}
									});
									ArrayList<String> lore = new ArrayList<String>();
									lore.add("§aModifier quantité max :");
									lore.add("§7Entrer la valeur voulue,");
									lore.add("§e> §7Renommer l'item pour valider.");
									ItemStack anvilItem = Utils.createItemStack("Entrée valeur...", Material.PAPER, 1, lore, 0, true);
									menu.setSlot(AnvilSlot.INPUT_LEFT, anvilItem);
									try {
										editor.inAnvil = true;
										menu.open();
									} catch (IllegalAccessException | InvocationTargetException | InstantiationException exp) {
										exp.printStackTrace();
									}
								}
								else if(e.getClick().equals(ClickType.MIDDLE)) {
									AnvilGUI menu = new AnvilGUI(p, new AnvilGUI.AnvilEventHandler() {

										Loot loot = eLoot;

										@Override
										public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
											event.setWillClose(true);
											event.setWillDestroy(true);
											if(event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
												double prct;
												try {
													prct = Double.parseDouble(event.getName());
												}catch(NumberFormatException exp) {
													event.getPlayer().sendMessage(CMD_TAG+"§cErreur : La valeur §9" + event.getName() + " §cest invalide.");
													return;
												}
												loot.luckPrct = prct <= 100 ? prct : 100;
												loot.luckPrct = prct < 0 ? 0 : prct;
												p.sendMessage(CMD_TAG+"§aLe pourcentage est défini sur §9" + loot.luckPrct + "§a.");
											}
										}

										@Override
										public void onAnvilClose(AnvilCloseEvent event) {
											Bukkit.getScheduler().scheduleSyncDelayedTask(XelephiaPlugin.getInstance(), new Runnable() {
												@Override
												public void run() {
													reOpenEditor(p);
												}
											}, 8);
											editor.inAnvil = false;
										}
									});
									ArrayList<String> lore = new ArrayList<String>();
									lore.add("§aModifier pourcentage :");
									lore.add("§7Entrer la valeur voulue,");
									lore.add("§e> §7Renommer l'item pour valider.");
									ItemStack anvilItem = Utils.createItemStack("Entrée valeur...", Material.PAPER, 1, lore, 0, true);
									menu.setSlot(AnvilSlot.INPUT_LEFT, anvilItem);
									try {
										editor.inAnvil = true;
										menu.open();
									} catch (IllegalAccessException | InvocationTargetException | InstantiationException exp) {
										exp.printStackTrace();
									}
								}
							}else e.setCancelled(true);
						}else{
							e.setCancelled(true);
							// Scroll
							if(e.getSlot() == 24 && editor.scroll < editor.getScrollMax()) {
								createOrUpdateEditMenu(editor.point, editor.scroll+1, editor.inv);
								editor.scroll+=1;
							}else if(e.getSlot() == 20 && editor.scroll > 0) {
								createOrUpdateEditMenu(editor.point, editor.scroll-1, editor.inv);
								editor.scroll-=1;
							}else if(e.getSlot() == 26) {
								editor.willClose = true;
								p.closeInventory();
							}
							// Point PARA
							else if(e.getSlot() == 4) {
								//RESPAWN TIME
								if(e.getClick().equals(ClickType.DOUBLE_CLICK)) {
									AnvilGUI menu = new AnvilGUI(p, new AnvilGUI.AnvilEventHandler() {

										DropPoint point = editor.point;
										
										@Override
										public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
											event.setWillClose(true);
											event.setWillDestroy(true);
											if(event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
												int time;
												try {
													time = Integer.parseInt(event.getName());
												}catch(NumberFormatException exp) {
													event.getPlayer().sendMessage(CMD_TAG+"§cErreur : La valeur §9" + event.getName() + " §cest invalide.");
													return;
												}
												point.respawnTime = time < 0 ? 5 : time;
												p.sendMessage(CMD_TAG+"§aLe temps de respawn est défini sur §9" + point.respawnTime + " sec(s)§a.");
											}
										}

										@Override
										public void onAnvilClose(AnvilCloseEvent event) {
											Bukkit.getScheduler().scheduleSyncDelayedTask(XelephiaPlugin.getInstance(), new Runnable() {
												@Override
												public void run() {
													reOpenEditor(p);
												}
											}, 8);
											editor.inAnvil = false;
										}
									});
									ArrayList<String> lore = new ArrayList<String>();
									lore.add("§aModifier temps de respawn :");
									lore.add("§7Entrer la valeur voulue,");
									lore.add("§e> §7Renommer l'item pour valider.");
									ItemStack anvilItem = Utils.createItemStack("Entrée valeur...", Material.PAPER, 1, lore, 0, true);
									menu.setSlot(AnvilSlot.INPUT_LEFT, anvilItem);
									try {
										editor.inAnvil = true;
										menu.open();
									} catch (IllegalAccessException | InvocationTargetException | InstantiationException exp) {
										exp.printStackTrace();
									}
								}
								// MIN PLAYER
								if(e.getClick().equals(ClickType.MIDDLE)) {
									AnvilGUI menu = new AnvilGUI(p, new AnvilGUI.AnvilEventHandler() {

										DropPoint point = editor.point;
										
										@Override
										public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
											event.setWillClose(true);
											event.setWillDestroy(true);
											if(event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
												int number;
												try {
													number = Integer.parseInt(event.getName());
												}catch(NumberFormatException exp) {
													event.getPlayer().sendMessage(CMD_TAG+"§cErreur : La valeur §9" + event.getName() + " §cest invalide.");
													return;
												}
												point.minPlayers = number < 0 ? 1 : number;
												p.sendMessage(CMD_TAG+"§aLe minimun de joueurs est défini sur §9" + point.minPlayers + " joueurs§a.");
											}
										}

										@Override
										public void onAnvilClose(AnvilCloseEvent event) {
											Bukkit.getScheduler().scheduleSyncDelayedTask(XelephiaPlugin.getInstance(), new Runnable() {
												@Override
												public void run() {
													reOpenEditor(p);
												}
											}, 8);
											editor.inAnvil = false;
										}
									});
									ArrayList<String> lore = new ArrayList<String>();
									lore.add("§aModifier le minimum de joueurs :");
									lore.add("§7Entrer la valeur voulue,");
									lore.add("§e> §7Renommer l'item pour valider.");
									ItemStack anvilItem = Utils.createItemStack("Entrée valeur...", Material.PAPER, 1, lore, 0, true);
									menu.setSlot(AnvilSlot.INPUT_LEFT, anvilItem);
									try {
										editor.inAnvil = true;
										menu.open();
									} catch (IllegalAccessException | InvocationTargetException | InstantiationException exp) {
										exp.printStackTrace();
									}
								}
							}
						}
					}
					//ADD LOOT
					else if(e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && e.getView().getItem(17) != null){
						ItemStack newItem = e.getCurrentItem().clone();
						e.getClickedInventory().clear(e.getSlot());
						editor.point.addLoot(new Loot(newItem, 1, newItem.getAmount()));
						editor.point.save();
						createOrUpdateEditMenu(editor.point, editor.scroll, editor.inv);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(pointEditors.isEmpty() ? false : pointEditors.containsKey(p.getName())) {
			PointEditor editor = pointEditors.get(p.getName());
			editor.point.save();
			pointsInEditor.remove(editor.point.getName());
			pointEditors.remove(p.getName());
		}
	}

	@EventHandler
	public void onCloseMenu(InventoryCloseEvent e) {
		if(e.getPlayer() instanceof Player) {
			Player p = (Player) e.getPlayer();
			if(e.getInventory() != null && pointEditors.isEmpty() ? false : pointEditors.containsKey(p.getName())) {
				PointEditor editor = pointEditors.get(p.getName());
				editor.point.save();
				if(editor.willClose) {
					pointsInEditor.remove(editor.point.getName());
					pointEditors.remove(p.getName());
				}else if(!editor.inAnvil){
					Bukkit.getScheduler().scheduleSyncDelayedTask(XelephiaPlugin.getInstance(), new Runnable() {
						@Override
						public void run() {
							reOpenEditor(p);
						}
					}, 8);
				}
			}
		}
	}

	private Inventory createOrUpdateEditMenu(DropPoint point, int scroll, @Nullable Inventory INV) {

		Inventory inv;
		boolean isSet;

		if(INV == null) {
			inv = Bukkit.createInventory(null, 27, CMD_TAG+"§9"+point.getName());
			isSet = false;
		}
		else {
			inv = INV;
			isSet = true;
		}

		if(!isSet) {
			for(int i = 0; i < 9; i++) inv.setItem(i, Utils.createItemStack(" ", Material.STAINED_GLASS_PANE, 1, new ArrayList<String>(), 7, false));
			for(int i = 18; i < 27; i++) inv.setItem(i, Utils.createItemStack(" ", Material.STAINED_GLASS_PANE, 1, new ArrayList<String>(), 7, false));

			ItemStack rollRightItem = Utils.createItemSkull("§9Défiler (droite)", new ArrayList<String>(), SkullType.PLAYER, "MHF_ArrowRight", false);
			ItemStack rollLeftItem = Utils.createItemSkull("§9Défiler (gauche)", new ArrayList<String>(), SkullType.PLAYER, "MHF_ArrowLeft", false); 
			ItemStack closeItem = Utils.createItemStack("§cFermer & Sauver", Material.BARRIER, 1, new ArrayList<String>(), 0, false);
			ArrayList<String> tutoLore = new ArrayList<>();
			tutoLore.add("§aActions disponibles :");
			tutoLore.add("§cShift-Click §7pour supprimer le loot");
			tutoLore.add("§cMiddle-Click §7pour editer le pourcentage");
			tutoLore.add("§cDouble-Click §7pour editer la quantité max");
			ItemStack tutorialItem = Utils.createItemSkull("§7Tutoriel", tutoLore, SkullType.PLAYER, "MHF_Question", false);

			inv.setItem(20, rollLeftItem);
			inv.setItem(22, tutorialItem);
			inv.setItem(24, rollRightItem);
			inv.setItem(26, closeItem);
		}

		// Info
		ArrayList<String> infoLore = new ArrayList<>();
		infoLore.add("§7Nombre de loot : §c"+point.getLoots().size());
		infoLore.add("§7Nombre de joueurs min. : §c"+point.getMinPlayers());
		infoLore.add("§7Temps de respawn : §c"+point.respawnTime+" §7secs");
		infoLore.add("§7Location : (§c"+point.pos.getBlockX()+"§7, §c"+point.pos.getBlockY()+"§7,"
				+ " §c"+point.pos.getBlockZ()+"§7)");
		infoLore.add("");
		infoLore.add("§cDouble-Click §7pour editer le temps de respawn");
		infoLore.add("§cMiddle-Click §7pour editer le minimum de joueurs");
		ItemStack infoItem = Utils.createItemStack("§aInformations", Material.BOOK, 1, infoLore, 0, false);
		inv.setItem(4, infoItem);

		// LootItems
		//Bukkit.broadcastMessage("=====INSERT ITEMS=====");
		for(int i = 0; i < 9; i++) {
			Loot loot;
			if(!point.getLoots().isEmpty() && i+scroll < point.getLoots().size()) {
				loot = point.getLoots().get(i+scroll);
				//Bukkit.broadcastMessage("§7Shown item => slot : " + (int)(i+9) + " index : " + (int)(i+scroll) + " scroll : " + scroll);
				ItemStack item = loot.getItem().clone();
				ItemMeta meta = item.getItemMeta();
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("§7Pourcentage : §c"+loot.getLuckPrct());
				lore.add("§7Quantité max : §c"+loot.getMax());
				meta.setLore(lore);
				item.setItemMeta(meta);
				inv.setItem(i+9, item);
			}
			else {
				inv.clear(i+9);
				//Bukkit.broadcastMessage("§7Clear item => slot : " + (int)(i+9) + " index : " + (int)(i+scroll) + " scroll : " + scroll);
			}
		}
		return inv;
	}
}
