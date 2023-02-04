package Plugin.Events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAcceptEvent extends Event{

    private static final HandlerList HANDLERS = new HandlerList();

    Player player;
    Block block;
    
    public PlayerAcceptEvent(Player player, Block block) {
    	
    	this.player = player;
    	this.block = block;
    	
    }
    
    public Player getPlayer() {
		return player;
	}

	public Block getBlock() {
		return block;
	}

	public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
	
}
