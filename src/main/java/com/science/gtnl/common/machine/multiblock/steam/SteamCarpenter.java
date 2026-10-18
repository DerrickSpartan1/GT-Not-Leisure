package com.science.gtnl.common.machine.multiblock.steam;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.BlockIcons;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.objects.ItemData;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class SteamCarpenter extends SteamMultiMachineBase<SteamCarpenter> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String SC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/steam_carpenter";
    private static final String[][] shape = StructureUtils.readStructureFromFile(SC_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    public static final String SAW_BLADE_WEAR_TAG = "GTNLSawBladeWear";
    public static final int MIN_SAW_BLADE_WEAR = 32;
    public static final CheckRecipeResult NO_SAW_BLADE = SimpleCheckRecipeResult.ofFailure("no_saw_blade");

    public SteamCarpenter(String aName) {
        super(aName);
    }

    public SteamCarpenter(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.steam_carpenter.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamCarpenter(this.mName);
    }

    @Override
    public IStructureDefinition<SteamCarpenter> getStructureDefinition() {
        return StructureDefinition.<SteamCarpenter>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                StructureUtility.ofChain(
                    buildSteamWirelessInput(SteamCarpenter.class).casingIndex(10)
                        .hint(1)
                        .build(),
                    buildSteamBigInput(SteamCarpenter.class).casingIndex(10)
                        .hint(1)
                        .build(),
                    buildSteamInput(SteamCarpenter.class).casingIndex(10)
                        .hint(1)
                        .build(),
                    GTStructureUtility.buildHatchAdder(SteamCarpenter.class)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            HatchElement.InputBus,
                            SteamHatchElement.OutputBus_Steam,
                            HatchElement.OutputBus,
                            HatchElement.Maintenance)
                        .casingIndex(10)
                        .hint(1)
                        .buildAndChain(),
                    Casings.BronzePlatedBricks.asElement()))
            .addElement('B', Casings.BronzeGearBoxCasing.asElement())
            .addElement('C', GTStructureUtility.ofFrame(Materials.Wood))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) {
            return;
        }
        checkHatch(errors);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.SteamCarpenterRecipes;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    /** The blade shown in NEI. Any Buzzsaw Blade works in the machine. */
    public static ItemStack getDisplaySawBlade() {
        ItemStack blade = GTOreDictUnificator.get(OrePrefixes.toolHeadBuzzSaw, Materials.Bronze, 1L);
        return blade != null ? blade : GTOreDictUnificator.get(OrePrefixes.toolHeadBuzzSaw, Materials.Iron, 1L);
    }

    public static boolean isSawBlade(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return false;
        ItemData data = GTOreDictUnificator.getAssociation(stack);
        return data != null && data.mPrefix == OrePrefixes.toolHeadBuzzSaw;
    }

    /** How many cuts a blade lasts, taken from the tool durability of its material. */
    public static int getSawBladeMaxWear(ItemStack stack) {
        ItemData data = GTOreDictUnificator.getAssociation(stack);
        if (data == null || data.mMaterial == null || data.mMaterial.mMaterial == null) return MIN_SAW_BLADE_WEAR;
        return (int) Math.max(MIN_SAW_BLADE_WEAR, Math.min(Integer.MAX_VALUE, data.mMaterial.mMaterial.mDurability));
    }

    public static int getSawBladeWear(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger(SAW_BLADE_WEAR_TAG);
    }

    public static boolean needsSawBlade(GTRecipe recipe) {
        return recipe.mSpecialItems instanceof ItemStack stack && isSawBlade(stack);
    }

    public static ItemStack findSawBlade(SteamMultiMachineBase<?> machine) {
        for (ItemStack stack : machine.getStoredInputs()) {
            if (isSawBlade(stack)) return stack;
        }
        return null;
    }

    /** Wears the blade by one point per cut. A stack of blades wears its top blade first. */
    public static void wearSawBlade(SteamMultiMachineBase<?> machine, int cuts) {
        while (cuts > 0) {
            ItemStack blade = findSawBlade(machine);
            if (blade == null) return;
            int maxWear = getSawBladeMaxWear(blade);
            int wear = getSawBladeWear(blade);
            int used = Math.min(cuts, maxWear - wear);
            cuts -= used;
            wear += used;
            if (wear >= maxWear) {
                blade.stackSize--;
                wear = 0;
            }
            if (wear > 0) {
                if (blade.getTagCompound() == null) blade.setTagCompound(new NBTTagCompound());
                blade.getTagCompound()
                    .setInteger(SAW_BLADE_WEAR_TAG, wear);
            } else if (blade.getTagCompound() != null) {
                blade.getTagCompound()
                    .removeTag(SAW_BLADE_WEAR_TAG);
                if (blade.getTagCompound()
                    .hasNoTags()) blade.setTagCompound(null);
            }
        }
        machine.updateSlots();
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @Override
            public @NotNull GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(configSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setPerfectOC(getPerfectOC())
                    .setMaxTierSkips(getMaxTierSkip())
                    .setMaxOverclocks(getMaxOverclocks());
            }

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (needsSawBlade(recipe) && findSawBlade(SteamCarpenter.this) == null) return NO_SAW_BLADE;
                return super.validateRecipe(recipe);
            }

            @Override
            public CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                if (needsSawBlade(recipe)) wearSawBlade(SteamCarpenter.this, Math.max(1, calculatedParallels));
                return super.onRecipeStart(recipe);
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) {
                return new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)),
                    TextureFactory.builder()
                        .addIcon(BlockIcons.OVERLAY_FRONT_STEAM_CARPENTER_ACTIVE)
                        .extFacing()
                        .build() };
            }
            return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_STEAM_CARPENTER)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons
            .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 10)) };
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.steam_carpenter.recipe_type");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(getMachineType())
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_carpenter.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_carpenter.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_carpenter.tooltip.1"))
            .beginStructureBlock(3, 3, 3, true)
            .toolTipFinisher();
        return tt;
    }
}
