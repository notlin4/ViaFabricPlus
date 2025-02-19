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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.block;

import net.minecraft.block.*;
import net.raphimc.vialoader.util.VersionEnum;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlock.class)
public abstract class MixinHopperBlock extends BlockWithEntity {

    @Unique
    private final static VoxelShape viafabricplus_inside_shape_v1_12_2 = Block.createCuboidShape(2, 10, 2, 14, 16, 14);

    @Unique
    private final static VoxelShape viafabricplus_hopper_shape_v1_12_2 = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), viafabricplus_inside_shape_v1_12_2, BooleanBiFunction.ONLY_FIRST);

    @Unique
    private boolean viafabricplus_requireOriginalShape;

    public MixinHopperBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void injectGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (viafabricplus_requireOriginalShape) {
            viafabricplus_requireOriginalShape = false;
            return;
        }
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            cir.setReturnValue(viafabricplus_hopper_shape_v1_12_2);
        }
    }

    @Inject(method = "getRaycastShape", at = @At("HEAD"), cancellable = true)
    public void injectGetRaycastShape(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            cir.setReturnValue(viafabricplus_inside_shape_v1_12_2);
        }
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        viafabricplus_requireOriginalShape = true;
        return super.getCullingShape(state, world, pos);
    }
}
