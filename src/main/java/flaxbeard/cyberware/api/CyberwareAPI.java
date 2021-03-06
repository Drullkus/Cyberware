package flaxbeard.cyberware.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.oredict.OreDictionary;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.CyberwareSyncPacket;

public final class CyberwareAPI
{
	@CapabilityInject(ICyberwareUserData.class)
	public static final Capability<ICyberwareUserData> CYBERWARE_CAPABILITY = null;
	
	public static Map<ItemStack, ICyberware> linkedWare = new HashMap<ItemStack, ICyberware>();
	
	
	/**
	 * Links an ItemStack to an instance of ICyberware. This option is generally worse than
	 * implementing ICyberware in your Item, but if you don't have access to the Item it's the
	 * best option. This version of the method links a specific meta value.
	 * 
	 * @param stack	The ItemStack to link
	 * @param link	An instance of ICyberware to link it to
	 */
	public static void linkCyberware(ItemStack stack, ICyberware link)
	{
		if (stack == null) return;
		
		ItemStack key = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
		linkedWare.put(key, link);
	}
	
	/**
	 * Links an Item to an instance of ICyberware. This option is generally worse than
	 * implementing ICyberware in your Item, but if you don't have access to the Item it's the
	 * best option. This version of the method links all meta values.
	 * 
	 * @param item	The Item to link
	 * @param link	An instance of ICyberware to link it to
	 */
	public static void linkCyberware(Item item, ICyberware link)
	{
		if (item == null) return;
		
		ItemStack key = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
		linkedWare.put(key, link);
	}
	
	/**
	 * Determines if the inputted item stack is Cyberware. This means its item either
	 * implements ICyberware or is linked to one (in the case of vanilla items)
	 * 
	 * @param stack	The ItemStack to test
	 * @return		If the stack is valid Cyberware
	 */
	public static boolean isCyberware(ItemStack stack)
	{
		return stack != null && (stack.getItem() instanceof ICyberware || getLinkedWare(stack) != null);
	}
	
	/**
	 * Returns an instance of ICyberware linked with an itemstack, usually
	 * the item which extends ICyberware, though it may be a standalone
	 * ICyberware-implementing object
	 * 
	 * @param stack	The ItemStack, from which the linked ICyberware is found
	 * @return		The linked instance of ICyberware
	 */
	public static ICyberware getCyberware(ItemStack stack)
	{
		if (stack != null)
		{
			if (stack.getItem() instanceof ICyberware)
			{
				return (ICyberware) stack.getItem();
			}
			else if (getLinkedWare(stack) != null)
			{
				return getLinkedWare(stack);
			}
		}
		
		throw new RuntimeException("Cannot call getCyberware on a non-cyberware item!");
	}
	
	private static ICyberware getLinkedWare(ItemStack stack)
	{
		if (stack == null) return null;
		
		ItemStack test = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
		ICyberware result = getWareFromKey(test);
		if (result != null)
		{
			return result;
		}
		
		ItemStack testGeneric = new ItemStack(stack.getItem(), 1, OreDictionary.WILDCARD_VALUE);
		result = getWareFromKey(testGeneric);
		if (result != null)
		{
			return result;
		}
		
		return null;
	}
	
	private static ICyberware getWareFromKey(ItemStack key)
	{
		for (Entry<ItemStack, ICyberware> entry : linkedWare.entrySet())
		{
			ItemStack entryKey = entry.getKey();
			if (key.getItem() == entryKey.getItem() && key.getItemDamage() == entryKey.getItemDamage())
			{
				return entry.getValue();
			}
		}
		return null;
	}
	
	/**
	 * A shortcut method to determine if the entity that is inputted
	 * has ICyberwareUserData. Works with null entites.
	 * 
	 * @param targetEntity	The entity to test
	 * @return				If the entity has ICyberwareUserData
	 */
	public static boolean hasCapability(@Nullable Entity targetEntity)
	{
		if (targetEntity == null) return false;
		return targetEntity.hasCapability(CYBERWARE_CAPABILITY, EnumFacing.EAST);
	}
	
	/**
	 * Assistant method to hasCapability. A shortcut to get you the ICyberwareUserData
	 * of a specific entity. Note that you must verify if it has the capability first.
	 * 
	 * @param targetEntity	The entity whose ICyberwareUserData you want
	 * @return				The ICyberwareUserData associated with the entity
	 */
	public static ICyberwareUserData getCapability(Entity targetEntity)
	{
		return targetEntity.getCapability(CYBERWARE_CAPABILITY, EnumFacing.EAST);
	}
	
	/**
	 * A shortcut method for event handlers and the like to quickly tell if an entity
	 * has a piece of Cyberware installed. Can handle null entites and entities without
	 * ICyberwareUserData.
	 * 
	 * @param targetEntity	The entity you want to check
	 * @param stack			The Cyberware you want to check for
	 * @return				If the entity has the Cyberware
	 */
	public static boolean isCyberwareInstalled(@Nullable Entity targetEntity, ItemStack stack)
	{
		if (!hasCapability(targetEntity)) return false;
		
		ICyberwareUserData cyberware = getCapability(targetEntity);
		return cyberware.isCyberwareInstalled(stack);
	}
	
	/**
	 * A shortcut method for event handlers and the like to quickly determine what level of
	 * Cyberware is installed. Returns 0 if none. Can handle null entites and entities without
	 * ICyberwareUserData.
	 * 
	 * @param targetEntity	The entity you want to check
	 * @param stack			The Cyberware you want to check for
	 * @return				If the entity has the Cyberware, the level, or 0 if not
	 */
	public static int getCyberwareRank(@Nullable Entity targetEntity, ItemStack stack)
	{
		if (!hasCapability(targetEntity)) return 0;
		
		ICyberwareUserData cyberware = getCapability(targetEntity);
		return cyberware.getCyberwareRank(stack);
	}
	
	/**
	 * A shortcut method for event handlers and the like to get the itemstack for a piece
	 * of cyberware. Useful for NBT data. Can handle null entites and entities without
	 * ICyberwareUserData.
	 * 
	 * @param targetEntity	The entity you want to check
	 * @param stack			The Cyberware you want to check for
	 * @return				The ItemStack found, or null if none
	 */
	public static ItemStack getCyberware(@Nullable Entity targetEntity, ItemStack stack)
	{
		if (!hasCapability(targetEntity)) return null;
		
		ICyberwareUserData cyberware = getCapability(targetEntity);
		return cyberware.getCyberware(stack);
	}
	
	public static void updateData(Entity targetEntity)
	{
		if (!targetEntity.worldObj.isRemote)
		{
			WorldServer world = (WorldServer) targetEntity.worldObj;
			
			NBTTagCompound nbt = CyberwareAPI.getCapability(targetEntity).serializeNBT();
			
			if (targetEntity instanceof EntityPlayer)
			{
				CyberwarePacketHandler.INSTANCE.sendTo(new CyberwareSyncPacket(nbt, targetEntity.getEntityId()), (EntityPlayerMP) targetEntity);
				//System.out.println("Sent data for player " + ((EntityPlayer) targetEntity).getName() + " to that player's client");
			}

			for (EntityPlayer trackingPlayer : world.getEntityTracker().getTrackingPlayers(targetEntity))
			{
				CyberwarePacketHandler.INSTANCE.sendTo(new CyberwareSyncPacket(nbt, targetEntity.getEntityId()), (EntityPlayerMP) trackingPlayer);
				
				if (targetEntity instanceof EntityPlayer)
				{
					//System.out.println("Sent data for player " + ((EntityPlayer) targetEntity).getName() + " to player " + trackingPlayer.getName());
				}
			}
		}

	}
}
