package hikko.betterchat.commands;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import hikko.betterchat.BetterChat;
import hikko.betterchat.playerhistory.ChatController;
import hikko.betterchat.playerhistory.ChatMessage;
import hikko.betterchat.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelMessage extends AbstractCommand {

    public DelMessage() {
        super("dmsg");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("betterchat.detelemessage")) {
            if (args.length == 0) {
                sender.sendMessage("/dmsg [id]");
            }
            if (args.length > 0) {
                try {
                    int id = Integer.parseInt(args[0]);
                    if (id != -1) {
                        if (ChatUtils.deletedIdMessages.contains(id)) {
                            Component component = Component.text("Это сообщение уже удалено", NamedTextColor.YELLOW);
                            sender.sendMessage(component);
                            ChatController.getPlayer((Player) sender).appendMessage(-1, null, null, component, null);
                            return;
                        }

                        ChatMessage chatMessage = ChatController.getMessage(id);

                        if (chatMessage != null) {

                            // Start webhook
                            String reportedMessage = chatMessage.getSender().getName() + ": " + ChatUtils.getStringFromComponent(chatMessage.getContent());
                            String descriptionMessage = "Администратор **" + sender.getName() + "** удалил сообщение:" + "\n\n" +
                                    '`' + reportedMessage + '`';
                            WebhookEmbed.EmbedFooter embedFooter = new WebhookEmbed.EmbedFooter("Message id: " + id, "");
                            WebhookEmbed embed = new WebhookEmbedBuilder()
                                    .setColor(0xFF8E31)
                                    .setDescription(descriptionMessage)
                                    .setThumbnailUrl("https://minotar.net/avatar/"+sender.getName()+"/300.png")
                                    .setFooter(embedFooter)
                                    .build();
                            WebhookMessageBuilder builder = new WebhookMessageBuilder();
                            builder.setUsername("Admin Action");
                            builder.setAvatarUrl("https://cdn.icon-icons.com/icons2/1076/PNG/512/shield_77898.png");
                            builder.addEmbeds(embed);
                            WebhookClient webhookClient = BetterChat.getInstance().getWebhookClient().getWebhookClient();
                            if (webhookClient != null) webhookClient.send(builder.build());
                            // End webhook

                            ChatUtils.deletedIdMessages.add(id);
                            ChatController.deleteMessage(id);
                            if (ChatUtils.reportedIdMessages.contains(id)) {
                                ChatController.deleteMessage((id * -1));
                                ChatUtils.deletedIdMessages.add((id * -1));
                            }
                        } else {
                            Component component = Component.text("Сообщение не найдено", NamedTextColor.YELLOW);
                            sender.sendMessage(component);
                            ChatController.getPlayer((Player) sender).appendMessage(-1, null, null, component, null);
                        }
                        return;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("/dmsg [id]");
                    return;
                }
                sender.sendMessage("/dmsg [id]");
            }
        }
    }
}
