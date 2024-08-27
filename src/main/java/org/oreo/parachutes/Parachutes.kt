package org.oreo.parachutes

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.oreo.parachutes.commands.GetParachute
import org.oreo.parachutes.items.ItemManager
import org.oreo.parachutes.listeners.ParachutePlayerTouchGround

class Parachutes : JavaPlugin() {

    public val playersParachuting: MutableList<Player> = ArrayList()

    override fun onEnable() {
        // Plugin startup logic
        server.pluginManager.registerEvents(ParachutePlayerTouchGround(this), this)

        ItemManager.init(this)

        getCommand("parachute")!!.setExecutor(GetParachute())
    }
}
