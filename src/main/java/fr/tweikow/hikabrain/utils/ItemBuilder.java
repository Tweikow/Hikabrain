package fr.tweikow.hikabrain.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public abstract class ItemBuilder {
    ItemBuilder items;
    public ItemBuilder(ItemBuilder itemBuilder) {
        this.items=itemBuilder;
    }

    public ItemStack ItemBuilder(Material material, int amount, Enchantment[] enchantments,
                                 int[] enchantmentLevels, String name, String... lores) {
        return create(material, amount, enchantments, enchantmentLevels, name, lores);
    }

    public static ItemStack create(Material material, int amount, Enchantment[] enchantments,
                                   int[] enchantmentLevels, String name, String... lores) {
        ItemStack itemStack;
        ItemMeta itemMeta;

        if (material == null)
            return null;
        itemStack = new ItemStack(material, amount);
        if (enchantments != null && enchantmentLevels != null && enchantments.length == enchantmentLevels.length) {
            for (int i = 0; i < enchantments.length; i++)
                itemStack.addEnchantment(enchantments[i], enchantmentLevels[i]);
        }
        if (name == null && (lores == null || lores.length < 1))
            return itemStack;
        itemMeta = itemStack.getItemMeta();
        if (name != null)
            itemMeta.setDisplayName(name);
        if (lores != null)
            itemMeta.setLore(Arrays.asList(lores));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack create(Material material, int amount, String name, String... lores) {
        return create(material, amount, null, null, name, lores);
    }

    public static ItemStack create(Material material, int amount, int data, String name, String... lores) {
        return create(material, amount, null,null, name, lores);
    }

    public static ItemStack createWithoutLores(Material material, int amount, short damage, byte data, String name) {
        return create(material, amount, null, null, name, new String[0]);
    }

    public static ItemStack createWithoutLores(Material material, int amount, byte data, String name) {
        return createWithoutLores(material, amount, (short) 0, data, name);
    }

    public static ItemStack createWithoutLores(Material material, int amount, short damage, String name) {
        return createWithoutLores(material, amount, damage, (byte) 0, name);
    }

    public static ItemStack createWithoutLores(Material material, int amount, byte data) {
        return createWithoutLores(material, amount, (short) 0, data, null);
    }

    public static ItemStack createWithoutLores(Material material, int amount, short damage) {
        return createWithoutLores(material, amount, damage, (byte) 0, null);
    }

    public static ItemStack createWithoutLores(Material material, int amount, String name, String... lores) {
        return create(material, amount, null, null, name, lores);
    }

    public static ItemStack skull(int amount, String name, String owner, String... lores){
        ItemStack itemStack;
        SkullMeta itemMeta;
        if (name == null || lores == null) return null;

        itemStack = new ItemStack(Material.PLAYER_HEAD, amount, (short) 3);

        itemMeta = (SkullMeta) itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lores));
        itemMeta.setOwner(owner);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createLeather(Material material, Color color) {
        ItemStack itemStack = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.setColor(color);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}