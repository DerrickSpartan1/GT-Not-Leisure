package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gregtech.common.tileentities.machines.multi.steam.MTESteamAlloySmelter;

@Mixin(value = MTESteamAlloySmelter.class, remap = false)
public class MixinMTESteamAlloySmelter {

    @Shadow
    private int tierMachine;

    // The Steam Fuser replaces the Large Steam Alloy Smelter in this fork: steel casings count as MV, so only a
    // high pressure Fuser can make Stronze. Bronze casings stay at LV.
    @Inject(method = "getTierRecipes", at = @At("HEAD"), cancellable = true)
    private void gtnl$steelRunsMV(CallbackInfoReturnable<Integer> cir) {
        if (tierMachine == 2) cir.setReturnValue(2);
    }
}
