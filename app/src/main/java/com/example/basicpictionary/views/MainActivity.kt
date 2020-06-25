package com.example.basicpictionary.views

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.basicpictionary.R
import com.example.basicpictionary.entities.ImageEntity
import com.example.basicpictionary.viewmodels.MainViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.util.*
import kotlin.Comparator

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    var isRunning:Boolean=false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListener()
    }

    private fun initListener() {
        start_game.setOnClickListener {

            showProgress()

            val handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    if (msg.what == 1) {
                        setData(msg.obj as List<ImageEntity>)
                        dismissProgress()
                        moveToFragment()
                        isRunning=false
                    }
                    super.handleMessage(msg)
                }
            }
            val thread = Thread(JsonProcessor(this, handler))
            if (!isRunning) {
                isRunning=true
                thread.start()
            }
        }

    }

    private fun setData(list: List<ImageEntity>) {
        viewModel.list = list.toMutableList()
    }

    private fun showProgress() {
        loading.visibility = View.VISIBLE
    }

    private fun dismissProgress() {
        loading.visibility = View.GONE
    }

    private fun moveToFragment() {
        Log.d("act", viewModel.list[0].imageUrl)
    }
}

class JsonProcessor(private val context: Context, private val handler: Handler) : Runnable {
    override fun run() {
        try {
            val msg = handler.obtainMessage()
            msg.what = 1
            msg.obj = returnList()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    handler.sendMessage(msg)
                }
            }, 3000)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun loadJsonFromAsset(): String? {
        val ipStream = context.assets.open("pictionary.json")
        val size = ipStream.available()
        val bytes = ByteArray(size)
        ipStream.read(bytes)
        ipStream.close()
        return String(bytes)
    }

    private fun returnList(): List<ImageEntity> {
        val list = mutableListOf<ImageEntity>()
        val jsonArray = JSONArray(loadJsonFromAsset())
        val gson = Gson()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray[i]
            val imageEntity = gson.fromJson(obj.toString(), ImageEntity::class.java)
            list.add(imageEntity)
        }
        list.sortWith(Comparator { o1, o2 -> o1?.id?.compareTo(o2?.id ?: 0) ?: 0 })
        return list
    }
}