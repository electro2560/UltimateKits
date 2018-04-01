package com.songoda.kitpreview.utils;

import com.songoda.arconix.Arconix;
import com.songoda.kitpreview.KitPreview;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by songoda on 2/24/2017.
 */
public class Methods {
    
    public static ItemStack getGlass() {
        KitPreview plugin = KitPreview.getInstance();
        return Arconix.pl().getGUI().getGlass(plugin.getConfig().getBoolean("Interfaces.Replace Glass Type 1 With Rainbow Glass"), plugin.getConfig().getInt("Interfaces.Glass Type 1"));
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        KitPreview plugin = KitPreview.getInstance();
        if (type)
            return Arconix.pl().getGUI().getGlass(false, plugin.getConfig().getInt("Interfaces.Glass Type 2"));
        else
            return Arconix.pl().getGUI().getGlass(false, plugin.getConfig().getInt("Interfaces.Glass Type 3"));
    }

    public static void fillGlass(Inventory i) {
        int nu = 0;
        while (nu != 27) {
            ItemStack glass = getGlass();
            i.setItem(nu, glass);
            nu++;
        }
    }

    public static Collection<Entity> getNearbyEntities(Location location, double x, double y, double z) {
        if (!KitPreview.getInstance().v1_7) return location.getWorld().getNearbyEntities(location,x,y,z);

        if (location == null) return Collections.emptyList();

        World world = location.getWorld();
        net.minecraft.server.v1_7_R4.AxisAlignedBB aabb = net.minecraft.server.v1_7_R4.AxisAlignedBB
                .a(location.getX() - x, location.getY() - y, location.getZ() - z, location.getX() + x, location.getY() + y, location.getZ() + z);
        List<net.minecraft.server.v1_7_R4.Entity> entityList = ((org.bukkit.craftbukkit.v1_7_R4.CraftWorld) world).getHandle().getEntities(null, aabb, null);
        List<Entity> bukkitEntityList = new ArrayList<>();

        for (Object entity : entityList) {
            bukkitEntityList.add(((net.minecraft.server.v1_7_R4.Entity) entity).getBukkitEntity());
        }

        return bukkitEntityList;
    }


}
