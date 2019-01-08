package me.badbones69.crazycrates.multisupport.nms;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

public class NMS_v1_8_R3 {
	
	public static ItemStack addGlow(ItemStack item) {
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasEnchants())
				return item;
		}
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;
		if(!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		if(tag == null)
			tag = nmsStack.getTag();
		NBTTagList ench = new NBTTagList();
		tag.set("ench", ench);
		nmsStack.setTag(tag);
		return CraftItemStack.asCraftMirror(nmsStack);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getInHand(Player player) {
		return player.getItemInHand();
	}
	
	public static void openChest(Block b, Location location, Boolean open) {
		net.minecraft.server.v1_8_R3.World world = ((CraftWorld) location.getWorld()).getHandle();
		net.minecraft.server.v1_8_R3.BlockPosition position = new net.minecraft.server.v1_8_R3.BlockPosition(location
				.getX(), location.getY(), location.getZ());
		if(b.getType() == Material.ENDER_CHEST) {
			net.minecraft.server.v1_8_R3.TileEntityEnderChest tileChest = (net.minecraft.server.v1_8_R3.TileEntityEnderChest) world
					.getTileEntity(position);
			world.playBlockAction(position, tileChest.w(), 1, open ? 1 : 0);
		}else {
			net.minecraft.server.v1_8_R3.TileEntityChest tileChest = (net.minecraft.server.v1_8_R3.TileEntityChest) world
					.getTileEntity(position);
			world.playBlockAction(position, tileChest.w(), 1, open ? 1 : 0);
		}
	}
	
	// http://stackoverflow.com/questions/24101928/setting-block-data-from-schematic-in-bukkit
	@SuppressWarnings("deprecation")
	public static List<Location> pasteSchematic(File f, Location loc) {
		loc = loc.subtract(2, 1, 2);
		List<Location> locations = new ArrayList<Location>();
		try {
			FileInputStream fis = new FileInputStream(f);
			NBTTagCompound nbt = NBTCompressedStreamTools.a(fis);
			short width = nbt.getShort("Width");
			short height = nbt.getShort("Height");
			short length = nbt.getShort("Length");
			byte[] blocks = nbt.getByteArray("Blocks");
			byte[] data = nbt.getByteArray("Data");
			fis.close();
			// paste
			for(int x = 0; x < width; ++x) {
				for(int y = 0; y < height; ++y) {
					for(int z = 0; z < length; ++z) {
						int index = y * width * length + z * width + x;
						final Location l = new Location(loc.getWorld(), x + loc.getX(), y + loc.getY(), z + loc.getZ());
						int b = blocks[index] & 0xFF;// make the block unsigned, so that blocks with an id over 127,
														// like quartz and emerald, can be pasted
						final Block block = l.getBlock();
						Material m = Material.getMaterial(b);
						block.setType(m);
						block.setData(data[index]);
						// you can check what type the block is here, like if(m.equals(Material.BEACON))
						// to check if it's a beacon
						locations.add(l);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return locations;
	}
	
	public static List<Location> getLocations(File f, Location loc) {
		loc = loc.subtract(2, 1, 2);
		List<Location> locations = new ArrayList<Location>();
		try {
			FileInputStream fis = new FileInputStream(f);
			NBTTagCompound nbt = NBTCompressedStreamTools.a(fis);
			short width = nbt.getShort("Width");
			short height = nbt.getShort("Height");
			short length = nbt.getShort("Length");
			fis.close();
			// paste
			for(int x = 0; x < width; ++x) {
				for(int y = 0; y < height; ++y) {
					for(int z = 0; z < length; ++z) {
						final Location l = new Location(loc.getWorld(), x + loc.getX(), y + loc.getY(), z + loc.getZ());
						locations.add(l);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return locations;
	}
	
}