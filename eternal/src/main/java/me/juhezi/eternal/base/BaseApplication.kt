package me.juhezi.eternal.base

import android.app.Application
import io.realm.Realm

/**
 * Created by Juhezi[juhezix@163.com] on 2018/7/24.
 */
class BaseApplication : Application() {

    companion object {
        @JvmStatic
        private lateinit var sContext: BaseApplication

        public fun getApplicationContext() = sContext
    }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        sContext = this
    }

}
