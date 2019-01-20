package io.github.abhishekbhartiprojects.cleanjunks.model

import java.util.ArrayList

class JunkGroup {
    companion object {
        val GROUP_PROCESS = 0
        val GROUP_CACHE = 1
        val GROUP_APK = 2
        val GROUP_TMP = 3
        val GROUP_LOG = 4
        val GROUP_ADV = 5
        val GROUP_APPLEFT = 6
    }

    var mName: String? = null
    var mSize: Long = 0
    var mChildren: ArrayList<JunkInfo>? = null
}