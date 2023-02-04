package Plugin.Listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Plugin.Loader;
import Plugin.Menu.MenuUtils;

public class ChestListener implements Listener{

	private Loader plugin;
	private MenuUtils utils;
	
	public ChestListener(Loader plugin) {
		
		this.plugin = plugin;
		this.utils = new MenuUtils();
		
	}
	
	@EventHandler
	public void onChestOpen(PlayerInteractEvent e) {
		
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType() == Material.CHEST) {
			
			if(!checkOwner(e.getClickedBlock().getLocation(), e.getPlayer())) {
				
				e.getPlayer().sendMessage(ChatColor.RED + "Nie masz dostępu do tej skrzyni!");
				
				e.setCancelled(true);
				
			}
			
		} else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK) && e.getClickedBlock().getType() == Material.CHEST) {
			
			if(!checkOwner(e.getClickedBlock().getLocation(), e.getPlayer())) {
				
				e.getPlayer().sendMessage(ChatColor.RED + "Nie możesz rozwalić zablokowanej skrzyni!");
				
				e.setCancelled(true);
				
			} else if(searchForLock(e.getClickedBlock().getLocation(), e.getPlayer())){
				
				initializeInventory(e.getPlayer());
				
				e.setCancelled(true);
				
			} else if(checkGuest(e.getClickedBlock().getLocation(), e.getPlayer())){
				
				e.getPlayer().sendMessage(ChatColor.RED + "Nie możesz rozwalić skrzyni jako gość!");
				
				e.setCancelled(true);
				
			}
			
		}
		
	}
	
	public boolean checkGuest(Location location, Player player) {
		
		for(String players : plugin.getConfig().getKeys(false)){
			
			List<String> playerAccepted = plugin.getConfig().getStringList(players + ".locks." + returnLockName(location, players) + ".players");
			
			if(playerAccepted.contains(player.getName()) && playerAccepted != null) return true;
			
		}
		
		return false;
	}
	
	public boolean checkOwner(Location location, Player p) {
		
		double requiredX = location.getX();
		double requiredY = location.getY();
		double requiredZ = location.getZ();
		
		for(String player : plugin.getConfig().getKeys(false)){
			
			if(player.equalsIgnoreCase(p.getName())) return true;
			
			for(String crateName : plugin.getConfig().getConfigurationSection(player + ".locks").getKeys(false)) {
				
				List<String> playerAccepted = plugin.getConfig().getStringList(player + ".locks." + crateName + ".players");
				
				if(playerAccepted.contains(p.getName()) && playerAccepted != null) return true;
				
				double x = plugin.getConfig().getDouble(player + ".locks." + crateName + ".x");
				double y = plugin.getConfig().getDouble(player + ".locks." + crateName + ".y");
				double z = plugin.getConfig().getDouble(player + ".locks." + crateName + ".z");
				
				if(requiredX == x && requiredY == y && requiredZ == z) return false;
			}
			
		}
		
		return true;
	}
	
	public boolean searchForLock(Location location, Player player) {
		
		double requiredX = location.getX();
		double requiredY = location.getY();
		double requiredZ = location.getZ();
		
		if(!plugin.getConfig().isSet(player.getName() + ".locks")) return false;
		
		for(String crateName : plugin.getConfig().getConfigurationSection(player.getName() + ".locks").getKeys(false)) {
			
			double x = plugin.getConfig().getDouble(player.getName() + ".locks." + crateName + ".x");
			double y = plugin.getConfig().getDouble(player.getName() + ".locks." + crateName + ".y");
			double z = plugin.getConfig().getDouble(player.getName() + ".locks." + crateName + ".z");
			
			if(requiredX == x && requiredY == y && requiredZ == z) return true;
			
		}
		
		return false;
	}
	
	public String returnLockName(Location location, Player player) {
		
		double requiredX = location.getX();
		double requiredY = location.getY();
		double requiredZ = location.getZ();
		
		for(String crateName : plugin.getConfig().getConfigurationSection(player.getName() + ".locks").getKeys(false)) {
			
			double x = plugin.getConfig().getDouble(player.getName() + ".locks." + crateName + ".x");
			double y = plugin.getConfig().getDouble(player.getName() + ".locks." + crateName + ".y");
			double z = plugin.getConfig().getDouble(player.getName() + ".locks." + crateName + ".z");
			
			if(requiredX == x && requiredY == y && requiredZ == z) return crateName;
			
		}
		
		return null;
	}
	
	public String returnLockName(Location location, String player) {
		
		double requiredX = location.getX();
		double requiredY = location.getY();
		double requiredZ = location.getZ();
		
		for(String crateName : plugin.getConfig().getConfigurationSection(player + ".locks").getKeys(false)) {
			
			double x = plugin.getConfig().getDouble(player + ".locks." + crateName + ".x");
			double y = plugin.getConfig().getDouble(player + ".locks." + crateName + ".y");
			double z = plugin.getConfig().getDouble(player + ".locks." + crateName + ".z");
			 
			if(requiredX == x && requiredY == y && requiredZ == z) return crateName;
			
		}
		
		return null;
	}
	
	public void initializeInventory(Player player) {
		
		Inventory inv = utils.createMenu(player, ChatColor.RED + "Lock Menu!", 9, false);
		
		ItemStack chest = new ItemStack(Material.GREEN_CONCRETE, 1);
		ItemMeta chestMeta = chest.getItemMeta();
		chestMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Potwierdz");
		chest.setItemMeta(chestMeta);
		
		ItemStack lock = new ItemStack(Material.RED_CONCRETE, 1);
		ItemMeta lockMeta = lock.getItemMeta();
		lockMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Anuluj");
		lock.setItemMeta(lockMeta);
		
		
		inv.setItem(3, chest);
		inv.setItem(5, lock);
		
		player.openInventory(inv);
		
	}
}
