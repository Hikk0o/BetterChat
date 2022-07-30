package hikko.betterchat.events;

import hikko.betterchat.BetterChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PortalEvents implements Listener {

    FileConfiguration config = BetterChat.getInstance().getConfig();

    @EventHandler
    public void Player(PlayerTeleportEvent e) {
//        BetterChat.logger.info(String.valueOf(config.getBoolean("allow-end")));
        {

        }
        if (e.getTo().getWorld().getName().equals("world_the_end") && !config.getBoolean("allow-end")) {
            e.setCancelled(true);
//            BetterChat.logger.info("Cancel teleport to world_the_end");
        }
    }
}
