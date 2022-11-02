package fr.tweikow.hikabrain.utils;

import fr.tweikow.hikabrain.managers.StateGame;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InvManager {

    public static void sendStuff(Player player, String team){
        if (StateGame.getStatus().equals(StateGame.FINISH))
            return;
        ItemStack sword = ItemBuilder.create(Material.IRON_SWORD, 1, null);
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        sword.setItemMeta(meta);

        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, ItemBuilder.create(Material.IRON_PICKAXE, 1, null));
        player.getInventory().setItem(2, ItemBuilder.create(Material.GOLDEN_APPLE, 64, null));
        player.getInventory().addItem(ItemBuilder.create(Material.SANDSTONE, 35*64, null));
        player.getInventory().setItemInOffHand(ItemBuilder.create(Material.SANDSTONE, 64, null));

        if (team.equalsIgnoreCase("red")) {
            player.getInventory().setHelmet(ItemBuilder.createLeather(Material.LEATHER_HELMET, Color.RED));
            player.getInventory().setChestplate(ItemBuilder.createLeather(Material.LEATHER_CHESTPLATE, Color.RED));
            player.getInventory().setLeggings(ItemBuilder.createLeather(Material.LEATHER_LEGGINGS, Color.RED));
            player.getInventory().setBoots(ItemBuilder.createLeather(Material.LEATHER_BOOTS, Color.RED));
        }
        if (team.equalsIgnoreCase("blue")) {
            player.getInventory().setHelmet(ItemBuilder.createLeather(Material.LEATHER_HELMET, Color.BLUE));
            player.getInventory().setChestplate(ItemBuilder.createLeather(Material.LEATHER_CHESTPLATE, Color.BLUE));
            player.getInventory().setLeggings(ItemBuilder.createLeather(Material.LEATHER_LEGGINGS, Color.BLUE));
            player.getInventory().setBoots(ItemBuilder.createLeather(Material.LEATHER_BOOTS, Color.BLUE));
        }
    }
}
