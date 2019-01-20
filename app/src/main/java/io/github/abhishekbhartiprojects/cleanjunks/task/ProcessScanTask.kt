package io.github.abhishekbhartiprojects.cleanjunks.task

import android.content.pm.PackageManager
import android.os.AsyncTask
import com.jaredrummler.android.processes.ProcessManager
import io.github.abhishekbhartiprojects.cleanjunks.MyApplication
import io.github.abhishekbhartiprojects.cleanjunks.callback.IScanCallback
import io.github.abhishekbhartiprojects.cleanjunks.model.JunkInfo
import java.io.IOException
import java.util.*

class ProcessScanTask(callback: IScanCallback): AsyncTask<Void, Void, Void>() {

    private val mCallback: IScanCallback
    init {
        this.mCallback = callback
    }


    override fun doInBackground(vararg p0: Void?): Void? {
        mCallback.onBegin()

        val processes = ProcessManager.getRunningAppProcesses()

        val junks = ArrayList<JunkInfo>()

        for (process in processes) {
            val info = JunkInfo()
            info.mIsChild = false
            info.mIsVisible = true
            info.mPackageName = process.getPackageName()

            try {
                val statm = process.statm()
                info.mSize = statm.getResidentSetSize()
            } catch (e: IOException) {
                e.printStackTrace()
                continue
            }

            try {
                val pm = MyApplication.getInstance()!!.getPackageManager()
                val packageInfo = process.getPackageInfo(MyApplication.getInstance(), 0)
                info.name = packageInfo.applicationInfo.loadLabel(pm).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                continue
            }

            mCallback.onProgress(info)

            junks.add(info)
        }

        Collections.sort(junks)
        Collections.reverse(junks)
        mCallback.onFinish(junks)

        return null
    }
}