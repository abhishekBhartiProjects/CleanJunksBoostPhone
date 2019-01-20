package io.github.abhishekbhartiprojects.cleanjunks.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import io.github.abhishekbhartiprojects.cleanjunks.main.MainActivity
import io.github.abhishekbhartiprojects.cleanjunks.base.BaseActivity
import io.github.abhishekbhartiprojects.cleanjunks.utils.DiskStat
import io.github.abhishekbhartiprojects.cleanjunks.utils.MemStat

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val handler = Handler()

        val thread = Thread(Runnable {
            val diskStat = DiskStat()
            val memStat = MemStat(this@SplashActivity)
            handler.post {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                intent.putExtra(MainActivity.PARAM_TOTAL_SPACE, diskStat.getTotalSpace())
                intent.putExtra(MainActivity.PARAM_USED_SPACE, diskStat.getUsedSpace())
                intent.putExtra(MainActivity.PARAM_TOTAL_MEMORY, memStat.getTotalMemory())
                intent.putExtra(MainActivity.PARAM_USED_MEMORY, memStat.getUsedMemory())
                startActivity(intent)
                finish()
            }
        })

        thread.start()
    }
}
