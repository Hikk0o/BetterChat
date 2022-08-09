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
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportMessage extends AbstractCommand {


    public ReportMessage() {
        super("rmsg");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            try {
                int id = Integer.parseInt(args[0]);
                if (id != -1) {
                    Player player = (Player) sender;
                    if (ChatUtils.deletedIdMessages.contains(id)) {
                        Component component = Component.text("Это сообщение уже удалено администратором", NamedTextColor.YELLOW);
                        sender.sendMessage(component);
                        ChatController.getPlayer(player).appendMessage(-1, null, null, component, null);
                        return;
                    }
                    if (ChatUtils.reportedIdMessages.contains(id)) {
                        Component component = Component.text("На это сообщение уже пожаловались", NamedTextColor.YELLOW);
                        sender.sendMessage(component);
                        ChatController.getPlayer(player).appendMessage(-1, null, null, component, null);
                        return;
                    }
                    ChatMessage message = ChatController.getPlayer(player).getMessage(id);
                    if (message != null) {
                        ChatUtils.reportedIdMessages.add(id);

                        Component reportedMessage = Component.empty()
                                .append(ChatUtils.getDelMessageComponent(id))
                                .append(Component.text(message.getSender().getName()))
                                .append(ChatUtils.messageColonComponent)
                                .append(message.getContent());

                        Component reportInfo = Component.empty()
                                .append(Component.text("Игрок ", NamedTextColor.RED))
                                .append(Component.text(sender.getName(), NamedTextColor.YELLOW))
                                .append(Component.text(" пожаловался на сообщение:", NamedTextColor.RED));

                        for (Player playerr : BetterChat.getInstance().getServer().getOnlinePlayers()) {
                            if (playerr.hasPermission("betterchat.viewmessagereports")) {

                                playerr.sendMessage(reportInfo);
                                playerr.sendMessage(reportedMessage);
                                ChatController.getPlayer(playerr).appendMessage(-1, null, null, reportInfo, null);
                                ChatController.getPlayer(playerr).appendMessage((id * -1), ChatUtils.getDelMessageComponent(id), Component.text(message.getSender().getName()), message.getContent(), null);
                            }
                        }
                        Component component = Component.text("Вы пожаловались на сообщение игрока", NamedTextColor.YELLOW);
                        sender.sendMessage(component);
                        ChatController.getPlayer((Player) sender).appendMessage(-1, null, null, component, null);

                        BetterChat.logger.info("[Report on message ("+id+")] " + player.getName() + ": " + PlainTextComponentSerializer.plainText().serialize(message.getContent()));
//                        BetterChat.logger.info("/dmsg " + id);

                        // Start webhook
                        String descriptionMessage = "Игрок **" + sender.getName() + "** пожаловался на сообщение:" + "\n\n" +
                                '`' + message.getSender().getName() + ": " + ChatUtils.getStringFromComponent(message.getContent()) + '`';
                        WebhookEmbed.EmbedFooter embedFooter = new WebhookEmbed.EmbedFooter("Message id: " + id, "");
                        WebhookEmbed embed = new WebhookEmbedBuilder()
                                .setColor(0xFF5555)
                                .setDescription(descriptionMessage)
                                .setFooter(embedFooter)
                                .setThumbnailUrl("https://minotar.net/avatar/"+player.getName()+"/300.png")
                                .build();
                        WebhookMessageBuilder builder = new WebhookMessageBuilder();
                        builder.setUsername("Report System");
                        builder.setAvatarUrl("https://cdn.icon-icons.com/icons2/1378/PNG/512/dialogerror_92823.png");
                        builder.addEmbeds(embed);
                        WebhookClient webhookClient = BetterChat.getInstance().getWebhookClient().getWebhookClient();
                        if (webhookClient != null) webhookClient.send(builder.build());
                        // End webhook

                    } else {
                        Component component = Component.text("Сообщение не найдено", NamedTextColor.YELLOW);
                        sender.sendMessage(component);
                        ChatController.getPlayer((Player) sender).appendMessage(-1, null, null, component, null);
                    }
                }
            } catch (Exception e) {
                BetterChat.logger.warning(e.getMessage());
            }
        }
    }
}
