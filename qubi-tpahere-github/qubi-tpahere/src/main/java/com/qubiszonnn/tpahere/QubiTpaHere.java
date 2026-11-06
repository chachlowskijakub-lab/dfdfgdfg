package com.qubiszonnn.tpahere;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QubiTpaHere implements ClientModInitializer {
    public static boolean enabled = true;
    public static int cooldownSeconds = 5;

    private static final Map<String, Long> lastSent = new HashMap<>();
    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.qubitpa.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.create(Identifier.of("qubitpa", "main"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                client.setScreen(new QubiScreen());
            }
        });

        // Czat serwerowy (Donut wysyla wiekszosc wiadomosci jako zwykle/systemowe)
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!overlay) handle(message.getString());
        });
        ClientReceiveMessageEvents.CHAT.register((message, signed, sender, params, ts) -> handle(message.getString()));
    }

    private static void handle(String raw) {
        if (!enabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        String nick = findSender(raw, mc);
        if (nick == null) return;

        long now = System.currentTimeMillis();
        Long last = lastSent.get(nick.toLowerCase());
        if (last != null && now - last < cooldownSeconds * 1000L) return;
        lastSent.put(nick.toLowerCase(), now);

        // Bez ukosnika - sendChatCommand dodaje go sam. Wysyla DOKLADNIE: /tpahere <nick>
        mc.execute(() -> mc.getNetworkHandler().sendChatCommand("tpahere " + nick));
    }

    /** Szuka nicku nadawcy TYLKO sposrod graczy faktycznie online (tab lista),
     *  wiec ranga/prefiks nigdy nie trafi do komendy. */
    private static String findSender(String raw, MinecraftClient mc) {
        // Nadawca stoi przed separatorem typu ">", "»", ":"
        int sep = -1;
        for (char c : new char[]{'»', '>', ':'}) {
            int i = raw.indexOf(c);
            if (i > 0 && (sep == -1 || i < sep)) sep = i;
        }
        if (sep == -1) return null; // brak separatora = to nie jest wiadomosc gracza (np. join/leave)
        String prefix = raw.substring(0, sep);

        String own = mc.player.getGameProfile().name();
        String best = null;
        int bestPos = -1;

        for (PlayerListEntry e : mc.getNetworkHandler().getPlayerList()) {
            String name = e.getProfile().name();
            if (name == null || name.isEmpty() || name.equalsIgnoreCase(own)) continue;
            Matcher m = Pattern.compile("(?<![A-Za-z0-9_])" + Pattern.quote(name) + "(?![A-Za-z0-9_])",
                    Pattern.CASE_INSENSITIVE).matcher(prefix);
            int pos = -1;
            while (m.find()) pos = m.start();
            // wybieramy nick stojacy NAJBLIZEJ separatora (po randze)
            if (pos > bestPos) { bestPos = pos; best = name; }
        }
        return best;
    }
}
