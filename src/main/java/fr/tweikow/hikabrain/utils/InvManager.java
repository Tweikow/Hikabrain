package fr.tweikow.hikabrain.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InvManager {

    public static void sendStuff(Player player, String team){
        ItemStack sword = ItemBuilder.create(Material.IRON_SWORD, 1, null);
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        sword.setItemMeta(meta);

        player.getInventory().addItem(sword);
        player.getInventory().addItem(ItemBuilder.create(Material.SANDSTONE, 35*64, null));

        if (team.equalsIgnoreCase("rouge")) {
            player.getInventory().setHelmet(ItemBuilder.createLeather(Material.LEATHER_HELMET, Color.RED));
            player.getInventory().setChestplate(ItemBuilder.createLeather(Material.LEATHER_CHESTPLATE, Color.RED));
            player.getInventory().setLeggings(ItemBuilder.createLeather(Material.LEATHER_LEGGINGS, Color.RED));
            player.getInventory().setBoots(ItemBuilder.createLeather(Material.LEATHER_BOOTS, Color.RED));
        }
        if (team.equalsIgnoreCase("bleu")) {
            player.getInventory().setHelmet(ItemBuilder.createLeather(Material.LEATHER_HELMET, Color.BLUE));
            player.getInventory().setChestplate(ItemBuilder.createLeather(Material.LEATHER_CHESTPLATE, Color.BLUE));
            player.getInventory().setLeggings(ItemBuilder.createLeather(Material.LEATHER_LEGGINGS, Color.BLUE));
            player.getInventory().setBoots(ItemBuilder.createLeather(Material.LEATHER_BOOTS, Color.BLUE));
        }
    }
}
