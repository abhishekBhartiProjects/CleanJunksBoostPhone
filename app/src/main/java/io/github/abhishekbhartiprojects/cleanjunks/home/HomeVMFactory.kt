package io.github.abhishekbhartiprojects.cleanjunks.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeVMFactory(context: Context): ViewModelProvider.NewInstanceFactory() {

    val repository = HomeRepository(context)

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeVM(repository) as T
    }
}