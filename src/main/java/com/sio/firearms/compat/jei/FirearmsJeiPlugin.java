package com.sio.firearms.compat.jei;

import com.sio.firearms.Firearms;
import com.sio.firearms.registry.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class FirearmsJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Firearms.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new MetalPressRecipeCategory(gui),
                new AssemblyBenchRecipeCategory(gui),
                new LatheRecipeCategory(gui),
                new EBFRecipeCategory(gui),
                new EBFStructureCategory(gui),
                new ChemicalMixerRecipeCategory(gui),
                new ChemicalMixerStructureCategory(gui),
                new ElectrolysisRecipeCategory(gui),
                // Semiconductor fabrication machines
                new WaferCuttingMachineRecipeCategory(gui),
                new DepositionChamberRecipeCategory(gui),
                new PlasmaEtcherRecipeCategory(gui),
                new IonImplanterRecipeCategory(gui),
                new MetallizationChamberRecipeCategory(gui),
                new WaferTesterRecipeCategory(gui),
                new DicingSawRecipeCategory(gui),
                new ChipPackagingMachineRecipeCategory(gui),
                new CrystalGrowthRecipeCategory(gui),
                new EuvLithographyRecipeCategory(gui),
                new GasCentrifugeRecipeCategory(gui),
                // Multiblock structure guides
                new RefineryStructureCategory(gui),
                new OilDerrickStructureCategory(gui),
                new NuclearReactorStructureCategory(gui),
                new CoolingTowerStructureCategory(gui),
                new SpentFuelStorageStructureCategory(gui),
                new VehicleGarageStructureCategory(gui),
                new AircraftHangarStructureCategory(gui),
                new VehicleGarageRecipeCategory(gui),
                new HangarRecipeCategory(gui),
                new AcidBathRecipeCategory(gui),
                new HeatTreatmentRecipeCategory(gui)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(MetalPressRecipeCategory.RECIPE_TYPE,    MetalPressJeiRecipe.getAllRecipes());
        registration.addRecipes(AssemblyBenchRecipeCategory.RECIPE_TYPE, AssemblyBenchJeiRecipe.getAllRecipes());
        registration.addRecipes(LatheRecipeCategory.RECIPE_TYPE,         LatheJeiRecipe.getAllRecipes());
        registration.addRecipes(EBFRecipeCategory.RECIPE_TYPE,           EBFJeiRecipe.getAllRecipes());
        registration.addRecipes(EBFStructureCategory.RECIPE_TYPE,        EBFStructureJeiRecipe.getAllRecipes());
        registration.addRecipes(ChemicalMixerRecipeCategory.RECIPE_TYPE,          ChemicalMixerJeiRecipe.getAllRecipes());
        registration.addRecipes(ChemicalMixerStructureCategory.RECIPE_TYPE,       ChemicalMixerStructureJeiRecipe.getAllRecipes());
        registration.addRecipes(ElectrolysisRecipeCategory.RECIPE_TYPE,  ElectrolysisJeiRecipe.getAllRecipes());
        // Semiconductor fabrication machines
        registration.addRecipes(WaferCuttingMachineRecipeCategory.RECIPE_TYPE,  WaferCuttingMachineJeiRecipe.getAllRecipes());
        registration.addRecipes(DepositionChamberRecipeCategory.RECIPE_TYPE,    DepositionChamberJeiRecipe.getAllRecipes());
        registration.addRecipes(PlasmaEtcherRecipeCategory.RECIPE_TYPE,         PlasmaEtcherJeiRecipe.getAllRecipes());
        registration.addRecipes(IonImplanterRecipeCategory.RECIPE_TYPE,         IonImplanterJeiRecipe.getAllRecipes());
        registration.addRecipes(MetallizationChamberRecipeCategory.RECIPE_TYPE, MetallizationChamberJeiRecipe.getAllRecipes());
        registration.addRecipes(WaferTesterRecipeCategory.RECIPE_TYPE,          WaferTesterJeiRecipe.getAllRecipes());
        registration.addRecipes(DicingSawRecipeCategory.RECIPE_TYPE,            DicingSawJeiRecipe.getAllRecipes());
        registration.addRecipes(ChipPackagingMachineRecipeCategory.RECIPE_TYPE, ChipPackagingMachineJeiRecipe.getAllRecipes());
        registration.addRecipes(CrystalGrowthRecipeCategory.RECIPE_TYPE,       CrystalGrowthJeiRecipe.getAllRecipes());
        registration.addRecipes(EuvLithographyRecipeCategory.RECIPE_TYPE,      EuvLithographyJeiRecipe.getAllRecipes());
        registration.addRecipes(GasCentrifugeRecipeCategory.RECIPE_TYPE,      GasCentrifugeJeiRecipe.getAllRecipes());
        // Multiblock structure guides
        registration.addRecipes(RefineryStructureCategory.RECIPE_TYPE,        RefineryStructureJeiRecipe.getAllRecipes());
        registration.addRecipes(OilDerrickStructureCategory.RECIPE_TYPE,      OilDerrickStructureJeiRecipe.getAllRecipes());
        registration.addRecipes(NuclearReactorStructureCategory.RECIPE_TYPE,  NuclearReactorStructureJeiRecipe.getAllRecipes());
        registration.addRecipes(CoolingTowerStructureCategory.RECIPE_TYPE,    CoolingTowerStructureJeiRecipe.getAllRecipes());
        registration.addRecipes(SpentFuelStorageStructureCategory.RECIPE_TYPE, SpentFuelStorageStructureJeiRecipe.getAllRecipes());
        registration.addRecipes(VehicleGarageStructureCategory.RECIPE_TYPE,   VehicleGarageStructureJeiRecipe.getAllRecipes());
        registration.addRecipes(AircraftHangarStructureCategory.RECIPE_TYPE,  AircraftHangarStructureJeiRecipe.getAllRecipes());
        registration.addRecipes(VehicleGarageRecipeCategory.RECIPE_TYPE,      VehicleGarageJeiRecipe.getAllRecipes());
        registration.addRecipes(HangarRecipeCategory.RECIPE_TYPE,             HangarJeiRecipe.getAllRecipes());
        registration.addRecipes(AcidBathRecipeCategory.RECIPE_TYPE,          AcidBathJeiRecipe.getAllRecipes());
        registration.addRecipes(HeatTreatmentRecipeCategory.RECIPE_TYPE,     HeatTreatmentJeiRecipe.getAllRecipes());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(
                com.sio.firearms.screen.FluidPortConfigScreen.class,
                new FluidPortGhostHandler());
        registration.addGhostIngredientHandler(
                com.sio.firearms.screen.FluidPipeConfigScreen.class,
                new FluidPipeGhostHandler());
        registration.addGhostIngredientHandler(
                com.sio.firearms.screen.FluidPipeUnifiedScreen.class,
                new FluidPipeUnifiedGhostHandler());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.METAL_PRESS.get()),          MetalPressRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ASSEMBLY_BENCH.get()),       AssemblyBenchRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.LATHE.get()),                LatheRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.EBF_CONTROLLER.get()),       EBFRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.EBF_CONTROLLER.get()),       EBFStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BLAST_FURNACE_CASING.get()), EBFStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MUFFLER_HATCH.get()),        EBFStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CHEMICAL_MIXER.get()),            ChemicalMixerRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CHEMICAL_MIXER_CONTROLLER.get()), ChemicalMixerRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CHEMICAL_MIXER_CONTROLLER.get()), ChemicalMixerStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CHEMICAL_MIXER_BASE.get()),       ChemicalMixerStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CHEMICAL_MIXER_WALL.get()),       ChemicalMixerStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ELECTROLYSIS_MACHINE.get()), ElectrolysisRecipeCategory.RECIPE_TYPE);
        // Semiconductor fabrication machines
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WAFER_CUTTING_MACHINE.get()),  WaferCuttingMachineRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.DEPOSITION_CHAMBER.get()),    DepositionChamberRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PLASMA_ETCHER.get()),         PlasmaEtcherRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ION_IMPLANTER.get()),         IonImplanterRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.METALLIZATION_CHAMBER.get()), MetallizationChamberRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WAFER_TESTER.get()),          WaferTesterRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.DICING_SAW.get()),            DicingSawRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CHIP_PACKAGING_MACHINE.get()),      ChipPackagingMachineRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CRYSTAL_GROWTH_CONTROLLER.get()),   CrystalGrowthRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.EUV_LITHOGRAPHY_CONTROLLER.get()),  EuvLithographyRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GAS_CENTRIFUGE.get()),             GasCentrifugeRecipeCategory.RECIPE_TYPE);
        // Multiblock structure guides
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.REFINERY_CONTROLLER.get()), RefineryStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.REFINERY_BASE.get()),       RefineryStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.REFINERY_WALL.get()),       RefineryStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.REFINERY_TOP.get()),        RefineryStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.OIL_DERRICK_CONTROLLER.get()), OilDerrickStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.OIL_DERRICK_BASE.get()),       OilDerrickStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.OIL_DERRICK_PILLAR.get()),     OilDerrickStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.REACTOR_CONTROLLER.get()),  NuclearReactorStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.REACTOR_BASE.get()),        NuclearReactorStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.REACTOR_WALL.get()),        NuclearReactorStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.REACTOR_TOP.get()),         NuclearReactorStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COOLING_TOWER_CONTROLLER.get()), CoolingTowerStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COOLING_TOWER_BASE.get()),       CoolingTowerStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COOLING_TOWER_WALL.get()),       CoolingTowerStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COOLING_TOWER_VENT.get()),       CoolingTowerStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SPENT_FUEL_STORAGE_CONTROLLER.get()), SpentFuelStorageStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SPENT_FUEL_STORAGE_BASE.get()),       SpentFuelStorageStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SPENT_FUEL_STORAGE_WALL.get()),       SpentFuelStorageStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GARAGE_CONTROLLER.get()), VehicleGarageStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GARAGE_FLOOR.get()),      VehicleGarageStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GARAGE_WALL.get()),       VehicleGarageStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GARAGE_DOOR.get()),       VehicleGarageStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GARAGE_ROOF.get()),       VehicleGarageStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HANGAR_CONTROLLER.get()), AircraftHangarStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HANGAR_FLOOR.get()),      AircraftHangarStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HANGAR_WALL.get()),       AircraftHangarStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HANGAR_DOOR.get()),       AircraftHangarStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HANGAR_ROOF.get()),       AircraftHangarStructureCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GARAGE_CONTROLLER.get()), VehicleGarageRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HANGAR_CONTROLLER.get()), HangarRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ACID_BATH.get()),              AcidBathRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HEAT_TREATMENT_FURNACE.get()), HeatTreatmentRecipeCategory.RECIPE_TYPE);
    }
}
