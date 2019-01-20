package io.github.abhishekbhartiprojects.cleanjunks.task

import android.os.AsyncTask
import android.os.Environment
import io.github.abhishekbhartiprojects.cleanjunks.callback.IScanCallback
import io.github.abhishekbhartiprojects.cleanjunks.model.JunkInfo
import java.io.File
import java.util.*

class OverallScanTask(callback: IScanCallback): AsyncTask<Void, Void, Void>() {
    private val mCallback: IScanCallback
    private val SCAN_LEVEL = 4
    private val mApkInfo: JunkInfo
    private val mLogInfo: JunkInfo
    private val mTmpInfo: JunkInfo

    init {
        mCallback = callback
        mApkInfo = JunkInfo()
        mLogInfo = JunkInfo()
        mTmpInfo = JunkInfo()
    }

    private fun travelPath(root: File?, level: Int) {
        if (root == null || !root.exists() || level > SCAN_LEVEL) {
            return
        }

        val lists = root.listFiles()
        if(lists != null){
            for (file in lists) {
                if (file.isFile) {
                    val name = file.name
                    var info: JunkInfo? = null
                    if (name.endsWith(".apk")) {
                        info = JunkInfo()
                        info.mSize = file.length()
                        info.name = name
                        info.mPath = file.absolutePath
                        info.mIsChild = false
                        info.mIsVisible = true
                        mApkInfo.mChildren.add(info)
                        mApkInfo.mSize += info.mSize
                    } else if (name.endsWith(".log")) {
                        info = JunkInfo()
                        info.mSize = file.length()
                        info.name = name
                        info.mPath = file.absolutePath
                        info.mIsChild = false
                        info.mIsVisible = true
                        mLogInfo.mChildren.add(info)
                        mLogInfo.mSize += info.mSize
                    } else if (name.endsWith(".tmp") || name.endsWith(".temp")) {
                        info = JunkInfo()
                        info.mSize = file.length()
                        info.name = name
                        info.mPath = file.absolutePath
                        info.mIsChild = false
                        info.mIsVisible = true
                        mTmpInfo.mChildren.add(info)
                        mTmpInfo.mSize += info.mSize
                    }

                    if (info != null) {
                        mCallback.onProgress(info)
                    }
                } else {
                    if (level < SCAN_LEVEL) {
                        travelPath(file, level + 1)
                    }
                }
            }
        }
    }

    override fun doInBackground(vararg p0: Void?): Void? {
        mCallback.onBegin()

        val externalDir = Environment.getExternalStorageDirectory()
        if (externalDir != null) {
            travelPath(externalDir, 0)
        }

        val list = ArrayList<JunkInfo>()

        if (mApkInfo.mSize > 0L) {
            Collections.sort(mApkInfo.mChildren)
            Collections.reverse(mApkInfo.mChildren)
            list.add(mApkInfo)
        }

        if (mLogInfo.mSize > 0L) {
            Collections.sort(mLogInfo.mChildren)
            Collections.reverse(mLogInfo.mChildren)
            list.add(mLogInfo)
        }

        if (mTmpInfo.mSize > 0L) {
            Collections.sort(mTmpInfo.mChildren)
            Collections.reverse(mTmpInfo.mChildren)
            list.add(mTmpInfo)
        }

        mCallback.onFinish(list)

        return null

    }
}