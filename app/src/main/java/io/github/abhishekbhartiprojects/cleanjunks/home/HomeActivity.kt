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

    lateinit var viewModel: HomeVM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home)

        subscribeVM()
        subscribeObserver()
        initUI()

    }

    override fun onStart() {
        super.onStart()

        getAllProcessData()
    }

    private fun subscribeVM() {
        val factory = HomeVMFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(HomeVM::class.java)
    }

    private fun subscribeObserver() {
        viewModel.allProcessDetails.observe(this, Observer { handleAllProcessDetails(it) })
    }

    private fun initUI(){
    }

    //Calls
    private fun getAllProcessData(){
        viewModel.getAllProcessDetails()
    }

    //Handle response
    private fun handleAllProcessDetails(any: Any){

    }

    private fun startPhoneBoost(){

    }

    private fun startTrashCleaner(){

    }

    private fun startCPUCooler(){

    }

    private fun startSecurity(){

    }

    private fun startAd1(){
        //ad
    }

    private fun startAd2(){
        //ad
    }

    private fun startNetworkBoost(){

    }

    private fun startDuSwipe(){

    }

    private fun startSimilarPicture(){

    }

    private fun startAppNotCommonlyUsed(){

    }



}
