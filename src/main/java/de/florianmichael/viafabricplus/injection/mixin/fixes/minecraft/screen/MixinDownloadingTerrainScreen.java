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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.screen;

import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.raphimc.vialoader.util.VersionEnum;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DownloadingTerrainScreen.class)
public class MixinDownloadingTerrainScreen extends Screen {

    @Shadow @Final private long loadStartTime;
    @Shadow private boolean closeOnNextTick;
    @Shadow private boolean ready;
    @Unique
    private int viafabricplus_tickCounter;

    public MixinDownloadingTerrainScreen(Text title) {
        super(title);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void injectTick(CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_1)) {
            viafabricplus_tickCounter++;

            if (viafabricplus_tickCounter % 20 == 0) {
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new KeepAliveC2SPacket(0));
            }
        }
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_1tor1_19_2)) {
            final boolean isTimeOver = this.closeOnNextTick || System.currentTimeMillis() > this.loadStartTime + 2000L;

            if (isTimeOver && this.client != null && this.client.player != null) {
                final BlockPos blockPos = this.client.player.getBlockPos();
                final boolean isWorldLoaded = this.client.world != null && this.client.world.isOutOfHeightLimit(blockPos.getY());

                if (isWorldLoaded || this.client.worldRenderer.isRenderingReady(blockPos)) {
                    this.close();
                }

                if (this.ready) {
                    this.closeOnNextTick = true;
                }

            }
            ci.cancel();
        }
    }
}
