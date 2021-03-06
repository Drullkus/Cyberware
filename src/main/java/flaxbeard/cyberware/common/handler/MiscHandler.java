package flaxbeard.cyberware.common.handler;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.mojang.realmsclient.gui.ChatFormatting;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberware;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.misc.CommandClearCyberware;

public class MiscHandler
{
	public static final MiscHandler INSTANCE = new MiscHandler();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@SideOnly(Side.CLIENT)
	public void handleCyberwareTooltip(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();
		if (CyberwareAPI.isCyberware(stack))
		{
			ICyberware ware = CyberwareAPI.getCyberware(stack);
			GameSettings settings = Minecraft.getMinecraft().gameSettings;
			if (settings.isKeyDown(settings.keyBindSneak))
			{
				List<String> info = ware.getInfo(stack);
				if (info != null)
				{
					event.getToolTip().addAll(info);
				}
				
				ItemStack[][] reqs = ware.required(stack);
				if (reqs.length > 0)
				{
					String joined = "";
					for (int i = 0; i < reqs.length; i++)
					{
						String toAdd = "";
						
						for (int j = 0; j < reqs[i].length; j++)
						{
							if (j != 0)
							{
								toAdd += " " + I18n.format("cyberware.tooltip.joinerOr") + " ";
							}
							toAdd += I18n.format(reqs[i][j].getUnlocalizedName() + ".name");
							
						}
						
						if (i != 0)
						{
							joined += I18n.format("cyberware.tooltip.joiner") + " ";
						}
						joined += toAdd;
						
					}
					event.getToolTip().add(ChatFormatting.AQUA + I18n.format("cyberware.tooltip.requires") + " "
							+ joined);
				}
				event.getToolTip().add(ChatFormatting.RED + I18n.format("cyberware.slot." + ware.getSlot(stack).getName()));
			}
			else
			{
				event.getToolTip().add(ChatFormatting.DARK_GRAY + I18n.format("cyberware.tooltip.shiftPrompt"));
			}
		}
	}
	
	@SubscribeEvent
	public void handleNeuropozynePopulation(LootTableLoadEvent event)
	{
		if (event.getName() == LootTableList.CHESTS_SIMPLE_DUNGEON
				|| event.getName() == LootTableList.CHESTS_ABANDONED_MINESHAFT
				|| event.getName() == LootTableList.CHESTS_STRONGHOLD_CROSSING
				|| event.getName() == LootTableList.CHESTS_STRONGHOLD_CORRIDOR
				|| event.getName() == LootTableList.CHESTS_STRONGHOLD_LIBRARY
				|| event.getName() == LootTableList.CHESTS_DESERT_PYRAMID
				|| event.getName() == LootTableList.CHESTS_JUNGLE_TEMPLE)
		{
			LootTable table = event.getTable();
			LootPool main = event.getTable().getPool("main");
			if (main != null)
			{
				LootCondition[] lc = new LootCondition[0];
				LootFunction[] lf = new LootFunction[] { new SetCount(lc, new RandomValueRange(16F, 64F)) };
				main.addEntry(new LootEntryItem(CyberwareContent.neuropozyne, 15, 0, lf, lc, "cyberware:neuropozyne"));
			}
		}
		
		if (event.getName() == LootTableList.CHESTS_NETHER_BRIDGE)
		{
			LootTable table = event.getTable();
			LootPool main = event.getTable().getPool("main");
			if (main != null)
			{
				LootCondition[] lc = new LootCondition[0];
				LootFunction[] lf = new LootFunction[0];
				main.addEntry(new LootEntryItem(Item.getItemFromBlock(CyberwareContent.surgeryApparatus), 15, 0, lf, lc, "cyberware:surgeryApparatus"));
			}
		}
	}
}
