package com.science.gtnl.common.machine.multiblock;

import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;

import gregtech.api.casing.Casings;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.pollution.Pollution;
import gregtech.common.pollution.PollutionConfig;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class BrickedBlastFurnace extends SteamMultiMachineBase<BrickedBlastFurnace> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    // Layers from top to bottom, rows from front to back.
    private static final String[][] shape = new String[][] { { "     ", " BBB ", " B-B ", " BBB ", "     " },
        { "     ", " BBB ", " B-B ", " BBB ", "     " }, { "     ", " BBB ", " B-B ", " BBB ", "     " },
        { "CCCCC", "CCCCC", "CC-CC", "CCCCC", "CCCCC" }, { "CBBBC", "B---B", "B---B", "B---B", "CBBBC" },
        { "CBBBC", "B---B", "B---B", "B---B", "CBBBC" }, { "CB~BC", "BBBBB", "BBBBB", "BBBBB", "CBBBC" },
        { "CDDDC", "DAAAD", "DAAAD", "DAAAD", "CDDDC" } };
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 6;
    private static final int CHIMNEY_DEPTH = 2;
    private static final int DEPTH_OFF_SET = 0;
    private static final int MIN_BRONZE_PLATED_BRICKS = 8;
    public static final int MAX_PARALLEL = 4;
    public static final double SPEED_BONUS = 2.0;

    public BrickedBlastFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public BrickedBlastFurnace(String aName) {
        super(aName);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.bricked_blast_furnace.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new BrickedBlastFurnace(this.mName);
    }

    @Override
    public IStructureDefinition<BrickedBlastFurnace> getStructureDefinition() {
        return StructureDefinition.<BrickedBlastFurnace>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', Casings.BronzeFireboxCasing.asElement())
            .addElement('B', Casings.Firebricks.asElement())
            .addElement('C', Casings.BronzePlatedBricks.asElement())
            .addElement(
                'D',
                GTStructureUtility.buildHatchAdder(BrickedBlastFurnace.class)
                    .casingIndex(Casings.BronzePlatedBricks.getTextureId())
                    .hint(1)
                    .atLeast(
                        SteamHatchElement.InputBus_Steam,
                        HatchElement.InputBus,
                        SteamHatchElement.OutputBus_Steam,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance)
                    .buildAndChain(
                        StructureUtility.onElementPass(x -> ++x.mCountCasing, Casings.BronzePlatedBricks.asElement())))
            .build();
    }

    @Override
    public int getCasingTextureID() {
        return Casings.Firebricks.getTextureId();
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        checkCasingMin(errors, mCountCasing, MIN_BRONZE_PLATED_BRICKS);
    }

    @Override
    public int getMaxParallelRecipes() {
        return MAX_PARALLEL;
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() / SPEED_BONUS;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        // Half the pollution of the four Bricked Blast Furnaces it replaces.
        if (aBaseMetaTileEntity.isServerSide() && aBaseMetaTileEntity.isActive() && aTick % 20 == 0) {
            Pollution.addPollution(
                aBaseMetaTileEntity,
                PollutionConfig.pollutionPrimitveBlastFurnacePerSecond * MAX_PARALLEL / 2);
        }
        // Smoke rises through the chimney like on a Bricked Blast Furnace.
        if (aBaseMetaTileEntity.isClientSide() && aBaseMetaTileEntity.isActive()) {
            ForgeDirection back = aBaseMetaTileEntity.getBackFacing();
            Random random = aBaseMetaTileEntity.getWorld().rand;
            aBaseMetaTileEntity.getWorld()
                .spawnParticle(
                    "largesmoke",
                    aBaseMetaTileEntity.getXCoord() + back.offsetX * CHIMNEY_DEPTH + random.nextFloat(),
                    aBaseMetaTileEntity.getYCoord() + VERTICAL_OFF_SET + random.nextFloat(),
                    aBaseMetaTileEntity.getZCoord() + back.offsetZ * CHIMNEY_DEPTH + random.nextFloat(),
                    0.0,
                    0.3,
                    0.0);
        }
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.primitiveBlastRecipes;
    }

    @Override
    public boolean getPerfectOC() {
        return false;
    }

    @Override
    public int getTierRecipes() {
        return 14;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.MACHINE_CASING_BRICKEDBLASTFURNACE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.MACHINE_CASING_BRICKEDBLASTFURNACE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.MACHINE_CASING_BRICKEDBLASTFURNACE_INACTIVE)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.bricked_blast_furnace.recipe_type");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.bricked_blast_furnace.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.bricked_blast_furnace.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.bricked_blast_furnace.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.bricked_blast_furnace.tooltip.2"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.bricked_blast_furnace.tooltip.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.bricked_blast_furnace.tooltip.4"))
            .beginStructureBlock(5, 8, 5, true)
            .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.bricked_blast_furnace.structure.0"))
            .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.bricked_blast_furnace.structure.1"))
            .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.bricked_blast_furnace.structure.2"))
            .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.bricked_blast_furnace.structure.3"))
            .addStructureInfo(StatCollector.translateToLocal("gtnl.machine.bricked_blast_furnace.structure.4"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void checkMaintenance() {}

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    @Override
    public boolean supportsSteamOC() {
        return false;
    }

    @Override
    public boolean supportsSteamCapacityUI() {
        return false;
    }
}
