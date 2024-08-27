package org.oreo.parachutes.items

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

object ItemManager {
    private var plugin: JavaPlugin? = null
    var parachute: ItemStack? = null

    /**
     * Item initialisation
     */
    fun init(pluginInstance: JavaPlugin?) {
        plugin = pluginInstance
        createParachute()
    }

    /**
     * Creates the item
     */
    private fun createParachute() {
        parachute = createParachuteItem()
    }

    /**
     * @return the item
     * Makes the siege ladder item , gives it the enchantment glow description and lore
     */
    fun createParachuteItem(): ItemStack {
        val item = ItemStack(Material.NETHERITE_SWORD, 1)
        val meta = item.itemMeta

        if (meta != null) {
            meta.setDisplayName("§eParachute")

            meta.setCustomModelData(10) //Setting the correct textures

            val lore: MutableList<String> = ArrayList()
            lore.add("§7experimetntal")
            lore.add("§5\"idfk\"") //The funni
            meta.lore = lore

            meta.addEnchant(Enchantment.LUCK, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS) //to add the enchant glint but not have it be visible

            // Add a unique identifier to make the item non-stackable
            val data = meta.persistentDataContainer
            val key = NamespacedKey(plugin!!, "unique_id")
            data.set(key, PersistentDataType.STRING, UUID.randomUUID().toString())

            item.setItemMeta(meta)
        }
        return item
    }
}
