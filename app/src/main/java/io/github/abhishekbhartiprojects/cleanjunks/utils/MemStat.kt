package io.github.abhishekbhartiprojects.cleanjunks.utils

import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.Context
import android.os.Build

class MemStat(context: Context) {
    private var mTotalMemory: Long
    private var mUsedMemory: Long

    init {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)

        mTotalMemory = memInfo.totalMem
        mUsedMemory = memInfo.totalMem - memInfo.availMem
    }

    fun getTotalMemory(): Long {
        return mTotalMemory
    }

    fun getUsedMemory(): Long {
        return mUsedMemory
    }
}