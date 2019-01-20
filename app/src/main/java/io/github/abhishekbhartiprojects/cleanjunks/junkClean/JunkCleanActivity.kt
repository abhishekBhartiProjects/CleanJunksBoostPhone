package io.github.abhishekbhartiprojects.cleanjunks.junkClean

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import io.github.abhishekbhartiprojects.cleanjunks.R
import io.github.abhishekbhartiprojects.cleanjunks.callback.IScanCallback
import io.github.abhishekbhartiprojects.cleanjunks.model.JunkGroup
import io.github.abhishekbhartiprojects.cleanjunks.model.JunkInfo
import io.github.abhishekbhartiprojects.cleanjunks.task.OverallScanTask
import io.github.abhishekbhartiprojects.cleanjunks.task.ProcessScanTask
import io.github.abhishekbhartiprojects.cleanjunks.task.SysCacheScanTask
import io.github.abhishekbhartiprojects.cleanjunks.utils.CleanUtil
import java.util.*

class JunkCleanActivity : AppCompatActivity() {

    companion object {
        val MSG_SYS_CACHE_BEGIN = 0x1001
        val MSG_SYS_CACHE_POS = 0x1002
        val MSG_SYS_CACHE_FINISH = 0x1003

        val MSG_PROCESS_BEGIN = 0x1011
        val MSG_PROCESS_POS = 0x1012
        val MSG_PROCESS_FINISH = 0x1013

        val MSG_OVERALL_BEGIN = 0x1021
        val MSG_OVERALL_POS = 0x1022
        val MSG_OVERALL_FINISH = 0x1023

        val MSG_SYS_CACHE_CLEAN_FINISH = 0x1100
        val MSG_PROCESS_CLEAN_FINISH = 0x1101
        val MSG_OVERALL_CLEAN_FINISH = 0x1102

        val HANG_FLAG = "hanged"

    }

    private var handler: Handler? = null

    private var mIsSysCacheScanFinish = false
    private var mIsSysCacheCleanFinish = false

    private var mIsProcessScanFinish = false
    private var mIsProcessCleanFinish = false

    private var mIsOverallScanFinish = false
    private var mIsOverallCleanFinish = false

    private var mIsScanning = false

    private var mAdapter: BaseExpandableListAdapter? = null
    private var mJunkGroups: HashMap<Int, JunkGroup>? = null

    lateinit var mCleanButton: Button

    private var mHeaderView: ListHeaderView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_junk_clean)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)

                if(mHeaderView != null){
                    when (msg.what) {
                        MSG_SYS_CACHE_BEGIN -> {
                        }

                        MSG_SYS_CACHE_POS -> {
                            mHeaderView!!.mProgress.text = "Scanning:" + (msg.obj as JunkInfo).mPackageName
                            mHeaderView!!.mSize.text = CleanUtil.formatShortFileSize(this@JunkCleanActivity, getTotalSize())
                        }

                        MSG_SYS_CACHE_FINISH -> {
                            mIsSysCacheScanFinish = true
                            checkScanFinish()
                        }

                        MSG_SYS_CACHE_CLEAN_FINISH -> {
                            mIsSysCacheCleanFinish = true
                            checkCleanFinish()
                            val bundle = msg.data
                            if (bundle != null) {
                                val hanged = bundle.getBoolean(HANG_FLAG, false)
                                if (hanged) {
                                    Toast.makeText(this@JunkCleanActivity, "Cleanup system cache exception！", Toast.LENGTH_SHORT)
                                            .show()
                                }
                            }
                        }

                        MSG_PROCESS_BEGIN -> {
                        }

                        MSG_PROCESS_POS -> {
                            mHeaderView!!.mProgress.text = "Scanning:" + (msg.obj as JunkInfo).mPackageName
                            mHeaderView!!.mSize.text = CleanUtil.formatShortFileSize(this@JunkCleanActivity, getTotalSize())
                        }

                        MSG_PROCESS_FINISH -> {
                            mIsProcessScanFinish = true
                            checkScanFinish()
                        }

                        MSG_PROCESS_CLEAN_FINISH -> {
                            mIsProcessCleanFinish = true
                            checkCleanFinish()
                        }

                        MSG_OVERALL_BEGIN -> {
                        }

                        MSG_OVERALL_POS -> {
                            mHeaderView!!.mProgress.text = "Scanning:" + (msg.obj as JunkInfo).mPath
                            mHeaderView!!.mSize.text = CleanUtil.formatShortFileSize(this@JunkCleanActivity, getTotalSize())
                        }

                        MSG_OVERALL_FINISH -> {
                            mIsOverallScanFinish = true
                            checkScanFinish()
                        }

                        MSG_OVERALL_CLEAN_FINISH -> {
                            mIsOverallCleanFinish = true
                            checkCleanFinish()
                        }
                    }
                }

            }
        }


        mCleanButton = findViewById(R.id.do_junk_clean) as Button
        mCleanButton.setEnabled(true) //todo false
        mCleanButton.setOnClickListener({
            mCleanButton.setEnabled(false)
            clearAll()
        })


        resetState()

        val listView = findViewById(R.id.junk_list) as ExpandableListView
        mHeaderView = ListHeaderView(this, listView)
        mHeaderView!!.mProgress.setGravity(Gravity.CENTER_VERTICAL or Gravity.LEFT)
        listView.addHeaderView(mHeaderView)
        listView.setGroupIndicator(null)
        listView.setChildIndicator(null)
        listView.dividerHeight = 0
        listView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val info = mAdapter!!.getChild(groupPosition, childPosition) as JunkInfo
            if (groupPosition == JunkGroup.GROUP_APK ||
                    info.mIsChild ||
                    groupPosition == JunkGroup.GROUP_ADV && !info.mIsChild && info.mPath != null) {
                if (info.mPath != null) {
                    Toast.makeText(this@JunkCleanActivity, info.mPath, Toast.LENGTH_SHORT).show()
                }
            } else {
                val childrenInThisGroup = mAdapter!!.getChildrenCount(groupPosition)
                for (i in childPosition + 1 until childrenInThisGroup) {
                    val child = mAdapter!!.getChild(groupPosition, i) as JunkInfo
                    if (!child.mIsChild) {
                        break
                    }

                    child.mIsVisible = !child.mIsVisible
                }
                mAdapter!!.notifyDataSetChanged()
            }
            false
        }
        mAdapter = object : BaseExpandableListAdapter() {
            override fun getGroupCount(): Int {
                return mJunkGroups!!.size
            }

            override fun getChildrenCount(groupPosition: Int): Int {
                return if (mJunkGroups!!.get(groupPosition)!!.mChildren != null) {
                    mJunkGroups!!.get(groupPosition)!!.mChildren!!.size
                } else {
                    0
                }
            }

            override fun getGroup(groupPosition: Int): JunkGroup? {
                return mJunkGroups!!.get(groupPosition)
            }

            override fun getChild(groupPosition: Int, childPosition: Int): Any {
                return mJunkGroups!!.get(groupPosition)!!.mChildren!!.get(childPosition)
            }

            override fun getGroupId(groupPosition: Int): Long {
                return 0
            }

            override fun getChildId(groupPosition: Int, childPosition: Int): Long {
                return 0
            }

            override fun hasStableIds(): Boolean {
                return false
            }

            override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
                var convertView = convertView
                val holder: GroupViewHolder
                if (convertView == null) {
                    convertView = LayoutInflater.from(this@JunkCleanActivity)
                            .inflate(R.layout.group_list, null)
                    holder = GroupViewHolder()
                    holder.mPackageNameTv = convertView!!.findViewById(R.id.package_name) as TextView
                    holder.mPackageSizeTv = convertView.findViewById(R.id.package_size) as TextView
                    convertView.tag = holder
                } else {
                    holder = convertView.tag as GroupViewHolder
                }

                val group = mJunkGroups!!.get(groupPosition)
                holder.mPackageNameTv!!.setText(group!!.mName)
                holder.mPackageSizeTv!!.setText(CleanUtil.formatShortFileSize(this@JunkCleanActivity, group.mSize))

                return convertView
            }

            override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View, parent: ViewGroup): View {
                var convertView = convertView
                val info = mJunkGroups!!.get(groupPosition)!!.mChildren!!.get(childPosition)

                if (info.mIsVisible) {
                    val holder: ChildViewHolder
                    if (info.mIsChild) {
                        convertView = LayoutInflater.from(this@JunkCleanActivity)
                                .inflate(R.layout.level2_item_list, null)
                    } else {
                        convertView = LayoutInflater.from(this@JunkCleanActivity)
                                .inflate(R.layout.level1_item_list, null)
                    }
                    holder = ChildViewHolder()
                    holder.mJunkTypeTv = convertView.findViewById(R.id.junk_type) as TextView
                    holder.mJunkSizeTv = convertView.findViewById(R.id.junk_size) as TextView

                    holder!!.mJunkTypeTv!!.setText(info.name)
                    holder!!.mJunkSizeTv!!.setText(CleanUtil.formatShortFileSize(this@JunkCleanActivity, info.mSize))
                } else {
                    convertView = LayoutInflater.from(this@JunkCleanActivity)
                            .inflate(R.layout.item_null, null)
                }

                return convertView
            }

            override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
                return true
            }
        }

        listView.setAdapter(mAdapter)

        if (!mIsScanning) {
            mIsScanning = true
            startScan()
        }
    }


    private fun clearAll() {
        val clearThread = Thread(Runnable {
            val processGroup = mJunkGroups!!.get(JunkGroup.GROUP_PROCESS)
            for (info in processGroup!!.mChildren!!) {
                CleanUtil.killAppProcesses(info.mPackageName)
            }
            val msg = handler!!.obtainMessage(JunkCleanActivity.MSG_PROCESS_CLEAN_FINISH)
            msg.sendToTarget()

            CleanUtil.freeAllAppsCache(handler!!)

            val junks = ArrayList<JunkInfo>()
            var group = mJunkGroups!!.get(JunkGroup.GROUP_APK)
            junks.addAll(group!!.mChildren!!)

            group = mJunkGroups!!.get(JunkGroup.GROUP_LOG)
            junks.addAll(group!!.mChildren!!)

            group = mJunkGroups!!.get(JunkGroup.GROUP_TMP)
            junks.addAll(group!!.mChildren!!)

            CleanUtil.freeJunkInfos(junks, handler!!)
        })
        clearThread.start()
    }

    private fun resetState() {
        mIsScanning = false

        mIsSysCacheScanFinish = false
        mIsSysCacheCleanFinish = false

        mIsProcessScanFinish = false
        mIsProcessCleanFinish = false

        mJunkGroups = HashMap<Int, JunkGroup> ()

        mCleanButton!!.setEnabled(true)//TODO false

        val cacheGroup = JunkGroup()
        cacheGroup.mName = getString(R.string.cache_clean)
        cacheGroup.mChildren = arrayListOf()
        mJunkGroups!![JunkGroup.GROUP_CACHE] = cacheGroup

        val processGroup = JunkGroup()
        processGroup.mName = getString(R.string.process_clean)
        processGroup.mChildren = arrayListOf()
        mJunkGroups!![JunkGroup.GROUP_PROCESS] = processGroup

        val apkGroup = JunkGroup()
        apkGroup.mName = getString(R.string.apk_clean)
        apkGroup.mChildren = arrayListOf()
        mJunkGroups!![JunkGroup.GROUP_APK] = apkGroup

        val tmpGroup = JunkGroup()
        tmpGroup.mName = getString(R.string.tmp_clean)
        tmpGroup.mChildren = arrayListOf()
        mJunkGroups!![JunkGroup.GROUP_TMP] = tmpGroup

        val logGroup = JunkGroup()
        logGroup.mName = getString(R.string.log_clean)
        logGroup.mChildren = arrayListOf()
        mJunkGroups!![JunkGroup.GROUP_LOG] = logGroup
    }

    private fun checkScanFinish() {

        mAdapter!!.notifyDataSetChanged()

        if (mIsProcessScanFinish && mIsSysCacheScanFinish && mIsOverallScanFinish) {
            mIsScanning = false

            val cacheGroup = mJunkGroups!!.get(JunkGroup.GROUP_CACHE)
            var children = cacheGroup!!.mChildren
            cacheGroup.mChildren = arrayListOf()
            for (info in children!!) {
                cacheGroup!!.mChildren!!.add(info)
                if (info.mChildren != null) {
                    cacheGroup!!.mChildren!!.addAll(info.mChildren)
                }
            }
            children = null

            val size = getTotalSize()
            val totalSize = CleanUtil.formatShortFileSize(this, size)
            mHeaderView!!.mSize.setText(totalSize)
            mHeaderView!!.mProgress.setText("Found together:$totalSize")
            mHeaderView!!.mProgress.setGravity(Gravity.CENTER)

            mCleanButton!!.setEnabled(true)
        }
    }

    private fun checkCleanFinish() {
        if (mIsProcessCleanFinish && mIsSysCacheCleanFinish && mIsOverallCleanFinish) {
            mHeaderView!!.mProgress.setText("Clean up")
            mHeaderView!!.mSize.setText(CleanUtil.formatShortFileSize(this, 0L))

            for (group in mJunkGroups!!.values) {
                group.mSize = 0L
                group.mChildren = null
            }

            mAdapter!!.notifyDataSetChanged()
        }
    }

    private fun startScan() {

        val processScanTask = ProcessScanTask(object : IScanCallback {
            override fun onBegin() {
                val msg = handler!!.obtainMessage(MSG_PROCESS_BEGIN)
                msg.sendToTarget()
            }

            override fun onProgress(info: JunkInfo) {
                val msg = handler!!.obtainMessage(MSG_PROCESS_POS)
                msg.obj = info
                msg.sendToTarget()
            }

            override fun onFinish(children: ArrayList<JunkInfo>) {
                val cacheGroup = mJunkGroups!!.get(JunkGroup.GROUP_PROCESS)
                cacheGroup!!.mChildren!!.addAll(children)
                for (info in children) {
                    cacheGroup.mSize += info.mSize
                }
                val msg = handler!!.obtainMessage(MSG_PROCESS_FINISH)
                msg.sendToTarget()
            }
        })
        processScanTask.execute()

        val sysCacheScanTask = SysCacheScanTask(object : IScanCallback {
            override fun onBegin() {
                val msg = handler!!.obtainMessage(MSG_SYS_CACHE_BEGIN)
                msg.sendToTarget()
            }

            override fun onProgress(info: JunkInfo) {
                val msg = handler!!.obtainMessage(MSG_SYS_CACHE_POS)
                msg.obj = info
                msg.sendToTarget()
            }

            override fun onFinish(children: ArrayList<JunkInfo>) {
                val cacheGroup = mJunkGroups!!.get(JunkGroup.GROUP_CACHE)
                cacheGroup!!.mChildren!!.addAll(children)
                Collections.sort(cacheGroup.mChildren)
                Collections.reverse(cacheGroup.mChildren)
                for (info in children) {
                    cacheGroup.mSize += info.mSize
                }
                val msg = handler!!.obtainMessage(MSG_SYS_CACHE_FINISH)
                msg.sendToTarget()
            }
        })
        sysCacheScanTask.execute()

        val overallScanTask = OverallScanTask(object : IScanCallback {
            override fun onBegin() {
                val msg = handler!!.obtainMessage(MSG_OVERALL_BEGIN)
                msg.sendToTarget()
            }

            override fun onProgress(info: JunkInfo) {
                val msg = handler!!.obtainMessage(MSG_OVERALL_POS)
                msg.obj = info
                msg.sendToTarget()
            }

            override fun onFinish(children: ArrayList<JunkInfo>) {
                for (info in children) {
                    val path = info.mChildren.get(0).mPath
                    var groupFlag = 0
                    if (path!!.endsWith(".apk")) {
                        groupFlag = JunkGroup.GROUP_APK
                    } else if (path!!.endsWith(".log")) {
                        groupFlag = JunkGroup.GROUP_LOG
                    } else if (path!!.endsWith(".tmp") || path!!.endsWith(".temp")) {
                        groupFlag = JunkGroup.GROUP_TMP
                    }

                    val cacheGroup = mJunkGroups!!.get(groupFlag)
                    cacheGroup!!.mChildren!!.addAll(info.mChildren)
                    cacheGroup!!.mSize = info.mSize
                }

                val msg = handler!!.obtainMessage(MSG_OVERALL_FINISH)
                msg.sendToTarget()
            }
        })
        overallScanTask.execute()
    }

    private fun getTotalSize(): Long {
        var size = 0L
        for (group in mJunkGroups!!.values) {
            size += group.mSize
        }
        return size
    }

    class GroupViewHolder {
        var mPackageNameTv: TextView? = null
        var mPackageSizeTv: TextView? = null
    }

    class ChildViewHolder {
        var mJunkTypeTv: TextView? = null
        var mJunkSizeTv: TextView? = null
    }

}
