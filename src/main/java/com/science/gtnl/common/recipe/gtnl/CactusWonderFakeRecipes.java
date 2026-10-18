package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;
import com.science.gtnl.utils.recipes.metadata.SteamAmountMetadata;

import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class CactusWonderFakeRecipes implements IRecipePool {

    private static final SteamAmountMetadata OFFER_VALUE = SteamAmountMetadata.INSTANCE;
    public RecipeMap<?> CWFR = GTNLRecipeMaps.CactusWonderFakeRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.CactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 2667L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.BlockCactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 30000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.CompressedCactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 337500L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.DoubleCompressedCactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 3796875L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.TripleCompressedCactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 42714844L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.QuadrupleCompressedCactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 480541992L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.QuintupleCompressedCactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 5406097412L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.CactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 5333L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.BlockCactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 60000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.CompressedCactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 675000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.DoubleCompressedCactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 7593750L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.TripleCompressedCactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 85429688L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.QuadrupleCompressedCactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 961083984L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.QuintupleCompressedCactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(32000))
            .metadata(OFFER_VALUE, 10812194824L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

    }
}
