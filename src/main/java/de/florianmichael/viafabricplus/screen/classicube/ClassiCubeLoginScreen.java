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
package de.florianmichael.viafabricplus.screen.classicube;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.classic4j.ClassiCubeHandler;
import de.florianmichael.classic4j.api.LoginProcessHandler;
import de.florianmichael.classic4j.model.classicube.highlevel.CCAccount;
import de.florianmichael.viafabricplus.definition.c0_30.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.integration.Classic4JImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ClassiCubeLoginScreen extends Screen {
    public final static ClassiCubeLoginScreen INSTANCE = new ClassiCubeLoginScreen();
    public Screen prevScreen;

    public ClassiCubeLoginScreen() {
        super(Text.literal("ClassiCube Login"));
    }

    public static ClassiCubeLoginScreen get(final Screen prevScreen) {
        ClassiCubeLoginScreen.INSTANCE.prevScreen = prevScreen;
        ClassiCubeLoginScreen.INSTANCE.status = Text.translatable("classicube.viafabricplus.account");
        return ClassiCubeLoginScreen.INSTANCE;
    }

    private TextFieldWidget nameField;
    private TextFieldWidget passwordField;

    private Text status;

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(nameField = new TextFieldWidget(textRenderer, width / 2 - 150, 70 + 10, 300, 20, Text.empty()));
        this.addDrawableChild(passwordField = new TextFieldWidget(textRenderer, width / 2 - 150, nameField.getY() + 20 + 5, 300, 20, Text.empty()));
        passwordField.setRenderTextProvider((s, integer) -> Text.literal("*".repeat(s.length())).asOrderedText());

        nameField.setPlaceholder(Text.literal("Name"));
        passwordField.setPlaceholder(Text.literal("Password"));

        nameField.setMaxLength(Integer.MAX_VALUE);
        passwordField.setMaxLength(Integer.MAX_VALUE);

        final CCAccount classiCubeAccount = ClassiCubeAccountHandler.INSTANCE.getAccount();
        if (classiCubeAccount != null) {
            nameField.setText(classiCubeAccount.username());
            passwordField.setText(classiCubeAccount.password());
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Login"), button -> {
            ClassiCubeAccountHandler.INSTANCE.setAccount(new CCAccount(nameField.getText(), passwordField.getText()));
            status = Text.translatable("classicube.viafabricplus.loading");

            ClassiCubeHandler.requestAuthentication(ClassiCubeAccountHandler.INSTANCE.getAccount(), null, new LoginProcessHandler() {
                @Override
                public void handleMfa(CCAccount account) {
                    RenderSystem.recordRenderCall(() -> MinecraftClient.getInstance().setScreen(ClassiCubeMFAScreen.get(prevScreen)));
                }

                @Override
                public void handleSuccessfulLogin(CCAccount account) {
                    RenderSystem.recordRenderCall(() -> ClassiCubeServerListScreen.open(MinecraftClient.getInstance().currentScreen, this));
                }

                @Override
                public void handleException(Throwable throwable) {
                    throwable.printStackTrace();
                    status = Text.literal(throwable.getMessage());
                }
            });
        }).position(width / 2 - 75, passwordField.getY() + (20 * 4) + 5).size(150, 20).build());
    }

    @Override
    public void tick() {
        super.tick();

        nameField.tick();
        passwordField.tick();
    }

    @Override
    public void close() {
        this.client.setScreen(this.prevScreen);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 70, 16777215);
        context.drawCenteredTextWithShadow(this.textRenderer, this.status, this.width / 2, 1, 16777215);

        super.render(context, mouseX, mouseY, delta);
    }
}
