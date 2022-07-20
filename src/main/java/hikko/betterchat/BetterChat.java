package hikko.betterchat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import hikko.betterchat.commands.DelMessage;
import hikko.betterchat.events.ChatEvents;
import hikko.betterchat.playerhistory.protocol.ChatPacketHandler;
import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class BetterChat extends JavaPlugin {

    private static BetterChat instance;
    private Logger logger;
    private PluginManager pluginManager;
    private static ChatEvents chatEvents;
    public static ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        instance = this;
        logger = this.getLogger();
        pluginManager = Bukkit.getPluginManager();

        // Other
        protocolManager = ProtocolLibrary.getProtocolManager();

        // Commands
        new DelMessage();

        // Events
        pluginManager.registerEvents(chatEvents = new ChatEvents(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BetterChat getInstance() {
        return instance;
    }

    public IEssentials getAPIEssentials() {
        return (IEssentials) pluginManager.getPlugin("Essentials");
    }

    public static ChatEvents getChatEvents() {
        return chatEvents;
    }
}
