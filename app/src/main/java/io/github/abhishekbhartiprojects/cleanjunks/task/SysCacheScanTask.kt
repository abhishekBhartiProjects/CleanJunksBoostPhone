package io.github.abhishekbhartiprojects.cleanjunks.task

import android.content.pm.IPackageStatsObserver
import android.content.pm.PackageManager
import android.content.pm.PackageStats
import android.os.AsyncTask
import android.os.RemoteException
import io.github.abhishekbhartiprojects.cleanjunks.MyApplication
import io.github.abhishekbhartiprojects.cleanjunks.R
import io.github.abhishekbhartiprojects.cleanjunks.callback.IScanCallback
import io.github.abhishekbhartiprojects.cleanjunks.model.JunkInfo
import java.lang.reflect.InvocationTargetException
import java.util.*

class SysCacheScanTask(callback: IScanCallback): AsyncTask<Void, Void, Void>() {

    private val mCallback: IScanCallback
    private var mScanCount: Int = 0
    private var mTotalCount: Int = 0
    private var mSysCaches: ArrayList<JunkInfo>? = null
    private var mAppNames: HashMap<String, String>? = null
    private var mTotalSize = 0L

    init {
        this.mCallback = callback
    }

    override fun doInBackground(vararg p0: Void?): Void? {
        mCallback.onBegin()
        if(MyApplication.getInstance() != null){
            val pm = MyApplication.getInstance()!!.getPackageManager()
            val installedPackages = pm.getInstalledApplications(PackageManager.GET_GIDS)

            mScanCount = 0
            mTotalCount = installedPackages.size
            mSysCaches = ArrayList()
            mAppNames = HashMap()

            val observer: IPackageStatsObserver.Stub = PackageStatsObserver() //todo: Here i have changed logic

            for (i in 0 until mTotalCount) {
                val info = installedPackages[i]
                mAppNames!![info.packageName] = pm.getApplicationLabel(info).toString()
                getPackageInfo(info.packageName, observer)
            }
        }

        return null
    }

    fun getPackageInfo(packageName: String, observer: IPackageStatsObserver.Stub) {
        try {
            val pm = MyApplication.getInstance()!!.getPackageManager()
            val getPackageSizeInfo = pm.javaClass
                    .getMethod("getPackageSizeInfo", String::class.java, IPackageStatsObserver::class.java)

            getPackageSizeInfo.invoke(pm, packageName, observer)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

    }


    private inner class PackageStatsObserver : IPackageStatsObserver.Stub() {

        override fun onGetStatsCompleted(packageStats: PackageStats?, succeeded: Boolean) {
            mScanCount++
            if (packageStats == null || !succeeded) {
            } else {
                val info = JunkInfo()
                info.mPackageName = packageStats.packageName
                info.name = mAppNames!![info!!.mPackageName]
                info.mSize = packageStats.cacheSize + packageStats.externalCacheSize
                if (info.mSize > 0) {
                    mSysCaches!!.add(info)
                    mTotalSize += info.mSize
                }
                mCallback.onProgress(info)
            }

            if (mScanCount == mTotalCount) {
                val info = JunkInfo()
                info.name = if(MyApplication.getInstance() != null) MyApplication.getInstance()!!.getString(R.string.system_cache) else ""
                info.mSize = mTotalSize
                Collections.sort(mSysCaches)
                Collections.reverse(mSysCaches)
                info.mChildren = mSysCaches!!
                info.mIsVisible = true
                info.mIsChild = false

                val list = ArrayList<JunkInfo>()
                list.add(info)
                mCallback.onFinish(list)
            }
        }
    }
}