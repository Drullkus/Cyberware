package flaxbeard.cyberware.common.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.lib.LibConstants;
import flaxbeard.cyberware.common.network.CyberwarePacketHandler;
import flaxbeard.cyberware.common.network.DodgePacket;
import flaxbeard.cyberware.common.network.ParticlePacket;

public class ItemHeartUpgrade extends ItemCyberware
{

	public ItemHeartUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}
	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		return other.getItem() == CyberwareContent.cyberheart && (stack.getItemDamage() == 0 || stack.getItemDamage() == 3);
	}
	
	@SubscribeEvent
	public void handleDeath(LivingDeathEvent event)
	{

		EntityLivingBase e = event.getEntityLiving();
		ItemStack test = new ItemStack(this, 1, 0);
		if (CyberwareAPI.isCyberwareInstalled(e, test) && !event.isCanceled())
		{
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(e);
			if (cyberware.usePower(test, this.getPowerConsumption(test), false))
			{
				ItemStack[] items = cyberware.getInstalledCyberware(EnumSlot.HEART);
				ItemStack[] itemsNew = items.clone();
				for (int i = 0; i < items.length; i++)
				{
					ItemStack item = items[i];
					if (item != null && item.getItem() == this && item.getItemDamage() == 0)
					{
						itemsNew[i] = null;
						break;
					}
				}
				cyberware.setInstalledCyberware(e, EnumSlot.HEART, itemsNew);
				cyberware.updateCapacity();
				if (!e.worldObj.isRemote)
				{
					CyberwareAPI.updateData(e);
				}
				e.setHealth(e.getMaxHealth() / 3F);
				event.setCanceled(true);
			}
		}
	}
	
	private static Map<EntityLivingBase, Integer> timesPlatelets = new HashMap<EntityLivingBase, Integer>();

	@SubscribeEvent
	public void handleLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		
		ItemStack test = new ItemStack(this, 1, 2);
		if (e.ticksExisted % 20 == 0 && CyberwareAPI.isCyberwareInstalled(e, test))
		{
			isStemWorking.put(e, CyberwareAPI.getCapability(e).usePower(test, getPowerConsumption(test)));
		}
		
		
		test = new ItemStack(this, 1, 1);
		if (e.ticksExisted % 20 == 0 && CyberwareAPI.isCyberwareInstalled(e, test))
		{
			isPlateletWorking.put(e, CyberwareAPI.getCapability(e).usePower(test, getPowerConsumption(test)));
		}
		if (isPlateletWorking(e) && CyberwareAPI.isCyberwareInstalled(e, test))
		{
			if (e.getHealth() >= e.getMaxHealth() * .8F && e.getHealth() != e.getMaxHealth())
			{
				int t = getPlateletTime(e);
				if (t >= 40)
				{
					timesPlatelets.put(e, e.ticksExisted);
					e.heal(1);
				}
			}
			else
			{
				timesPlatelets.put(e, e.ticksExisted);
			}
		}
		else
		{
			if (timesPlatelets.containsKey(e))
			{
				timesPlatelets.remove(e);
			}
		}
		
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 2)))
		{
			if (isStemWorking(e))
			{
				int t = getMedkitTime(e);
				if (t >= 100 && damageMedkit.get(e) > 0F)
				{
					CyberwarePacketHandler.INSTANCE.sendToAllAround(new ParticlePacket(0, (float) e.posX, (float) e.posY + e.height / 2F, (float) e.posZ), 
							new TargetPoint(e.worldObj.provider.getDimension(), e.posX, e.posY, e.posZ, 20));

					e.heal(damageMedkit.get(e));
					timesMedkit.put(e, 0);
					damageMedkit.put(e, 0F);
				}
			}

		}
		else
		{
			if (timesMedkit.containsKey(e))
			{
				//timesMedkit.remove(e);
				//damageMedkit.remove(e);
			}
		}
	}
	
	private static Map<EntityLivingBase, Boolean> isPlateletWorking = new HashMap<EntityLivingBase, Boolean>();
	
	private boolean isPlateletWorking(EntityLivingBase e)
	{
		if (!isPlateletWorking.containsKey(e))
		{
			isPlateletWorking.put(e, false);
		}
		
		return isPlateletWorking.get(e);
	}
	
	private static Map<EntityLivingBase, Boolean> isStemWorking = new HashMap<EntityLivingBase, Boolean>();
	
	private boolean isStemWorking(EntityLivingBase e)
	{
		if (!isStemWorking.containsKey(e))
		{
			isStemWorking.put(e, false);
		}
		
		return isStemWorking.get(e);
	}
	
	
	private static Map<EntityLivingBase, Integer> timesMedkit = new HashMap<EntityLivingBase, Integer>();
	private static Map<EntityLivingBase, Float> damageMedkit = new HashMap<EntityLivingBase, Float>();
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void handleHurt(LivingHurtEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		if (!event.isCanceled() && CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 2)))
		{
			float damageAmount = event.getAmount();
			DamageSource damageSrc = event.getSource();

			damageAmount = applyArmorCalculations(e, damageSrc, damageAmount);
			damageAmount = applyPotionDamageCalculations(e, damageSrc, damageAmount);
			damageAmount = Math.max(damageAmount - e.getAbsorptionAmount(), 0.0F);
			
			damageMedkit.put(e, damageAmount);
			timesMedkit.put(e, e.ticksExisted);
		}
	}
	
	// Stolen from EntityLivingBase
    protected float applyArmorCalculations(EntityLivingBase e, DamageSource source, float damage)
    {
        if (!source.isUnblockable())
        {
            damage = CombatRules.getDamageAfterAbsorb(damage, (float)e.getTotalArmorValue(), (float)e.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        }

        return damage;
    }
	
	// Stolen from EntityLivingBase
	protected float applyPotionDamageCalculations(EntityLivingBase e, DamageSource source, float damage)
	{
		if (source.isDamageAbsolute())
		{
			return damage;
		}
		else
		{
			if (e.isPotionActive(MobEffects.RESISTANCE) && source != DamageSource.outOfWorld)
			{
				int i = (e.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
				int j = 25 - i;
				float f = damage * (float)j;
				damage = f / 25.0F;
			}

			if (damage <= 0.0F)
			{
				return 0.0F;
			}
			else
			{
				int k = EnchantmentHelper.getEnchantmentModifierDamage(e.getArmorInventoryList(), source);

				if (k > 0)
				{
					damage = CombatRules.getDamageAfterMagicAbsorb(damage, (float)k);
				}

				return damage;
			}
		}
	}
	
	private int getPlateletTime(EntityLivingBase e)
	{
		if (!timesPlatelets.containsKey(e))
		{
			timesPlatelets.put(e, e.ticksExisted);
		}
		return e.ticksExisted - timesPlatelets.get(e);
	}
	
	private int getMedkitTime(EntityLivingBase e)
	{
		if (!timesMedkit.containsKey(e))
		{
			timesMedkit.put(e, e.ticksExisted);
			damageMedkit.put(e, 0F);
		}
		return e.ticksExisted - timesMedkit.get(e);
	}
	
	@SubscribeEvent
	public void power(LivingUpdateEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		ItemStack test = new ItemStack(this, 1, 3);
		if (e.ticksExisted % 20 == 0 && CyberwareAPI.isCyberwareInstalled(e, test))
		{
			CyberwareAPI.getCapability(e).addPower(getPowerProduction(test), test);
		}

	}
	
	@Override
	public int getPowerConsumption(ItemStack stack)
	{
		return stack.getItemDamage() == 0 ? LibConstants.DEFIBRILLATOR_CONSUMPTION :
			stack.getItemDamage() == 1 ? LibConstants.PLATELET_CONSUMPTION :
			stack.getItemDamage() == 2 ? LibConstants.STEMCELL_CONSUMPTION : 0;
	}
	
	@Override
	public int getCapacity(ItemStack stack)
	{
		return stack.getItemDamage() == 0 ? LibConstants.DEFIBRILLATOR_CONSUMPTION: 0;
	}
	
	@Override
	public boolean hasCustomPowerMessage(ItemStack stack)
	{
		return stack.getItemDamage() == 0 ? true : false;
	}
	
	@Override
	public int getPowerProduction(ItemStack stack)
	{
		return stack.getItemDamage() == 3 ? LibConstants.COUPLER_PRODUCTION + 1 : 0;
	}

}
