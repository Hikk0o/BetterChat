package hikko.betterchat;

import hikko.betterchat.commands.DelMessage;
import hikko.betterchat.commands.ReportMessage;
import hikko.betterchat.events.ChatEvents;
import hikko.betterchat.utils.WebhookClient;
import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public final class BetterChat extends JavaPlugin {

    private static BetterChat instance;
    public static Logger logger;
    private PluginManager pluginManager;
    private WebhookClient webhookClient;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        logger = this.getLogger();
        pluginManager = Bukkit.getPluginManager();

        // Utils
        webhookClient = new WebhookClient();

        // Commands
        new DelMessage();
        new ReportMessage();

        // Events
        pluginManager.registerEvents(new ChatEvents(), this);

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

    public WebhookClient getWebhookClient() {
        return webhookClient;
    }
}
