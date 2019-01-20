package io.github.abhishekbhartiprojects.cleanjunks.callback

import io.github.abhishekbhartiprojects.cleanjunks.model.JunkInfo
import java.util.ArrayList

interface IScanCallback {
    fun onBegin()

    fun onProgress(info: JunkInfo)

    fun onFinish(children: ArrayList<JunkInfo>)
}