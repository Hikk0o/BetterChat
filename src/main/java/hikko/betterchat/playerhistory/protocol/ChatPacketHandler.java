package hikko.betterchat.playerhistory.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import hikko.betterchat.BetterChat;
import hikko.betterchat.playerhistory.ChatController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


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
				Player player = event.getPlayer();

				try {
					if (player == null) return;
					if (ChatController.getPlayer(player) == null) return;
					if (ChatController.getPlayer(player).isChatLock()) return;
					StructureModifier<WrappedChatComponent> chatComponents = event.getPacket().getChatComponents();
					String jsonMessage = chatComponents.getTarget().toString()
							.replace("ClientboundSystemChatPacket[adventure$content=TextComponentImpl", "")
							.replace("ClientboundSystemChatPacket[adventure$content=null, content=", "")
							.replace(", content=null, overlay=false]", "")
							.replace(", overlay=false]", "");

					try {
						Component component = GsonComponentSerializer.gson().deserialize(jsonMessage);
						ChatController.getPlayer(player).appendMessage(-1, null, null, component);
					} catch (Exception e) {
						// pass
					}
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
