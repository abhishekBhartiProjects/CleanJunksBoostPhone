package io.github.abhishekbhartiprojects.cleanjunks.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.github.abhishekbhartiprojects.cleanjunks.R
import io.github.abhishekbhartiprojects.cleanjunks.base.BaseActivity
import io.github.abhishekbhartiprojects.cleanjunks.home.HomeActivity
import io.github.abhishekbhartiprojects.cleanjunks.junkClean.JunkCleanActivity

class MainActivity : BaseActivity() {

    companion object {
        val PARAM_TOTAL_SPACE = "total_space"
        val PARAM_USED_SPACE = "used_space"
        val PARAM_TOTAL_MEMORY = "total_memory"
        val PARAM_USED_MEMORY = "used_memory"
    }

    private var mJunkCleanButton: Button? = null
    private var mProcessClean: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener(View.OnClickListener { view ->
            Snackbar.make(view, "contact: http://mazhuang.org", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        })

        mJunkCleanButton = findViewById(R.id.junk_clean) as Button
        mJunkCleanButton!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, JunkCleanActivity::class.java)
            startActivity(intent)
        })

        mProcessClean = findViewById(R.id.process_clean) as Button
        mProcessClean!!.setOnClickListener({
            var intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }
}
