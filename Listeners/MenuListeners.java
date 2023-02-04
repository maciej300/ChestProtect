package Plugin.Listeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import Plugin.Loader;
import Plugin.Events.PlayerAcceptEvent;
import Plugin.Menu.MenuUtils;

public class MenuListeners implements Listener{
	
	private Loader plugin;
	private MenuUtils utils;
	private Map<UUID, String> chestLocator;
	
	public MenuListeners(Loader plugin) {
		
		this.plugin = plugin;
		this.utils = new MenuUtils();
		this.chestLocator = new HashMap<>();
		
	}
	
	@EventHandler
	public void onPlayerInventoryClick(InventoryClickEvent e) {
		
		if(e.getCurrentItem() == null) return;
		
		if(e.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "Lock Menu!")) {
			
			e.setCancelled(true);
			
			if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Potwierdz")) {
					
				/*
				 * To do: Delete Event
				 */
				
				Bukkit.getPluginManager().callEvent(new PlayerAcceptEvent((Player) e.getWhoClicked(), e.getWhoClicked().getTargetBlockExact(2)));
				
			} else if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Anuluj")) {
				
				e.getWhoClicked().closeInventory();
				
			}
			
		} else if(e.getView().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Zarządzanie Skrzyniami!")) {
			
			e.setCancelled(true);
			
			if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Twoje Skrzynie")) {
				
				initializeInventory((Player) e.getWhoClicked());
				
			}
			
		} else if(e.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "Twoje Skrzynie")) {
			
			e.setCancelled(true);
			
			if(e.getCurrentItem().getType() == Material.CHEST && e.getCurrentItem().getItemMeta().hasDisplayName()) {
				
				chestLocator.put(e.getWhoClicked().getUniqueId(), ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
				
				initializeChestManagerInventory((Player) e.getWhoClicked());
				
			} 
			
		} else if(e.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "Zarządzanie Skrzynią")) {
			
			e.setCancelled(true);
			
			if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Dodaj gracza")) {
				
				initializeAddPlayerInventory((Player) e.getWhoClicked());
				
			} else if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Usuń Gracza")) {
				
				List<String> players = plugin.getConfig().getStringList(e.getWhoClicked().getName() + ".locks." + chestLocator.get(e.getWhoClicked().getUniqueId()) + ".players");
				
				initializeDeletePlayerInventory((Player) e.getWhoClicked(), players);
				
			} else if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Powrót")) {
				
				initializeInventory((Player) e.getWhoClicked());
				
			}
			
		} else if(e.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "Dodaj Gracza")) {
			
			e.setCancelled(true);
			
			if(e.getCurrentItem().getType() == Material.PLAYER_HEAD) {

				List<String> players = plugin.getConfig().getStringList(e.getWhoClicked().getName() + ".locks." + chestLocator.get(e.getWhoClicked().getUniqueId()) + ".players");
				
				if(!players.contains(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()))) {
					players.add(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
					
					plugin.getConfig().set(e.getWhoClicked().getName() + ".locks." + chestLocator.get(e.getWhoClicked().getUniqueId()) + ".players", players);
					
					chestLocator.remove(e.getWhoClicked().getUniqueId());
					
					plugin.saveConfig();
					
				}
				
				initializeInventory((Player) e.getWhoClicked());
				
			}
			
		} else if(e.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "Usuń Gracza")) {
			
			e.setCancelled(true);
			
			List<String> players = plugin.getConfig().getStringList(e.getWhoClicked().getName() + ".locks." + chestLocator.get(e.getWhoClicked().getUniqueId()) + ".players");
			
			if(e.getCurrentItem().getType() == Material.PLAYER_HEAD) {
				
				players.remove(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
				plugin.getConfig().set(e.getWhoClicked().getName() + ".locks." + chestLocator.get(e.getWhoClicked().getUniqueId()) + ".players", players);
				
				chestLocator.remove(e.getWhoClicked().getUniqueId());
				
				plugin.saveConfig();
			
				initializeInventory((Player) e.getWhoClicked());
				
			}
			
		} 
		
	}
	
	private void initializeInventory(Player player) {
		
		Inventory inventory = utils.createMenu(player, ChatColor.RED + "Twoje Skrzynie", 6 * 9, true);
		
		for(Entry<String, Location> map : checkAccessibility(player).entrySet()) {
			
			Date date = new Date(plugin.getConfig().getLong(player.getName() + ".locks." + map.getKey() + ".date"));
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			ItemStack chest = new ItemStack(Material.CHEST, 1);
			ItemMeta chestMeta = chest.getItemMeta();
			chestMeta.setDisplayName(ChatColor.GREEN + map.getKey());
			List<String> chestLore = new ArrayList<>();
			chestLore.add("X: " + map.getValue().getX());
			chestLore.add("Y: " + map.getValue().getY());
			chestLore.add("Z: " + map.getValue().getZ());
			chestLore.add("Created: " + dateFormat.format(date));
			chestMeta.setLore(chestLore);
			chest.setItemMeta(chestMeta);
			
			inventory.addItem(chest);
			
		}
		
		player.openInventory(inventory);
		
	}
	
	private void initializeChestManagerInventory(Player player) {
		
		Inventory inventory = utils.createMenu(player, ChatColor.RED + "Zarządzanie Skrzynią", 9, false);
		
		ItemStack addPlayerItem = new ItemStack(Material.TRIPWIRE_HOOK, 1);
		ItemMeta addPlayerMeta = addPlayerItem.getItemMeta();
		addPlayerMeta.setDisplayName(ChatColor.GREEN + "Dodaj gracza");
		addPlayerItem.setItemMeta(addPlayerMeta);
		
		ItemStack deleteLockItem = new ItemStack(Material.PAPER, 1);
		ItemMeta deleteLockMeta = deleteLockItem.getItemMeta();
		deleteLockMeta.setDisplayName(ChatColor.GREEN + "Usuń Gracza");
		deleteLockItem.setItemMeta(deleteLockMeta);
		
		ItemStack backItem = new ItemStack(Material.BARRIER, 1);
		ItemMeta backMeta = backItem.getItemMeta();
		backMeta.setDisplayName(ChatColor.RED + "Powrót");
		backItem.setItemMeta(backMeta);
		
		inventory.setItem(3, addPlayerItem);
		inventory.setItem(5, deleteLockItem);
		inventory.setItem(8, backItem);
		
		player.openInventory(inventory);
		
	}
	
	private void initializeAddPlayerInventory(Player player) {
		
		Inventory inventory = utils.createMenu(player, ChatColor.RED + "Dodaj Gracza", 6 * 9, true);
		
		List<String> playersList = plugin.getConfig().getStringList(player.getName() + ".locks." + chestLocator.get(player.getUniqueId()) + ".players");
		
		for(Player players : Bukkit.getOnlinePlayers()) {
			
			if(playersList.contains(players.getName()) /* || players.getName().equalsIgnoreCase(player.getName())*/) continue;
			
			ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta playerSkullMeta = (SkullMeta) playerSkull.getItemMeta();
			playerSkullMeta.setDisplayName(ChatColor.GREEN + players.getName());
			playerSkullMeta.setOwningPlayer(players);
			playerSkull.setItemMeta(playerSkullMeta);
			
			inventory.addItem(playerSkull);
			
		}
		
		player.openInventory(inventory);
		
	}
	
	private void initializeDeletePlayerInventory(Player player, List<String> players) {
		
		Inventory inventory = utils.createMenu(player, ChatColor.RED + "Usuń Gracza", 6 * 9, true);
		
		for(String toAdd : players) {
			
			Player entity = Bukkit.getPlayer(toAdd);
			
			ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta playerSkullMeta = (SkullMeta) playerSkull.getItemMeta();
			playerSkullMeta.setDisplayName(ChatColor.GREEN + entity.getName());
			playerSkullMeta.setOwningPlayer(entity);
			playerSkull.setItemMeta(playerSkullMeta);
			
			inventory.addItem(playerSkull);
			
		}
		
		player.openInventory(inventory);
		
	}
	
	public Map<String, Location> checkAccessibility(Player player) {
		
		Map<String, Location> arrays = new HashMap<>();
		
		for(String s : plugin.getConfig().getConfigurationSection(player.getName() + ".locks").getKeys(false)) {

			double x = plugin.getConfig().getDouble(player.getName() + ".locks." + s + ".x");
			double y = plugin.getConfig().getDouble(player.getName() + ".locks." + s + ".y");
			double z = plugin.getConfig().getDouble(player.getName() + ".locks." + s + ".z");	
			
			Location location = new Location(Bukkit.getWorld("world"), x, y, z);
			
			arrays.put(s, location);
			
		}
		
		return arrays;
	}
	
}
