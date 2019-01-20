package io.github.abhishekbhartiprojects.cleanjunks.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
//import butterknife.BindView
//import butterknife.ButterKnife
import io.github.abhishekbhartiprojects.cleanjunks.R

class HomeActivity : AppCompatActivity() {

//    @BindView(R.id.listContainerRV)
//    lateinit var recyclerView: RecyclerView

    lateinit var viewModel: HomeVM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home)
//        ButterKnife.bind(this)

        subscribeVM()
        subscribeUI()
    }

    private fun subscribeVM() {
        val factory = HomeVMFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(HomeVM::class.java)
    }

    private fun subscribeUI() {

    }


}
