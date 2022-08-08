package hikko.betterchat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public final class ChatUtils {

    public static ArrayList<Integer> reportedIdMessages = new ArrayList<>();
    public static ArrayList<Integer> deletedIdMessages = new ArrayList<>();

    public static final Component globalChatComponent = Component.text("[G] ")
            .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Глобальный чат").color(TextColor.color(0x55FF55))))
            .color(NamedTextColor.GREEN);

    public static final Component localChatComponent = Component.text("[L] ")
            .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Локальный чат").color(TextColor.color(0xAAAAAA))))
            .color(NamedTextColor.GRAY);

    public static final Component messageColonComponent = Component.text(": ")
            .color(TextColor.color(0x5D5D5D));

    public static final Component notHeardComponent = Component.empty()
            .append(Component.text("Вас никто не услышал. ", Style.style(NamedTextColor.namedColor(0xFFFF55), TextDecoration.ITALIC)))
            .append(Component.text("[Как написать в глобальный чат?]", Style.style(NamedTextColor.namedColor(0x55FF55)))
//                                .clickEvent(ClickEvent.runCommand("!" + content))
                    .hoverEvent(HoverEvent.showText(Component.text()
                                    .append(Component.text("Поставьте "))
                                    .append(Component.text("!", NamedTextColor.GREEN))
                                    .append(Component.text(" в начале сообщения")).build()
                            )
                    ));

    public static Component getDelMessageComponent(int messageId) {
        return Component.empty()
                .append(Component.text("[X] ", NamedTextColor.RED)
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/dmsg " + messageId))
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(
                                        Component.text("Удалить сообшение", NamedTextColor.RED)
                                ))
                        ));
    }

    public static Component getReportMessageComponent(int messageId) {
        return Component.empty()
                .append(Component.text("⚠ ", TextColor.color(0xFFA513))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/rmsg " + messageId))
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(
                                        Component.text("Пожаловаться на сообшение", TextColor.color(0xFFA513))
                                ))
                        ));
    }

    public static Component getNicknameComponent(Player sender) {
        return Component.empty()
                .append(Component.text(sender.getName()))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/w " + sender.getName() + " "))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(
                        Component.text("Нажмите ", NamedTextColor.GREEN)
                                .append(Component.text("ЛКМ", NamedTextColor.WHITE))
                                .append(Component.text(", чтобы отправить личное сообщение игроку ", NamedTextColor.GREEN))
                                .append(Component.text(sender.getName(), NamedTextColor.WHITE))
                )));
    }

}
