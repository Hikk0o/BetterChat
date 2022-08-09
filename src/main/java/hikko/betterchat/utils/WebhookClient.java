package hikko.betterchat.utils;

import club.minnced.discord.webhook.WebhookClientBuilder;
import hikko.betterchat.BetterChat;
import org.bukkit.configuration.file.FileConfiguration;

public class WebhookClient {

    private club.minnced.discord.webhook.WebhookClient webhookClient = null;

    public WebhookClient() {
        FileConfiguration config = BetterChat.getInstance().getConfig();
        String url = config.getString("webhook-url");

        if (url != null && CommandUtils.isValidURL(url)) {
            BetterChat.logger.info("Webhook url is valid, creating WebhookClient.");
            WebhookClientBuilder webhookClientBuilder = new WebhookClientBuilder(url)
                    .setThreadFactory((job) -> {
                        Thread thread = new Thread(job);
                        thread.setDaemon(true);
                        return thread;
                    });
            webhookClientBuilder.setWait(true);
            webhookClient = webhookClientBuilder.build();
        } else {
            BetterChat.logger.warning("Webhook url is invalid, create WebhookClient canceled.");
        }

    }

    public club.minnced.discord.webhook.WebhookClient getWebhookClient() {
        return webhookClient;
    }

}
