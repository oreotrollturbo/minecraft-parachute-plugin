package org.oreo.parachutes.listeners

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.oreo.parachutes.Parachutes
import org.oreo.parachutes.items.ItemManager


class ParachuteLogic(private val plugin: Parachutes) : Listener{

    private val minParachuteHeight = plugin.config.getInt("parachute-min-height")
    private val parachuteDrain = plugin.config.getInt("parachute-drain")
    private val parachutePenalty = plugin.config.getInt("parachute-open-penalty")


     //This one isnt used yet but ill keep it here in case any changes happen
    private val parachuteClosedModel = plugin.config.getInt("parachute-closed-model")

    private val parachuteOpenModel = plugin.config.getInt("parachute-open-model")

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        val player = e.player

        if (!(e.from.y > e.to.y)) {  // Player is moving downwards
            return
        }

        if (isHoldingParachute(player)) {
            val itemInHand = player.inventory.itemInMainHand
            var itemMeta = itemInHand.itemMeta

            if (!player.isOnGround && isOnGround(player)) {
                player.removePotionEffect(PotionEffectType.SLOW_FALLING)

                if (itemMeta != null) {
                    itemMeta.setCustomModelData(parachuteClosedModel)
                    itemInHand.setItemMeta(itemMeta)
                }
                return
            }

            if (itemMeta == null) {
                itemMeta = Bukkit.getItemFactory().getItemMeta(itemInHand.type)
            }

            if (!isTooCloseToGround(player, player.world) && !player.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
                decreaseItemDurability(player, parachutePenalty)
                if (itemMeta != null) {
                    itemMeta.setCustomModelData(parachuteOpenModel) // Make the parachute open
                    itemInHand.setItemMeta(itemMeta)
                }
            }

            if (player.hasPotionEffect(PotionEffectType.SLOW_FALLING) || !isTooCloseToGround(player, player.world)) {
                player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 1 * 5, 1, true, false))
                decreaseItemDurability(player, parachuteDrain)
            } else if (!player.hasPotionEffect(PotionEffectType.SLOW_FALLING) && isTooCloseToGround(player, player.world) && !isOnGround(player)) {
                sendHotbarMessage(player, "You are too close to the ground to deploy")
            }
        }
    }

    private fun isOnGround(player: Player): Boolean {
        // Check if the block directly beneath the player's feet is solid
        return player.location.add(0.0, -1.0, 0.0).block.type != Material.AIR
    }

    /**
     * Instead of checking for the item meta in general we check for everything but durability
     */
    private fun isHoldingParachute(player: Player): Boolean {

        val itemInHand = player.inventory.itemInMainHand

        if (!isParachute(itemInHand)){
            return false
        }


        return true
    }

    private fun isParachute(item:ItemStack) : Boolean{

        val parachute = ItemManager.parachute ?: return false

        val itemMetaInHand = item.itemMeta ?: return false
        val parachuteMeta = parachute.itemMeta ?: return false

        // Check display name
        if (itemMetaInHand.displayName != parachuteMeta.displayName) {
            return false
        }

        // Check lore
        if (itemMetaInHand.lore != parachuteMeta.lore) {
            return false
        }

        // Check enchantments
        if (itemMetaInHand.enchants != parachuteMeta.enchants) {
            return false
        }

        return true
    }

    private fun isTooCloseToGround(player : Player , world: World) : Boolean{

        if ((player.location.y - minParachuteHeight) > world.getHighestBlockAt(player.location).y){
            return false
        }

        return true
    }


    private fun sendHotbarMessage(player: Player, message: String?) {
        player.spigot().sendMessage(
            ChatMessageType.ACTION_BAR,
            *TextComponent.fromLegacyText(message)
        )
    }

    private fun decreaseItemDurability(player: Player, damage : Int){
        val stack = player.itemInHand
        val currentDurability = stack.durability.toInt()
        val newDurability = (currentDurability + damage).coerceAtMost(stack.type.maxDurability.toInt())

        stack.durability = newDurability.toShort()

        // If the item's durability has reached its maximum, break the item
        if (newDurability >= stack.type.maxDurability) {
            //didn't use the stack value here just in case
            player.inventory.itemInMainHand.amount -= 1 // Break the item
        }
    }

    /**
     * This stops players from using the parachute as a weapon
     */
    @EventHandler
    fun parachuteDamage(e:EntityDamageByEntityEvent){

        if (e.damager !is Player){
            return
        }
        val player = e.damager as Player

        if (isHoldingParachute(player)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun switchFromParachuteEvent(e:PlayerItemHeldEvent){

        val player = e.player

        val previousItemItem: ItemStack? = player.inventory.getItem(e.previousSlot)

        if (previousItemItem != null && isParachute(previousItemItem)) {

            val itemMeta = previousItemItem.itemMeta

            if (itemMeta != null) {
                itemMeta.setCustomModelData(parachuteClosedModel)
                previousItemItem.setItemMeta(itemMeta)
            }

        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    fun playerCraftNetheriteSword(e: PrepareItemCraftEvent){
        val inventory = e.inventory
        val items = inventory.matrix

        // Check if any item in the crafting grid is a parachute
        for (item in items) {
            if (item != null && isParachute(item)) {
                // Cancel the crafting by setting the result to null
                inventory.result = ItemStack(Material.AIR)
                break
            }
        }
    }

}