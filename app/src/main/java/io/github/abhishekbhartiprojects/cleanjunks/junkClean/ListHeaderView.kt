package io.github.abhishekbhartiprojects.cleanjunks.junkClean

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import io.github.abhishekbhartiprojects.cleanjunks.R

class ListHeaderView(context: Context, listView: ViewGroup): RelativeLayout(context) {
    private val mContext: Context
    var mSize: TextView
    var mProgress: TextView

    init {
        this.mContext = context
        val view = LayoutInflater.from(this.mContext).inflate(R.layout.list_header_view, listView, false)
        addView(view)
        mSize = findViewById(R.id.total_size) as TextView
        mProgress = findViewById(R.id.progress_msg) as TextView
    }



}