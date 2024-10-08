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

    private var closedModel: Int? = null
    private var parachuteMinHeight: Int? = null

    /**
     * Item initialisation
     */
    fun init(pluginInstance: JavaPlugin?) {
        plugin = pluginInstance
        closedModel = plugin?.config?.getInt("parachute-closed-model")
        parachuteMinHeight = plugin?.config?.getInt("parachute-min-height")
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
     * Makes the parachute item , gives it the enchantment glow description and lore
     */
    fun createParachuteItem(): ItemStack {
        val item = ItemStack(Material.NETHERITE_SWORD, 1)
        val meta = item.itemMeta

        if (meta != null) {
            meta.setDisplayName("§eParachute")

            meta.setCustomModelData(closedModel) //Setting the correct textures

            val lore: MutableList<String> = ArrayList()
            lore.add("§7Hold this item while above $parachuteMinHeight blocks for a safe landing")
            lore.add("§5\"oreo shute\"") //The funni
            meta.lore = lore

            meta.addEnchant(Enchantment.LUCK, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS) //to add the enchant glint but not have it be visible

            // Add a unique identifier to make the item non-stackable
            val data = meta.persistentDataContainer
            val key = NamespacedKey(plugin!!, "unique_id")
            data.set(key, PersistentDataType.STRING, UUID.randomUUID().toString())

            meta.setCustomModelData(closedModel)

            item.setItemMeta(meta)

        }
        return item
    }
}
