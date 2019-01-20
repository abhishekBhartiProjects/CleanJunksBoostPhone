package io.github.abhishekbhartiprojects.cleanjunks.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {
    protected var TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}