package net.minecraft.minechem;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.BaseMod;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntityChest;
import net.minecraft.src.World;
import net.minecraft.src.mod_Minechem;

public class ItemTestTube extends Item {
	
	public static int dmgIdStart = 200000;
	public static int halfLifeTicks = 50;
	private static int elementIcon = ModLoader.addOverride("/gui/items.png", "/minechem/testtube_full.png");
	private static int moleculeIcon = ModLoader.addOverride("/gui/items.png", "/minechem/testtube_molecule.png");
	private Random random;
	
	public ItemTestTube(int i) {
		super(i);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setContainerItem(mod_Minechem.itemTesttubeEmpty);
		random = new Random();
	}
	
	@Override
	public int getIconFromDamage(int i) {
		if(i == 0)
			return moleculeIcon;
		else
			return elementIcon;
	}
	
	@Override
	public void onCreated(ItemStack itemstack, World world,
			EntityPlayer entityplayer) {
		super.onCreated(itemstack, world, entityplayer);
	}
	
	public void tryDecayFromChest(ItemStack itemstack, World world, Entity entity, TileEntityChest chest, int slotnumber) {
		ItemStack decayedStack = tryDecay(itemstack, world, entity);
		emitRadiation(itemstack, world, entity, 1500);
		if(decayedStack != null) {
			chest.setInventorySlotContents(slotnumber, decayedStack);
		}
	}
	
	public void tryDecayFromInventory(ItemStack itemstack, World world, Entity entity, int slotnumber) {
		ItemStack decayedStack = tryDecay(itemstack, world, entity);
		emitRadiation(itemstack, world, entity, 1000);
		if(decayedStack != null) {
			((EntityPlayer)entity).inventory.setInventorySlotContents(slotnumber, decayedStack);
		}
	}
	
	public void emitRadiation(ItemStack itemstack, World world, Entity entity, int chance) {
		chance -= (itemstack.getItemDamage() - 82) * 14;
		if(random.nextInt(chance) != 0)
			return;
		
		EntityPlayer player = (EntityPlayer)entity;
		
		// Get all entities within a 50 block radius around the player.
		AxisAlignedBB playerBox = player.boundingBox;
		playerBox = playerBox.expand(10, 10, 10);
		List entitiesInRange = world.getEntitiesWithinAABBExcludingEntity(player, playerBox);
		// Lower health for living entities.
		for(int i1 = 0; i1 < entitiesInRange.size(); i1++){
			Entity entity1 = (Entity)entitiesInRange.get(i1);
			if(entity1 instanceof EntityLiving) {
				EntityLiving entityLiving = (EntityLiving)entity1;
				entityLiving.setEntityHealth( entityLiving.getEntityHealth() - (1+random.nextInt(6)));
			}
		}
		
		// Check if player has lead armour.
		Minecraft minecraft = ModLoader.getMinecraftInstance();
		ItemStack boots 	= minecraft.thePlayer.inventory.armorInventory[0];
		ItemStack leggings 	= minecraft.thePlayer.inventory.armorInventory[1];
		ItemStack torso 	= minecraft.thePlayer.inventory.armorInventory[2];
		ItemStack helmet 	= minecraft.thePlayer.inventory.armorInventory[3];
		
		if(boots != null && leggings != null && torso != null && helmet != null
				&& boots.itemID == mod_Minechem.leadBoots.shiftedIndex && leggings.itemID == mod_Minechem.leadLeggings.shiftedIndex
				&& torso.itemID == mod_Minechem.leadTorso.shiftedIndex && helmet.itemID == mod_Minechem.leadHelmet.shiftedIndex)
		{
			// Player is sheilded from radiation.
		} else {
			// Harm the player.
			int h = 1+random.nextInt(2);
			player.setEntityHealth( player.getEntityHealth() - (h) );
			player.addChatMessage("Take Health: "+h);
		}
	}
	
	public ItemStack tryDecay(ItemStack itemstack, World world, Entity entity) {
		NBTTagCompound tag = itemstack.getTagCompound();
		if(tag != null && tag.getInteger("decaytime") != -1) {
			EntityPlayer player = (EntityPlayer)entity;
			int newDecayTime = tag.getInteger("decaytime") - 1;
			((ItemGeigerCounter)mod_Minechem.geigerCounter).onRadiationTick(world, itemstack.getItemDamage());
			
			// Decay and cause radiation poisoning.
			if(newDecayTime == 0){

				
				
				// Decay into lower element.
				int newAtomicNumber = itemstack.getItemDamage() - 1;
				int atoms = tag.getInteger("atoms");
				itemstack = new Molecule(newAtomicNumber, atoms).stack;
				
				return itemstack;
			} else {
				tag.setInteger("decaytime", newDecayTime);
			}
		}
		
		return null;
	}
	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {		
		tryDecayFromInventory(itemstack, world, entity, i);
    }
	
	//178 == 1;
	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		if(itemstack.getItemDamage() == 0)
		{
			NBTTagCompound tagCompound = itemstack.getTagCompound();
			if(tagCompound != null){
				String fullChemicalName = tagCompound.getString("fullChemicalName");
				
				if(!fullChemicalName.equals("")) {
					fullChemicalName = fullChemicalName.substring(0,1).toUpperCase() + fullChemicalName.substring(1);
					return fullChemicalName;
				}
			}
			return "Unknown";
		}
		else
		{
			String fullName = elements[itemstack.getItemDamage()][1];
			fullName = fullName.substring(0,1).toUpperCase() + fullName.substring(1);
			if(itemstack.getItemDamage() > 82)
				return "�a" + fullName;
			else
				return fullName;
		}
	}
	
	@Override
	public void addInformation(ItemStack itemstack, List list) {

		if(itemstack.getItemDamage() == 0)
		{
			NBTTagCompound tagCompound = itemstack.getTagCompound();
			if(tagCompound != null){
				String chemicalName = tagCompound.getString("chemicalname");
				int atoms = tagCompound.getInteger("atoms");
				list.add( Util.convertNumbersToSuperscript(chemicalName) );
			} else {
				list.add("Unknown");
			}
		}
		else
		{
			NBTTagCompound tagCompound = itemstack.getTagCompound();
			String chemicalName = elements[itemstack.getItemDamage()][0];
			if(tagCompound != null){
				int atoms = tagCompound.getInteger("atoms");
				if(atoms > 1)
					list.add( Util.convertNumbersToSuperscript( chemicalName + atoms ) );
				else
					list.add( chemicalName );
			} else {
				list.add( chemicalName );
			}
		}
	}

	public static String getFormula(ItemStack itemstack) {
		return elements[itemstack.getItemDamage()][0];
	}
	
	public static String[][] elements = {
		{"Null", "Null"},
		{"H", "hydrogen"},
		{"He", "helium"},
		{"Li", "lithium"},
		{"Be", "beryllium"},
		{"B", "Boron"},
		{"C", "carbon"},
		{"N", "nitrogen"},
		{"O", "oxygen"},
		{"F", "flourine"},
		{"Ne", "neon"},
		{"Na", "sodium"},
		{"Mg", "magnesium"},
		{"Al", "aluminium"},
		{"Si", "silicon"},
		{"P", "phosphorus"},
		{"S", "sulfur"},
		{"Cl", "chlorine"},
		{"Ar", "argon"},
		{"K", "potassium"},
		{"Ca", "calcium"},
		{"Sc", "scandium"},
		{"Ti", "titanium"},
		{"V", "vanadium"},
		{"Cr", "chromium"},
		{"Mn", "manganese"},
		{"Fe", "iron"},
		{"Co", "cobalt"},
		{"Ni", "nickel"},
		{"Cu", "copper"},
		{"Zn", "zinc"},
		{"Ga", "gallium"},
		{"Ge", "germanium"},
		{"As", "arsenic"},
		{"Se", "selenium"},
		{"Br", "bromine"},
		{"Kr", "krypton"},
		{"Rb", "rubidium"},
		{"Sr", "strontium"},
		{"Y", "yttrium"},
		{"Zr", "zirconium"},
		{"Nb", "niobium"},
		{"Mo", "molybdenum"},
		{"Tc", "techetium"},
		{"Ru", "ruthenium"},
		{"Rh", "rhodium"},
		{"Pd", "palladium"},
		{"Ag", "silver"},
		{"Cd", "cadmium"},
		{"In", "indium"},
		{"Sn", "tin"},
		{"Sb", "antimony"},
		{"Te", "tellurium"},
		{"I", "iodine"},
		{"Xe", "xenon"},
		{"Cs", "caesium"},
		{"Ba", "barium"},
		{"La", "lanthanum"},
		{"Ce", "cerium"},
		{"Pr", "praseodymium"},
		{"Nd", "neodymium"},
		{"Pm", "promethium"},
		{"Sm", "samarium"},
		{"Eu", "europium"},
		{"Gd", "gadolinium"},
		{"Tb", "terbium"},
		{"Dy", "dysprosium"},
		{"Ho", "holmium"},
		{"Er", "erbium"},
		{"Tm", "thulium"},
		{"Yb", "ytterbium"},
		{"Lu", "lutetium"},
		{"Hf", "hafnium"},
		{"Ta", "tantalum"},
		{"W", "tungsten"},
		{"Re", "rhenium"},
		{"Os", "osmium"},
		{"Ir", "iridium"},
		{"Pt", "platinum"},
		{"Au", "gold"},
		{"Hg", "mercury"},
		{"Tl", "thallium"},
		{"Pb", "lead"},
		{"Bi", "bismuth"},
		{"Po", "polonium"},
		{"At", "astatine"},
		{"Rn", "radon"},
		{"Fr", "francium"},
		{"Ra", "radium"},
		{"Ac", "actinium"},
		{"Th", "thorium"},
		{"Pa", "proctactinium"},
		{"U", "uranium"},
		{"Np", "neptunium"},
		{"Pu", "plutonium"},
		{"Am", "americium"},
		{"Cm", "curium"},
		{"Bk", "berkelium"},
		{"Cf", "californium"},
		{"Es", "einsteinium"},
		{"Fm", "fermium"},
		{"Md", "mendelevium"},
		{"No", "nobelium"},
		{"Lr", "lawrencium"},
		{"Rf", "rutherfordium"},
		{"Db", "dubnium"},
		{"Sg", "seaborgium"},
		{"Bh", "bohrium"},
		{"Hs", "hassium"},
		{"Mt", "meitnerium"},
		{"Ds", "darmstadtium"},
		{"Rg", "roentgenium"},
		{"Uub", "ununbium"},
		{"Uut", "ununtrium"},
		{"Uuq", "ununquadium"},
		{"Uup", "ununpentium"},
		{"Uuh", "ununhexium"},
		{"Uus", "ununseptium"},
		{"Uuo", "ununoctium"}
	};
	
	public static final String molecules[][] = {
		{"H2", "Hydrogen"},
		{"O2", "Oxygen"},
		{"H2O", "Water"},
		{"H2O2", "Hydrogen Pyroxide"},
		{"H2S", "Hydrogen Suplhide"}
	};
}
