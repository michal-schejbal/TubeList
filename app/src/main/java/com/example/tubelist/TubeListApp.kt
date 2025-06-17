package com.example.tubelist

import android.app.Application
import com.example.tubelist.app.Modules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class TubeListApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TubeListApp)
            modules(Modules.items)
        }
    }
}