package io.github.abhishekbhartiprojects.cleanjunks.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import io.github.abhishekbhartiprojects.cleanjunks.R

class HomeActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView

    lateinit var viewModel: HomeVM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home)

        subscribeVM()
        subscribeUI()
    }

    override fun onStart() {
        super.onStart()

        getAllProcessData()
    }

    private fun subscribeVM() {
        val factory = HomeVMFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(HomeVM::class.java)
    }

    private fun subscribeUI() {
        viewModel.allProcessDetails.observe(this, Observer { handleAllProcessDetails(it) })
    }

    //Calls
    private fun getAllProcessData(){
        viewModel.getAllProcessDetails()
    }

    //Handle response
    private fun handleAllProcessDetails(any: Any){

    }


}
