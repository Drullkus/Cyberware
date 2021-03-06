package flaxbeard.cyberware.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberware.EnumSlot;
import flaxbeard.cyberware.common.block.BlockCharger;
import flaxbeard.cyberware.common.block.BlockSurgery;
import flaxbeard.cyberware.common.block.BlockSurgeryChamber;
import flaxbeard.cyberware.common.effect.PotionNeuropozyne;
import flaxbeard.cyberware.common.entity.EntityCyberZombie;
import flaxbeard.cyberware.common.integration.botania.BotaniaIntegration;
import flaxbeard.cyberware.common.item.ItemBodyPart;
import flaxbeard.cyberware.common.item.ItemBoneUpgrade;
import flaxbeard.cyberware.common.item.ItemBrainUpgrade;
import flaxbeard.cyberware.common.item.ItemCreativeBattery;
import flaxbeard.cyberware.common.item.ItemCybereyeUpgrade;
import flaxbeard.cyberware.common.item.ItemCybereyes;
import flaxbeard.cyberware.common.item.ItemCyberheart;
import flaxbeard.cyberware.common.item.ItemCyberlimb;
import flaxbeard.cyberware.common.item.ItemCyberware;
import flaxbeard.cyberware.common.item.ItemDenseBattery;
import flaxbeard.cyberware.common.item.ItemExpCapsule;
import flaxbeard.cyberware.common.item.ItemFootUpgrade;
import flaxbeard.cyberware.common.item.ItemHandUpgrade;
import flaxbeard.cyberware.common.item.ItemHeartUpgrade;
import flaxbeard.cyberware.common.item.ItemLegUpgrade;
import flaxbeard.cyberware.common.item.ItemLowerOrgansUpgrade;
import flaxbeard.cyberware.common.item.ItemLungsUpgrade;
import flaxbeard.cyberware.common.item.ItemMuscleUpgrade;
import flaxbeard.cyberware.common.item.ItemNeuropozyne;
import flaxbeard.cyberware.common.item.ItemSkinUpgrade;
import flaxbeard.cyberware.common.item.VanillaWares.SpiderEyeWare;

public class CyberwareContent
{
	public static final int RARE = 10;
	public static final int UNCOMMON = 25;
	public static final int COMMON = 50;
	public static final int VERY_COMMON = 100;

	public static Block surgeryApparatus;
	public static BlockSurgeryChamber surgeryChamber;
	public static Block charger;
	public static Item bodyPart;
	public static ItemCyberware cybereyes;
	public static ItemCyberware cybereyeUpgrades;
	public static ItemCyberware brainUpgrades;
	public static Item expCapsule;
	public static ItemCyberware heartUpgrades;
	public static ItemCyberware cyberheart;
	public static ItemCyberware lungsUpgrades;
	public static ItemCyberware lowerOrgansUpgrades;
	public static ItemCyberware denseBattery;
	public static ItemCyberware skinUpgrades;
	public static ItemCyberware muscleUpgrades;
	public static ItemCyberware boneUpgrades;
	public static ItemCyberware handUpgrades;
	public static ItemCyberware legUpgrades;
	public static ItemCyberware footUpgrades;
	public static ItemCyberware cyberlimbs;
	public static ItemCyberware creativeBattery;
	public static Item neuropozyne;

	public static Potion neuropozyneEffect;

	public static List<Item> items;
	public static List<Block> blocks;
	
	public static List<NumItems> numItems;
	public static List<ZombieItem> zombieItems;


	public static void preInit()
	{
		
		items = new ArrayList<Item>();
		blocks = new ArrayList<Block>();
		
		numItems = new ArrayList<NumItems>();
		numItems.add(new NumItems(50, 4));
		numItems.add(new NumItems(25, 3));
		numItems.add(new NumItems(25, 5));
		numItems.add(new NumItems(15, 6));
		numItems.add(new NumItems(5, 10));

		zombieItems = new ArrayList<ZombieItem>();

		EntityRegistry.registerModEntity(EntityCyberZombie.class, "cyberzombie", 0, Cyberware.INSTANCE, 80, 3, true);
		
		neuropozyneEffect = new PotionNeuropozyne("neuropozyne", false, 0x47453d);

		surgeryApparatus = new BlockSurgery();
		surgeryChamber = new BlockSurgeryChamber();
		charger = new BlockCharger();
		
		neuropozyne = new ItemNeuropozyne("neuropozyne");

		bodyPart = new ItemBodyPart("bodyPart", 
				new EnumSlot[] { EnumSlot.EYES, EnumSlot.CRANIUM, EnumSlot.HEART, EnumSlot.LUNGS, EnumSlot.LOWER_ORGANS, EnumSlot.SKIN, EnumSlot.MUSCLE, EnumSlot.BONE, EnumSlot.ARM, EnumSlot.ARM, EnumSlot.LEG, EnumSlot.LEG },
				new String[] { "eyes", "brain", "heart", "lungs", "stomach", "skin", "muscles", "bones", "armLeft", "armRight", "legLeft", "legRight"});
		
		
		cybereyes = new ItemCybereyes("cybereyes", EnumSlot.EYES);
		cybereyes.setEssenceCost(10);
		cybereyes.setWeights(UNCOMMON);
		
		cybereyeUpgrades = new ItemCybereyeUpgrade("cybereyeUpgrades", EnumSlot.EYES,
				new String[] { "nightVision", "underwaterVision", "hudjack", "targeting", "zoom" });
		cybereyeUpgrades.setEssenceCost(2, 2, 1, 1, 1);
		cybereyeUpgrades.setWeights(UNCOMMON, UNCOMMON, VERY_COMMON, UNCOMMON, UNCOMMON);
		
		CyberwareAPI.linkCyberware(Items.SPIDER_EYE, new SpiderEyeWare());
		
		brainUpgrades = new ItemBrainUpgrade("brainUpgrades", EnumSlot.CRANIUM,
				new String[] { "corticalStack", "enderJammer", "consciousnessTransmitter", "neuralContextualizer", "matrix" });
		brainUpgrades.setEssenceCost(3, 10, 2, 2, 10);
		brainUpgrades.setWeights(RARE, UNCOMMON, UNCOMMON, COMMON, UNCOMMON);
		expCapsule = new ItemExpCapsule("expCapsule");
		
		cyberheart = new ItemCyberheart("cyberheart", EnumSlot.HEART);
		cyberheart.setEssenceCost(5);
		cyberheart.setWeights(COMMON);
		
		denseBattery = new ItemDenseBattery("denseBattery", EnumSlot.LOWER_ORGANS);
		denseBattery.setEssenceCost(15);
		denseBattery.setWeights(RARE);
		
		creativeBattery = new ItemCreativeBattery("creativeBattery", EnumSlot.LOWER_ORGANS);
		creativeBattery.setEssenceCost(0);
		
		heartUpgrades = new ItemHeartUpgrade("heartUpgrades", EnumSlot.HEART,
				new String[] { "defibrillator", "platelets", "medkit", "coupler" });
		heartUpgrades.setEssenceCost(10, 5, 15, 10);
		heartUpgrades.setWeights(COMMON, UNCOMMON, UNCOMMON, VERY_COMMON);
		
		lungsUpgrades = new ItemLungsUpgrade("lungsUpgrades", EnumSlot.LUNGS,
				new String[] { "oxygen", "hyperoxygenation" });
		lungsUpgrades.setEssenceCost(15, 2);
		lungsUpgrades.setWeights(UNCOMMON, COMMON);
		
		lowerOrgansUpgrades = new ItemLowerOrgansUpgrade("lowerOrgansUpgrades", EnumSlot.LOWER_ORGANS,
				new String[] { "liverFilter", "metabolic", "battery", "adrenaline" });
		lowerOrgansUpgrades.setEssenceCost(5, 5, 10, 5);
		lowerOrgansUpgrades.setWeights(UNCOMMON, COMMON, VERY_COMMON, UNCOMMON);
		
		skinUpgrades = new ItemSkinUpgrade("skinUpgrades", EnumSlot.SKIN,
				new String[] { "solarSkin", "subdermalSpikes", "fakeSkin" });
		skinUpgrades.setEssenceCost(15, 12, 0);
		skinUpgrades.setWeights(VERY_COMMON, UNCOMMON, UNCOMMON);
		
		muscleUpgrades = new ItemMuscleUpgrade("muscleUpgrades", EnumSlot.MUSCLE,
				new String[] { "wiredReflexes", "muscleReplacements" });
		muscleUpgrades.setEssenceCost(5, 15);
		muscleUpgrades.setWeights(UNCOMMON, RARE);

		boneUpgrades = new ItemBoneUpgrade("boneUpgrades", EnumSlot.BONE,
				new String[] { "bonelacing", "boneflex" });
		boneUpgrades.setEssenceCost(3, 5);
		boneUpgrades.setWeights(UNCOMMON);
		
		handUpgrades = new ItemHandUpgrade("handUpgrades", EnumSlot.HAND,
				new String[] { "craftHands", "claws", "mining" });
		handUpgrades.setEssenceCost(2, 2, 1);
		handUpgrades.setWeights(RARE, RARE, RARE);
		
		legUpgrades = new ItemLegUpgrade("legUpgrades", EnumSlot.LEG,
				new String[] { "jumpBoost" });
		legUpgrades.setEssenceCost(3);
		legUpgrades.setWeights(UNCOMMON);
		
		footUpgrades = new ItemFootUpgrade("footUpgrades", EnumSlot.FOOT,
				new String[] { "spurs" });
		footUpgrades.setEssenceCost(1);
		footUpgrades.setWeights(UNCOMMON);
		
		cyberlimbs = new ItemCyberlimb("cyberlimbs", 
				new EnumSlot[] { EnumSlot.ARM, EnumSlot.ARM, EnumSlot.LEG, EnumSlot.LEG },
				new String[] { "cyberarmLeft", "cyberarmRight", "cyberlegLeft", "cyberlegRight" });
		cyberlimbs.setEssenceCost(25, 25, 25, 25);
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(surgeryChamber.ib),
				"III",
				"IBI",
				"IDI",
				Character.valueOf('I'), "ingotIron", Character.valueOf('B'), "blockIron", Character.valueOf('D'), new ItemStack(Items.IRON_DOOR)
				));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(charger),
				"IFI",
				"IRI",
				"III",
				Character.valueOf('I'), "ingotIron", Character.valueOf('R'), "blockRedstone", Character.valueOf('F'), new ItemStack(Blocks.IRON_BARS)
				));
		
		if (CyberwareConfig.SURGERY_CRAFTING)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(surgeryApparatus),
					"III",
					"IRI",
					"PSA",
					Character.valueOf('I'), "ingotIron", Character.valueOf('R'), "blockRedstone", Character.valueOf('P'), new ItemStack(Items.DIAMOND_PICKAXE), Character.valueOf('S'), new ItemStack(Items.DIAMOND_SWORD), Character.valueOf('A'), new ItemStack(Items.DIAMOND_AXE)
					));
		}
		
		if (Loader.isModLoaded("Botania"))
		{
			BotaniaIntegration.preInit();
		}
	}
	

	public static class NumItems extends WeightedRandom.Item
	{
		public int num;
		public NumItems(int weight, int num)
		{
			super(weight);
			this.num = num;
		}

		@Override
		public boolean equals(Object target)
		{
			return target instanceof NumItems && num == ((NumItems)target).num;
		}
	}
	
	public static class ZombieItem extends WeightedRandom.Item
	{
		public ItemStack stack;
		public ZombieItem(int weight, ItemStack stack)
		{
			super(weight);
			this.stack = stack;
		}

		@Override
		public boolean equals(Object target)
		{
			if (!(target instanceof ZombieItem)) return false;
			ItemStack stack2 = ((ZombieItem)target).stack;
			return (stack == stack2 || (stack != null && stack2 != null && stack.getItem() == stack2.getItem() && stack.getItemDamage() == stack2.getItemDamage() && stack.stackSize == stack2.stackSize));
		}
	}

}
