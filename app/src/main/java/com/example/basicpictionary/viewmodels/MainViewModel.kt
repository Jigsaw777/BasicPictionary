package com.example.basicpictionary.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.basicpictionary.entities.ImageEntity
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class MainViewModel : ViewModel() {

    var roundNumber = 1
    var noCorrect = 0

    var list = mutableListOf<ImageEntity>()
    var visited = mutableListOf<ImageEntity>()
    lateinit var arr: ArrayList<Pair<Int, Int>>
    val round = MutableLiveData<Int>()
    val finishGame=MutableLiveData<Boolean>()
    var maxLevels = 10
    var difficulty = 1
    var score = 5


    fun init(listIP: List<ImageEntity>) {
        list = listIP.toMutableList()
        arr = ArrayList()
        var start = 0
        for (i in 0 until list.size) {
            if (i == list.size - 1) {
                arr.add(start to i)
            } else {
                if (list[i + 1].difficulty > list[i].difficulty) {
                    arr.add(start to i)
                    start = i + 1
                }
            }
        }
    }

    fun checkDiffAndSendImage(): ImageEntity? {
        var removed: ImageEntity? = null
        for (i in 0 until arr.size) {
            Log.d("vm", "start : ${arr[i].first}")
            Log.d("vm", "end : ${arr[i].second}")
            if(arr[i].first>arr[i].second)
                finishGame.postValue(true)
            else if (list[arr[i].second].difficulty == difficulty) {
                val pos = Random().nextInt(arr[i].second - arr[i].first + 1) + arr[i].first
                removed = list.removeAt(pos)
                updatePairList(i)
                visited.add(removed)
                break
            }
        }
        return removed
    }

    fun clear() {
        visited.clear()
        roundNumber = 1
        score = 3
        difficulty=1
        arr.clear()
        finishGame.postValue(false)
    }

    private fun updatePairList(position: Int){
        for(i in position until arr.size){
            val pair = arr[i]
//            if(pair.first.absoluteValue>pair.second.absoluteValue)
//                finishGame.postValue(true)
//            val startPos = if (pair.first != 0) pair.first - 1 else pair.first
            val new = pair.first to pair.second - 1
            arr.removeAt(i)
            arr.add(i, new)
        }
    }
}