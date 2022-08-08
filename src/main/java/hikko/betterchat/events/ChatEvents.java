package hikko.betterchat.events;

import com.earth2me.essentials.User;
import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import hikko.betterchat.BetterChat;
import hikko.betterchat.playerhistory.ChatController;
import hikko.betterchat.playerhistory.protocol.ChatPacketHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.ess3.api.events.PrivateMessagePreSendEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatEvents implements Listener {
    public ChatEvents() {
        BetterChat.logger.info( "Loading chat events...");
        new ChatPacketHandler();
    }
    private final Logger chatLogger = Logger.getLogger("Chat");
    private final BukkitScheduler scheduler = BetterChat.getInstance().getServer().getScheduler();
    public int messageCounter = 0;

    private boolean loginAlertIsCooldown = false;
    private boolean logoutAlertIsCooldown = false;

    @EventHandler
    public void PlayerHideEvent(PlayerHideEvent e) {
        logOutPlayerNotify(e.getPlayer(), true);
    }
    @EventHandler
    public void PlayerShowEvent(PlayerShowEvent e) {
        logInPlayerNotify(e.getPlayer(), true);
    }


    @EventHandler
    public void LoginEvent(LoginEvent e) { // AuthMe login event
        logInPlayerNotify(e.getPlayer(), false);
    }

    @EventHandler
    public void QuitEvent(PlayerQuitEvent e) {
        logOutPlayerNotify(e.getPlayer(), false);
        ChatController.removePlayer(e.getPlayer());
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        ChatController.addPlayer(player);
    }


    private void logInPlayerNotify(Player player, boolean forced) {
        for (Player playerr : Bukkit.getOnlinePlayers()) {
            if (playerr.canSee(player) || forced) {
                if (!loginAlertIsCooldown) playerr.playSound(playerr.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, (float) 0.3, 1);
                Component joinComponent = Component.text("")
//                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Поприветствовать ").color(NamedTextColor.WHITE).append(Component.text(player.getName()).color(NamedTextColor.YELLOW))))
//                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "!" + player.getName() + " qq"))
                                .append(Component.text("[+] ", NamedTextColor.GREEN))
                                .append(Component.text(player.getName(), NamedTextColor.YELLOW));
                playerr.sendMessage(joinComponent);
                ChatController.getPlayer(playerr).appendMessage(-1, null, null, joinComponent);
            }
        }
        if (!loginAlertIsCooldown) {
            loginAlertIsCooldown = true;
            scheduler.runTaskLater(BetterChat.getInstance(), () -> loginAlertIsCooldown = false, 1200);
        }
    }


    private void logOutPlayerNotify(Player player, boolean forced) {
        if (AuthMeApi.getInstance().isAuthenticated(player)) {
            for (Player playerr : Bukkit.getOnlinePlayers()) {
                if (playerr.canSee(player) || forced) {
                    if (!logoutAlertIsCooldown) playerr.playSound(playerr.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, (float) 0.3, (float) 0.2);
                    Component logoutComponent = Component.text(ChatColor.RED + "[-] " + ChatColor.YELLOW + player.getName());
                    playerr.sendMessage(logoutComponent);
                    ChatController.getPlayer(playerr).appendMessage(-1, null, null, logoutComponent);
                }
            }
            if (!logoutAlertIsCooldown) {
                logoutAlertIsCooldown = true;
                scheduler.runTaskLater(BetterChat.getInstance(), () -> logoutAlertIsCooldown = false, 1200);
            }
        }
    }

    @EventHandler
    public void PrivateMessageSend(PrivateMessagePreSendEvent e) { // Essentials event
        e.setCancelled(true);
        if (ChatController.chatIsCooldown(BetterChat.getInstance().getServer().getPlayer(e.getSender().getName()))) return;

        Player recipient = BetterChat.getInstance().getServer().getPlayer(e.getRecipient().getName());
        Player sender = BetterChat.getInstance().getServer().getPlayer(e.getSender().getName());

        if (recipient != null && sender != null) {
            Component message = Component.text(e.getMessage(), NamedTextColor.WHITE);
            message = itemParser(message, sender);
            message = positionParser(message, sender);

            Component delMessage = Component.empty()
                    .append(Component.text("[X] ", NamedTextColor.RED)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/dmsg " + messageCounter))
                            .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(
                                            Component.text("Удалить сообшение", NamedTextColor.RED)
                                    ))
                            ));

            boolean havePermDelMessage = sender.hasPermission("betterchat.detelemessage");

            sender.playSound(sender.getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, (float) 0.3, (float) 1.5);

            Component senderTag = Component.empty()
                    .append(havePermDelMessage ? delMessage : Component.empty())
                    .append(Component.text("Сообщение для ").color(TextColor.color(0xFF9D1F)))
                    .append(Component.text(recipient.getName()).color(TextColor.color(0xFFDB45)))
                    .append(Component.text(": ").color(TextColor.color(0xFF9D1F)));
            Component senderMessage = Component.empty()
                    .append(senderTag)
                    .append(message);

            sender.sendMessage(senderMessage);
            ChatController.getPlayer(sender).appendMessage(messageCounter, senderTag, null, message);


            havePermDelMessage = recipient.hasPermission("betterchat.detelemessage");

            recipient.playSound(recipient.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, (float) 0.5, (float) 2);

            Component recipientTag = Component.empty()
                    .append(havePermDelMessage ? delMessage : Component.empty())
                    .append(Component.text("Сообщение от ").color(TextColor.color(0xFF9D1F)))
                    .append(Component.text(sender.getName())
                            .color(TextColor.color(0xFFDB45))
                            .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Ответить игроку ").color(TextColor.color(0xFF9D1F)).append(Component.text(sender.getName()).color(TextColor.color(0xFFDB45)))))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/w " + sender.getName() + " ")))
                    .append(Component.text(": ").color(TextColor.color(0xFF9D1F)));

            Component recipientMessage = Component.empty()
                    .append(recipientTag)
                    .append(message);

            recipient.sendMessage(recipientMessage);
            ChatController.getPlayer(recipient).appendMessage(messageCounter, recipientTag, null, message);

            messageCounter++;

            Logger.getLogger("PM").log(Level.INFO, "От " + sender.getName() + " для " + recipient.getName() + ": " + e.getMessage());

            User senderr = BetterChat.getInstance().getAPIEssentials().getUser(sender);
            User recipientt = BetterChat.getInstance().getAPIEssentials().getUser(recipient);
            senderr.setReplyRecipient(e.getRecipient());
            recipientt.setReplyRecipient(e.getSender());
        }
    }

    @EventHandler
    public void DeathEvent(PlayerDeathEvent e) {
        if (!e.isCancelled()) {
            Player player = e.getPlayer();
            Component message = Component.empty();
            String world = getWorldName(player.getLocation().getWorld().getName());

            message = message
                    .append(Component.text("Координаты вашей смерти:").color(TextColor.color(0xFF9D1F)))
                    .append(Component.newline())
                    .append(Component.text("X: ").color(TextColor.color(0xFFFF55)))
                    .append(Component.text(player.getLocation().getBlockX()).color(TextColor.color(0xFFFFFF)))
                    .append(Component.space())
                    .append(Component.text("Y: ").color(TextColor.color(0xFFFF55)))
                    .append(Component.text(player.getLocation().getBlockY()).color(TextColor.color(0xFFFFFF)))
                    .append(Component.space())
                    .append(Component.text("Z: ").color(TextColor.color(0xFFFF55)))
                    .append(Component.text(player.getLocation().getBlockZ()).color(TextColor.color(0xFFFFFF)))
                    .append(Component.newline())
                    .append(Component.text("Мир: ").color(TextColor.color(0xFFFF55)))
                    .append(Component.text(world).color(TextColor.color(0xFFFFFF)));
            player.sendMessage(message);
            ChatController.getPlayer(player).appendMessage(-1, null, null, message);

        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void ChatEvent(AsyncChatEvent e) {
        if (e.isCancelled()) return;
        e.setCancelled(true);
        if (ChatController.chatIsCooldown(e.getPlayer())) return;

        Player sender = e.getPlayer();

        String content = PlainTextComponentSerializer.plainText().serialize(e.message());

        Location location = sender.getLocation();

        // Global chat
        if (content.startsWith("!")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendFinalChatComponent(content, e.getPlayer(), player, true, messageCounter);
            }
        // Local chat
        } else {
            boolean heard = false;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!location.getWorld().equals(player.getWorld())) continue;
                if (location.distance(player.getLocation()) < 100) {
                    sendFinalChatComponent(content, e.getPlayer(), player, false, messageCounter);
                    if (!player.equals(sender) && sender.canSee(player)) heard = true;
                }
            }
            if (!heard) {
                Component notHeard = Component.text()
                        .append(Component.text("Вас никто не услышал. ", Style.style(NamedTextColor.namedColor(0xFFFF55), TextDecoration.ITALIC)))
                        .append(Component.text("[Как написать в глобальный чат?]", Style.style(NamedTextColor.namedColor(0x55FF55)))
//                                .clickEvent(ClickEvent.runCommand("!" + content))
                                .hoverEvent(HoverEvent.showText(Component.text()
                                                .append(Component.text("Поставьте "))
                                                .append(Component.text("!", NamedTextColor.GREEN))
                                                .append(Component.text(" в начале сообщения")).build()
                                        )
                                ))
                        .build();
                sender.sendMessage(notHeard);
                ChatController.getPlayer(sender).appendMessage(-1, null, null, notHeard);
            }
        }
        messageCounter++;
        Component logContent = Component.empty()
                .append(Component.text((content.startsWith("!") ? "[G]" : "[L]") + " "))
                .append(Component.text(sender.getName()))
                .append(Component.text(": "))
                .append(Component.text(content.startsWith("!") ? content.replaceFirst("!", "") : content));
        chatLogger.info(PlainTextComponentSerializer.plainText().serialize(logContent));
    }

    private void sendFinalChatComponent(String messageContent, Player sender, Player player, boolean isGlobal, int messageId) {
        Component global = Component.text("[G] ")
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Глобальный чат").color(TextColor.color(0x55FF55))))
                .color(TextColor.color(0x55FF55));

        Component local = Component.text("[L] ")
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Локальный чат").color(TextColor.color(0xAAAAAA))))
                .color(TextColor.color(0xAAAAAA));

        Component delMessage = Component.empty()
                .append(Component.text("[X] ", NamedTextColor.RED)
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/dmsg " + messageId))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(
                        Component.text("Удалить сообшение", NamedTextColor.RED)
                        ))
                ));

        Component nickname = Component.empty()
                .append(Component.text(sender.getName()))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/w " + sender.getName() + " "))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(
                        Component.text("Нажмите ", NamedTextColor.GREEN)
                                .append(Component.text("ЛКМ", NamedTextColor.WHITE))
                                .append(Component.text(", чтобы отправить личное сообщение игроку ", NamedTextColor.GREEN))
                                .append(Component.text(sender.getName(), NamedTextColor.WHITE))
                )));

        Component messageColon = Component.text(": ")
                .color(TextColor.color(0x5D5D5D));

        if (isGlobal) messageContent = messageContent.replaceFirst("!", "");
        if (messageContent.contains(player.getName())) {
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, (float) 0.5, (float) 2);
            String tagName = LegacyComponentSerializer.legacyAmpersand().serialize(Component.text().append(Component.text(player.getName(), NamedTextColor.YELLOW)).append(Component.text("§f")).build());
            messageContent = messageContent.replaceFirst(player.getName(), tagName);
        }

        Component finalMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(messageContent);

        finalMessage = positionParser(finalMessage, sender);
        finalMessage = itemParser(finalMessage, sender);

        boolean havePermDelMessage = player.hasPermission("betterchat.detelemessage");

        Component tags = Component.empty()
                .append(havePermDelMessage ? delMessage : Component.empty())
                .append(isGlobal ? global : local);
        Component sendMessage = Component.empty()
                .append(tags)
                .append(nickname)
                .append(messageColon)
                .append(finalMessage);

        Component chatHistoryContent = LegacyComponentSerializer.legacyAmpersand().deserialize(messageContent);
        player.sendMessage(sendMessage);
        ChatController.getPlayer(player).appendMessage(messageCounter, tags, nickname, chatHistoryContent);
    }

    private String getWorldName(String name) {
        return switch (name) {
            case "world" -> "Верхний мир";
            case "world_the_end" -> "Энд";
            case "world_nether" -> "Нижний мир";
            default -> "Неизвестный мир";
        };
    }

    private Component positionParser(Component message, Player sender) {
        TextReplacementConfig parser = TextReplacementConfig.builder()
                .once()
                .match("%pos%|@pos|@p")
                .replacement(Component.text(
                                "[X: " + sender.getLocation().getBlockX() +
                                        " Y: " + sender.getLocation().getBlockY() +
                                        " Z: " + sender.getLocation().getBlockZ() + "]", NamedTextColor.GREEN)
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Мир: ").append(Component.text(getWorldName(sender.getLocation().getWorld().getName()), NamedTextColor.GREEN))))
                ).build();
        return message.replaceText(parser);
    }
    private Component itemParser(Component message, Player sender) {
        ItemStack item = sender.getInventory().getItemInMainHand();

        TextReplacementConfig parser = TextReplacementConfig.builder()
                .once()
                .match("%hand%|%item%|@item|@i")
                .replacement(Component.empty().color(item.displayName().color())
                        .append(Component.text('['))
                        .append(Component.translatable(item.translationKey()))
                        .append(Component.text(']'))
                        .hoverEvent(item)
                ).build();
        return message.replaceText(parser);
    }

}
