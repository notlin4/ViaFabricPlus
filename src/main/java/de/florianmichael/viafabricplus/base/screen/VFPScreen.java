/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.florianmichael.viafabricplus.base.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;

/**
 * This class is a wrapper for the {@link net.minecraft.client.gui.screen.Screen} class which provides some global
 * functions and features used in all screens which are added by ViaFabricPlus
 */
public class VFPScreen extends Screen {

    private final boolean backButton;
    public Screen prevScreen;

    public VFPScreen(String title, boolean backButton) {
        super(Text.of(title));
        this.backButton = backButton;
    }

    /**
     * Intended method to open a VFP screen
     *
     * @param prevScreen The current screen from which the VFP screen is opened
     */
    public void open(final Screen prevScreen) {
        this.prevScreen = prevScreen;

        RenderSystem.recordRenderCall(() -> MinecraftClient.getInstance().setScreen(this));
    }

    @Override
    protected void init() {
        if (backButton) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("<-"), button -> this.close()).position(5, 5).size(20, 20).build());
        }
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(prevScreen);
    }

    public void renderTitle(final DrawContext context) {
        renderTitle(context, Text.of("https://github.com/ViaVersion/ViaFabricPlus"));
    }

    /**
     * Renders the ViaFabricPlus title with a specific subtitle
     *
     * @param context The current draw context
     * @param subTitle The subtitle which should be rendered
     */
    public void renderTitle(final DrawContext context, final Text subTitle) {
        final MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.scale(2F, 2F, 2F);
        context.drawCenteredTextWithShadow(textRenderer, "ViaFabricPlus", width / 4, 3, Color.ORANGE.getRGB());
        matrices.pop();

        context.drawCenteredTextWithShadow(textRenderer, subTitle, width / 2, (textRenderer.fontHeight + 2) * 2 + 3, -1);
    }

    /**
     * Plays Minecraft's button click sound
     */
    public static void playClickSound() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /**
     * Opens an error screen with a specific title and throws the given throwable
     *
     * @param title The title of the error screen
     * @param throwable The throwable which should be thrown
     */
    public void showErrorScreen(final String title, final Throwable throwable) {
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new NoticeScreen(() ->
                RenderSystem.recordRenderCall(() -> MinecraftClient.getInstance().setScreen(this)), Text.of(title),
                Text.translatable("misc.viafabricplus.error").append("\n" + throwable.getMessage()),
                Text.translatable("misc.viafabricplus.cancel"), false)));

        ViaFabricPlus.LOGGER.error(throwable);
    }
}
