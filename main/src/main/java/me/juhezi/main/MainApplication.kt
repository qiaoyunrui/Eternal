package me.juhezi.main

import me.juhezi.eternal.base.BaseApplication
import me.juhezi.eternal.plugin.PluginManager

class MainApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        PluginManager.getInstance().loadPluginApk()
    }

}