package com.qubiszonnn.tpahere;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class QubiScreen extends Screen {
    private static final int PURPLE = 0xFF6A0DAD;
    private static final int DARK_PURPLE = 0xFF2B0A4A;
    private static final int LIGHT_PURPLE = 0xFFB66BFF;
    private static final int WHITE = 0xFFFFFFFF;

    private static final int W = 240, H = 150;
    private ButtonWidget toggle, cdBtn;

    public QubiScreen() { super(Text.literal("Qubi TPAHere")); }

    @Override
    protected void init() {
        int x = (width - W) / 2, y = (height - H) / 2;
        toggle = addDrawableChild(ButtonWidget.builder(toggleText(), b -> {
            QubiTpaHere.enabled = !QubiTpaHere.enabled;
            b.setMessage(toggleText());
        }).dimensions(x + 20, y + 45, W - 40, 20).build());

        cdBtn = addDrawableChild(ButtonWidget.builder(cdText(), b -> {
            QubiTpaHere.cooldownSeconds = QubiTpaHere.cooldownSeconds >= 30 ? 1 : QubiTpaHere.cooldownSeconds + 1;
            b.setMessage(cdText());
        }).dimensions(x + 20, y + 72, W - 40, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Zamknij"), b -> close())
                .dimensions(x + 20, y + 99, W - 40, 20).build());
    }

    private Text toggleText() {
        return Text.literal("Auto /tpahere: " + (QubiTpaHere.enabled ? "WŁĄCZONE" : "WYŁĄCZONE"));
    }
    private Text cdText() { return Text.literal("Cooldown na gracza: " + QubiTpaHere.cooldownSeconds + "s"); }

    @Override
    public void renderBackground(DrawContext c, int mx, int my, float delta) {
        super.renderBackground(c, mx, my, delta);
        int x = (width - W) / 2, y = (height - H) / 2;
        c.fill(x - 2, y - 2, x + W + 2, y + H + 2, LIGHT_PURPLE); // ramka
        c.fill(x, y, x + W, y + H, DARK_PURPLE);                  // tlo
        c.fill(x, y, x + W, y + 30, PURPLE);                      // pasek tytulu
        c.fill(x, y + 30, x + W, y + 31, WHITE);
        c.drawCenteredTextWithShadow(textRenderer, Text.literal("§lQUBI TPAHERE"), width / 2, y + 11, WHITE);
        c.drawCenteredTextWithShadow(textRenderer, Text.literal("created by qubiszonnn"), width / 2, y + H - 18, LIGHT_PURPLE);
    }

    @Override
    public boolean shouldPause() { return false; }
}
