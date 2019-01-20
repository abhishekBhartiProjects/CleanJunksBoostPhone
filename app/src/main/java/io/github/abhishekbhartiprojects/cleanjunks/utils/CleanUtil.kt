package io.github.abhishekbhartiprojects.cleanjunks.utils

import android.app.ActivityManager
import android.content.Context
import android.content.pm.IPackageDataObserver
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.RemoteException
import io.github.abhishekbhartiprojects.cleanjunks.MyApplication
import io.github.abhishekbhartiprojects.cleanjunks.R
import io.github.abhishekbhartiprojects.cleanjunks.junkClean.JunkCleanActivity
import io.github.abhishekbhartiprojects.cleanjunks.model.JunkInfo
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.util.ArrayList

class CleanUtil {

    companion object {
        fun formatShortFileSize(context: Context?, number: Long): String {
            if (context == null) {
                return ""
            }

            var result = number.toFloat()
            var suffix = R.string.byte_short
            if (result > 900) {
                suffix = R.string.kilo_byte_short
                result = result / 1024
            }
            if (result > 900) {
                suffix = R.string.mega_byte_short
                result = result / 1024
            }
            if (result > 900) {
                suffix = R.string.giga_byte_short
                result = result / 1024
            }
            if (result > 900) {
                suffix = R.string.tera_byte_short
                result = result / 1024
            }
            if (result > 900) {
                suffix = R.string.peta_byte_short
                result = result / 1024
            }
            val value: String
            if (result < 1) {
                value = String.format("%.2f", result)
            } else if (result < 10) {
                value = String.format("%.2f", result)
            } else if (result < 100) {
                value = String.format("%.1f", result)
            } else {
                value = String.format("%.0f", result)
            }
            return context.resources.getString(R.string.clean_file_size_suffix,
                    value, context.getString(suffix))
        }

        fun freeAllAppsCache(handler: Handler) {

            val context = MyApplication.getInstance()

            if(context != null){
                val externalDir = context.getExternalCacheDir() ?: return

                val pm = context.getPackageManager()
                val installedPackages = pm.getInstalledApplications(PackageManager.GET_GIDS)
                for (info in installedPackages) {
                    val externalCacheDir = externalDir.getAbsolutePath()
                            .replace(context.getPackageName(), info.packageName)
                    val externalCache = File(externalCacheDir)
                    if (externalCache.exists() && externalCache.isDirectory()) {
                        deleteFile(externalCache)
                    }
                }

                var hanged = true
                try {
                    val freeStorageAndNotify = pm.javaClass
                            .getMethod("freeStorageAndNotify", Long::class.javaPrimitiveType, IPackageDataObserver::class.java)
                    val freeStorageSize = java.lang.Long.MAX_VALUE

                    freeStorageAndNotify.invoke(pm, freeStorageSize, object : IPackageDataObserver.Stub() {
                        override fun onRemoveCompleted(packageName: String, succeeded: Boolean) {
                            val msg = handler.obtainMessage(JunkCleanActivity.MSG_SYS_CACHE_CLEAN_FINISH)
                            msg.sendToTarget()
                        }
                    })
                    hanged = false
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }

                if (hanged) {
                    val msg = handler.obtainMessage(JunkCleanActivity.MSG_SYS_CACHE_CLEAN_FINISH)
                    val bundle = Bundle()
                    bundle.putBoolean(JunkCleanActivity.HANG_FLAG, true)
                    msg.data = bundle
                    msg.sendToTarget()
                }
            }
        }

        fun deleteFile(file: File): Boolean {
            if (file.isDirectory) {
                val children = file.list()
                if(children != null && children.size > 0){
                    for (name in children) {
                        val suc = deleteFile(File(file, name))
                        if (!suc) {
                            return false
                        }
                    }
                }
            }
            return file.delete()
        }

        fun killAppProcesses(packageName: String?) {
            if (packageName == null || packageName.isEmpty()) {
                return
            }

            val am = MyApplication.getInstance()!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.killBackgroundProcesses(packageName)
        }

        fun freeJunkInfos(junks: ArrayList<JunkInfo>, handler: Handler) {
            if(junks != null && junks.size > 0){
                for (info in junks) {
                    val file = File(info.mPath)
                    if (file != null && file.exists()) {
                        file.delete()
                    }
                }
            }

            val msg = handler.obtainMessage(JunkCleanActivity.MSG_OVERALL_CLEAN_FINISH)
            msg.sendToTarget()
        }
    }
}