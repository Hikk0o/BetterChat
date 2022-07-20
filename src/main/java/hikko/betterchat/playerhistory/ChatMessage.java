package hikko.betterchat.playerhistory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.units.qual.C;

public class ChatMessage {
    public int id;
    private Component tag;
    private Component sender;
    private Component content;

    ChatMessage(int id, Component tag, Component sender, Component content) {
        this.id = id;
        this.tag = tag;
        this.sender = sender;
        this.content = content;
    }

    public Component getTag() {
        return tag;
    }

    public Component getContent() {
        return content;
    }

    public Component getSender() {
        return sender;
    }

    public void setContent(Component content) {
        this.content = content;
    }

    public Component getFullComponent() {
        if (sender != null) {
            return Component.text()
                    .append(tag)
                    .append(sender)
                    .append(Component.text(": ").color(TextColor.color(0x5D5D5D)))
                    .append(content)
                    .build();
        } else {
            return content;
        }

    }
}
