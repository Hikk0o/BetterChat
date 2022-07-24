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
import net.ess3.api.events.VanishStatusChangeEvent;
import net.kyori.adventure.text.Component;
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
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatEvents implements Listener {
    public ChatEvents() {
        BetterChat.getInstance().getLogger().log(Level.INFO, "Loading chat events...");
        new ChatPacketHandler();
    }
    private final Logger chatLogger = Logger.getLogger("Chat");
    private final BukkitScheduler scheduler = BetterChat.getInstance().getServer().getScheduler();
    public int messageCounter = 0;

    private boolean loginAlertIsCooldown = false;
    private boolean logoutAlertIsCooldown = false;

    @EventHandler
    public void LoginEvent(PlayerHideEvent e) {
        logOutPlayerNotify(e.getPlayer(), true);
    }
    @EventHandler
    public void LoginEvent(PlayerShowEvent e) {
        logInPlayerNotify(e.getPlayer(), true);
    }


    @EventHandler
    public void LoginEvent(LoginEvent e) { // AuthMe login event
        logInPlayerNotify(e.getPlayer(), false);
    }

    @EventHandler
    public void QuitEvent(PlayerQuitEvent e) {
        ChatController.removePlayer(e.getPlayer());
        logOutPlayerNotify(e.getPlayer(), false);
    }

    private void logInPlayerNotify(Player player, boolean forced) {
        for (Player playerr : Bukkit.getOnlinePlayers()) {
            if (playerr.canSee(player) || forced) {
                if (!loginAlertIsCooldown) playerr.playSound(playerr.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, (float) 0.3, 1);
                playerr.sendMessage(ChatColor.GREEN + "[+] " + ChatColor.YELLOW + player.getName());
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
                    playerr.sendMessage(ChatColor.RED + "[-] " + ChatColor.YELLOW + player.getName());
                }
            }
            if (!logoutAlertIsCooldown) {
                logoutAlertIsCooldown = true;
                scheduler.runTaskLater(BetterChat.getInstance(), () -> logoutAlertIsCooldown = false, 1200);
            }
        }
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        ChatController.addPlayer(player);
    }

    @EventHandler
    public void PrivateMessageSent(PrivateMessagePreSendEvent e) { // Essentials event
        e.setCancelled(true);
        if (ChatController.chatIsCooldown(BetterChat.getInstance().getServer().getPlayer(e.getSender().getName()))) return;
        String message = e.getMessage();

        Player recipient = BetterChat.getInstance().getServer().getPlayer(e.getRecipient().getName());
        Player sender = BetterChat.getInstance().getServer().getPlayer(e.getSender().getName());
        if (recipient != null && sender != null) {
            sender.playSound(sender.getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, (float) 0.3, (float) 1.5);
            Component sernderMessage = Component.empty();
            sernderMessage = sernderMessage
                    .append(Component.text("Сообщение для ").color(TextColor.color(0xFF9D1F)))
                    .append(Component.text(recipient.getName()).color(TextColor.color(0xFFDB45)))
                    .append(Component.text(": ").color(TextColor.color(0xFF9D1F)))
                    .append(Component.text(message).color(TextColor.color(0xFFFFFF)));

            sender.sendMessage(sernderMessage);
//            messageQueue.getPlayer(sender).addMessage(sernderMessage);

            recipient.playSound(recipient.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, (float) 0.5, (float) 2);
            Component recipientMessage = Component.empty();
            recipientMessage = recipientMessage
                    .append(Component.text("Сообщение от ").color(TextColor.color(0xFF9D1F)))
                    .append(Component.text(sender.getName())
                            .color(TextColor.color(0xFFDB45))
                            .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Ответить игроку ").color(TextColor.color(0xFF9D1F)).append(Component.text(sender.getName()).color(TextColor.color(0xFFDB45)))))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/w " + sender.getName() + " ")))
                    .append(Component.text(": ").color(TextColor.color(0xFF9D1F)))
                    .append(Component.text(message).color(TextColor.color(0xFFFFFF)));

            recipient.sendMessage(recipientMessage);

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
            String world;
            if (player.getLocation().getWorld().getName().equals("world")) {
                world = "Верхний мир";
            } else if (player.getLocation().getWorld().getName().equals("world_the_end")) {
                world = "Энд";
            } else if (player.getLocation().getWorld().getName().equals("world_nether")) {
                world = "Ад";
            } else {
                world = "Неизвестный мир";
            }
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
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void ChatEvent(AsyncChatEvent e) {
        if (e.isCancelled()) return;
        e.setCancelled(true);
        if (ChatController.chatIsCooldown(e.getPlayer())) return;

        Player sender = e.getPlayer();

        Component nickname = Component.empty();
        Component descNickname = Component.empty();
        Component messageColon = Component.text(": ")
                .color(TextColor.color(0x5D5D5D));
        String content = PlainTextComponentSerializer.plainText().serialize(e.message());

        nickname = nickname
                .append(Component.text(sender.getName()))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/w " + e.getPlayer().getName() + " "))
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, descNickname.append(
                                Component.text("Нажмите ",NamedTextColor.GREEN)
                                        .append(Component.text("ЛКМ", NamedTextColor.WHITE))
                                        .append(Component.text(", чтобы отправить личное сообщение игроку ", NamedTextColor.GREEN))
                                        .append(Component.text(e.getPlayer().getName(), NamedTextColor.WHITE))
                        )));



        Component global = Component.text("[G] ")
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Глобальный чат").color(TextColor.color(0x55FF55))))
                .color(TextColor.color(0x55FF55));

        Component local = Component.text("[L] ")
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Локальный чат").color(TextColor.color(0xAAAAAA))))
                .color(TextColor.color(0xAAAAAA));

        Location location = sender.getLocation();

        Component logMessage;
        if (content.startsWith("!")) { // Global chat
            logMessage = Component.text("[G] ", NamedTextColor.GREEN)
                    .append(Component.text(e.getPlayer().getName()))
                    .append(Component.text(": " + content.replaceFirst("!", "")));

            for (Player player : Bukkit.getOnlinePlayers()) {
                String playerContent = content;

                if (playerContent.contains(player.getName() + " ")) {
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, (float) 0.5, (float) 2);
                    String tagName = LegacyComponentSerializer.legacyAmpersand().serialize(Component.text().append(Component.text(player.getName(), NamedTextColor.YELLOW)).append(Component.text(" ", NamedTextColor.WHITE)).build());
                    playerContent = playerContent.replaceFirst(player.getName() + " ", tagName);
                }
                Component sendMessage = Component.empty();
                Component playerContentComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(playerContent.replaceFirst("!", ""));
                sendMessage = sendMessage
                        .append(global)
                        .append(nickname)
                        .append(messageColon)
                        .append(playerContentComponent);
                ChatController.getPlayer(player).appendMessage(messageCounter, global, nickname, playerContentComponent);
                player.sendMessage(sendMessage);

            }
        } else { // Local chat
            logMessage = Component.text("[L] ", NamedTextColor.GREEN)
                    .append(Component.text(e.getPlayer().getName()))
                    .append(Component.text(": " + content));

            boolean heard = false;

            for (Player player : Bukkit.getOnlinePlayers()) {
                String playerContent = content;
                if (!location.getWorld().equals(player.getWorld())) continue;
                if (location.distance(player.getLocation()) < 100) {

                    if (playerContent.contains(player.getName() + " ")) {
                        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, (float) 0.5, (float) 2);
                        String tagName = LegacyComponentSerializer.legacyAmpersand().serialize(Component.text().append(Component.text(player.getName(), NamedTextColor.YELLOW)).append(Component.text(" ", NamedTextColor.WHITE)).build());
                        playerContent = playerContent.replaceFirst(player.getName() + " ", tagName);
                    }
                    Component sendMessage = Component.empty();
                    Component playerContentComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(playerContent.replaceFirst("!", ""));
                    sendMessage = sendMessage
                            .append(local)
                            .append(nickname)
                            .append(messageColon)
                            .append(LegacyComponentSerializer.legacyAmpersand().deserialize(playerContent));

                    if (!player.equals(sender) && sender.canSee(player)) heard = true;

                    ChatController.getPlayer(player).appendMessage(messageCounter, local, nickname, playerContentComponent);
                    player.sendMessage(sendMessage);

                }
            }
            if (!heard) {
                Component notHeard = Component.text()
                        .append(Component.text("Вас никто не услышал. ", Style.style(NamedTextColor.namedColor(0xFFFF55), TextDecoration.ITALIC)))
                        .append(Component.text("[Написать в глобальный чат]", Style.style(NamedTextColor.namedColor(0x55FF55)))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "!" + content))
                                .hoverEvent(HoverEvent.hoverEvent(
                                        HoverEvent.Action.SHOW_TEXT, Component.text()
                                                .append(Component.text("Нажми сюда или просто напиши "))
                                                .append(Component.text("!", NamedTextColor.GREEN))
                                                .append(Component.text(" в начале сообщения")).build()
                                        )
                                )
                        ).build();
                ChatController.getPlayer(sender).appendMessage(messageCounter, null, null, notHeard);
                sender.sendMessage(notHeard);
            }
        }
        messageCounter++;
        chatLogger.log(Level.INFO, PlainTextComponentSerializer.plainText().serialize(logMessage));
    }

}
