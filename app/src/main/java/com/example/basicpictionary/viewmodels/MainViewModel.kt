package com.example.basicpictionary.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.basicpictionary.entities.ImageEntity
import java.util.*

class MainViewModel : ViewModel(){

    var roundNumber=1

    var list= mutableListOf<ImageEntity>()
    var visited = mutableListOf<ImageEntity>()
    val round=MutableLiveData<Int>()
    var maxLevels=0

    var level=10


    fun getImageEntity(): ImageEntity{
        val pos=Random().nextInt(list.size)
        val removed = list.removeAt(pos)
        visited.add(removed)
        return removed
    }

    fun clear(){
        visited.clear()
        roundNumber=1
        level=10
    }
}