package com.example.basicpictionary.viewmodels

import androidx.lifecycle.ViewModel
import com.example.basicpictionary.entities.ImageEntity

class MainViewModel : ViewModel(){

    var list= mutableListOf<ImageEntity>()

    private val level=10
}