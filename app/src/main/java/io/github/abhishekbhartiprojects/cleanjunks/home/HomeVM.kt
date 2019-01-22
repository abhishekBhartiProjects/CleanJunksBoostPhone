package io.github.abhishekbhartiprojects.cleanjunks.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeVM(repository: HomeRepository): ViewModel() {
    var allProcessDetails: MutableLiveData<Any> = MutableLiveData()


    fun getAllProcessDetails(){

    }
}