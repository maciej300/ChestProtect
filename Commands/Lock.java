package Plugin.Commands;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Plugin.Loader;

public class Lock implements CommandExecutor{
	
	private Loader plugin;
	
	public Lock(Loader plugin) {
		
		this.plugin = plugin;
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		if(sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if(args.length == 1) {
			
				Block block = p.getTargetBlockExact(2);
				
				if(block == null) return true;
				
				if(plugin.getConfig().getConfigurationSection(p.getName() + ".locks") == null) {
					
					Location blockLocation = block.getLocation();
					double x = blockLocation.getX();
					double y = blockLocation.getY();
					double z = blockLocation.getZ();
					
					if(checkOwner(blockLocation)) {
					
						plugin.getConfig().set(p.getName() + ".locks." + args[0] + ".x", x);
						plugin.getConfig().set(p.getName() + ".locks." + args[0] + ".y", y);
						plugin.getConfig().set(p.getName() + ".locks." + args[0] + ".z", z);
						
						p.sendMessage(ChatColor.GOLD + "Pomyślnie utworzyłeś zablokowaną skrzynie!");
						
						plugin.saveConfig();
					}
					return true;
				} 
				
				if(block.getType() == Material.CHEST && plugin.getConfig().getConfigurationSection(p.getName() + ".locks").getKeys(false).size() < 5) {
					
					executeCode(block, args[0], p);
					
				}
			}
		}
		
		return true;
	}
	
	public void executeCode(Block block, String name, Player player) {
		
		Location blockLocation = block.getLocation();
		double x = blockLocation.getX();
		double y = blockLocation.getY();
		double z = blockLocation.getZ();
		
		if(checkAccessibility(blockLocation, name, player) && checkOwner(blockLocation)) {
		
			plugin.getConfig().set(player.getName() + ".locks." + name + ".x", x);
			plugin.getConfig().set(player.getName() + ".locks." + name + ".y", y);
			plugin.getConfig().set(player.getName() + ".locks." + name + ".z", z);
			plugin.getConfig().set(player.getName() + ".locks." + name + ".date", System.currentTimeMillis());
			
			player.sendMessage(ChatColor.GOLD + "Pomyślnie utworzyłeś zablokowaną skrzynie!");
			
			plugin.saveConfig();
		} else {
			
			player.sendMessage(ChatColor.RED + "Taka skrzynia już istnieje!");
			
		}
		
	}
	
	public boolean checkOwner(Location location) {
		
		double requiredX = location.getX();
		double requiredY = location.getY();
		double requiredZ = location.getZ();
		
		for(String player : plugin.getConfig().getKeys(false)){
			
			for(String crateName : plugin.getConfig().getConfigurationSection(player + ".locks").getKeys(false)) {
				
				double x = plugin.getConfig().getDouble(player + ".locks." + crateName + ".x");
				double y = plugin.getConfig().getDouble(player + ".locks." + crateName + ".y");
				double z = plugin.getConfig().getDouble(player + ".locks." + crateName + ".z");
				
				if(requiredX == x && requiredY == y && requiredZ == z) return false;
			}
			
		}
		
		return true;
	}
	
	public boolean checkAccessibility(Location location, String name, Player player) {
		
		double requiredX = location.getX();
		double requiredY = location.getY();
		double requiredZ = location.getZ();
		
		for(String s : plugin.getConfig().getConfigurationSection(player.getName() + ".locks").getKeys(false)) {
			
			double x = plugin.getConfig().getDouble(player.getName() + ".locks." + s + ".x");
			double y = plugin.getConfig().getDouble(player.getName() + ".locks." + s + ".y");
			double z = plugin.getConfig().getDouble(player.getName() + ".locks." + s + ".z");
			
			if(requiredX == x && requiredY == y && requiredZ == z || s.equalsIgnoreCase(name)) return false;
			
		}
		
		return true;
	}
	
	public boolean checkForEntry(String name, Player player) {
		
		Set<String> check = plugin.getConfig().getConfigurationSection(player.getName() + ".locks").getKeys(false);
		
		return check.contains(name);
	}
	
}
