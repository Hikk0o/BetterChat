package hikko.betterchat.commands;

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
                if (id >= 0) {
                    Player player = (Player) sender;
                    ChatMessage message = ChatController.getPlayer(player).getMessage(id);
                    if (message != null) {
                        if (ChatUtils.reportedIdMessages.contains(id)) {
                            Component component = Component.text("На это сообщение уже пожаловались", NamedTextColor.YELLOW);
                            sender.sendMessage(component);
                            ChatController.getPlayer(player).appendMessage(-1, null, null, component);

                            return;
                        }
                        ChatUtils.reportedIdMessages.add(id);

                        Component reportedMessage = Component.empty()
                                .append(ChatUtils.getDelMessageComponent(id))
                                .append(message.getSender())
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
                                ChatController.getPlayer(playerr).appendMessage(-1, null, null, reportInfo);
                                ChatController.getPlayer(playerr).appendMessage((id * -1), ChatUtils.getDelMessageComponent(id), ChatUtils.getNicknameComponent((Player) sender), message.getContent());
                            }
                        }
                        Component component = Component.text("Вы пожаловались на сообщение игрока", NamedTextColor.YELLOW);
                        sender.sendMessage(component);
                        ChatController.getPlayer((Player) sender).appendMessage(-1, null, null, component);

                        Component consoleLog = Component.empty()
                                .append(ChatUtils.getNicknameComponent(player))
                                .append(ChatUtils.messageColonComponent)
                                .append(message.getContent());
                        BetterChat.logger.info("[Report on message] " + player.getName() + ": " + PlainTextComponentSerializer.plainText().serialize(message.getContent()));
                        BetterChat.logger.info("/dmsg " + id);
                    }
                }
            } catch (NumberFormatException e) {
                // pass
            }
        }
    }
}
