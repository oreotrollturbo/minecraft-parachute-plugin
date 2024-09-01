package org.oreo.parachutes

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.oreo.parachutes.commands.GetParachute
import org.oreo.parachutes.items.ItemManager
import org.oreo.parachutes.listeners.ParachuteLogic

class Parachutes : JavaPlugin() {


    override fun onEnable() {
        // Plugin startup logic
        server.pluginManager.registerEvents(ParachuteLogic(this), this)

        ItemManager.init(this)

        getCommand("parachute")!!.setExecutor(GetParachute(plugin = this))

        saveDefaultConfig()
    }
}
