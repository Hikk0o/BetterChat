package hikko.betterchat.playerhistory.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.stream.MalformedJsonException;
import hikko.betterchat.BetterChat;
import hikko.betterchat.playerhistory.ChatController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;


public class ChatPacketHandler {

	public ChatPacketHandler() {
		BetterChat.logger.info("Loading ChatPacketHandler...");
		Plugin plugin = BetterChat.getInstance();
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();

		manager.addPacketListener(new PacketAdapter(
				plugin,
				ListenerPriority.NORMAL,
				PacketType.Play.Server.SYSTEM_CHAT) {
			@Override
            public void onPacketSending(PacketEvent event) {
//				BetterChat.logger.info("packet");
//				if (event.getPacketType() == PacketType.Play.Server.SYSTEM_CHAT) {
				PacketContainer packet = event.getPacket();
				Player player = event.getPlayer();

				try {
//						if (packet.getChatTypes().getValues().isEmpty()) return;
					if (player == null) return;
					if (ChatController.getPlayer(player) == null) return;
					if (ChatController.getPlayer(player).isChatLock()) return;
					StructureModifier<WrappedChatComponent> chatComponents = event.getPacket().getChatComponents();
//					BetterChat.logger.severe(chatComponents.getTarget().toString()); // Debug
					String jsonMessage = chatComponents.getTarget().toString()
							.replace("ClientboundSystemChatPacket[adventure$content=TextComponentImpl", "")
							.replace("ClientboundSystemChatPacket[adventure$content=null, content=", "")
							.replace(", content=null, overlay=false]", "")
							.replace(", overlay=false]", "");
//					BetterChat.logger.warning(jsonMessage); // Debug

					try {
						Component component = GsonComponentSerializer.gson().deserialize(jsonMessage);
						ChatController.getPlayer(player).appendMessage(-1, null, null, component);
//						BetterChat.logger.info(PlainTextComponentSerializer.plainText().serialize(component));
					} catch (Exception e) {
						// pass
					}

//					try {
//						Component component = GsonComponentSerializer.gson().deserialize(chatComponents.getValues().get(0).getJson());
//						BetterChat.logger.info(player.getName() + " append");
//						BetterChat.logger.info(PlainTextComponentSerializer.plainText().serialize(component));
//						ChatController.getPlayer(player).appendMessage(-1, null, null, component);
//					} catch (NullPointerException e) {
//						e.printStackTrace();
//					}
				}
				catch (NullPointerException e) {
					BetterChat.logger.warning("------------ Start printStackTrace ------------");
					e.printStackTrace();
					BetterChat.logger.warning("------------- End printStackTrace -------------");
				}
			}
//			}
		});
	}
}
