package com.example.basicpictionary.views

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.basicpictionary.R
import com.example.basicpictionary.constants.AppConstants
import com.example.basicpictionary.entities.ImageEntity
import com.example.basicpictionary.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : Fragment() {

    private lateinit var currentImage: ImageEntity
    private val viewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }
    private lateinit var timer: CountDownTimer
    private var isTimerRunning=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRound(viewModel.roundNumber)
        setImage()
        initListeners()
        startTimer()
    }

    private fun initListeners() {
        next_button.setOnClickListener {
            processAnswer()
        }

        timer = object : CountDownTimer(40000L, 1000L) {
            override fun onFinish() {
                isTimerRunning=false
                processAnswer()
            }

            override fun onTick(millisUntilFinished: Long) {
                isTimerRunning=true
                time_text.text=(millisUntilFinished/1000).toString()
            }

        }
    }

    override fun onDestroyView() {
        timer.cancel()
        isTimerRunning=false
        super.onDestroyView()
    }

    private fun processAnswer(){
        val name = enter_text.text.toString()
        if (name.equals(currentImage.answer, true)) {
            gotoNextImages(true)
        } else {
            if (name.trim().isEmpty() && isTimerRunning) {
                Toast.makeText(requireContext(), AppConstants.EMPTY_WARNING, Toast.LENGTH_SHORT)
                    .show()
            } else {
                gotoNextImages(false)
            }
        }
    }

    private fun startTimer() {
        timer.start()
    }

    private fun resetTimer() {
        timer.cancel()
        timer.start()
    }

    private fun gotoNextImages(isCorrectAnswer: Boolean) {
        if (isCorrectAnswer)
            viewModel.level += 1
        else
            viewModel.level -= 1
        viewModel.round.postValue(++viewModel.roundNumber)
        checkLevel()
        enter_text.text?.clear()
        if (viewModel.roundNumber <= viewModel.maxLevels)
            setImage()
        resetTimer()
    }

    private fun checkLevel() {
        if (viewModel.level == viewModel.maxLevels) {
            timer.cancel()
            isTimerRunning=false
            Toast.makeText(
                requireContext(),
                AppConstants.WIN_TEXT,
                Toast.LENGTH_LONG
            ).show()
        }

        if (viewModel.level == 0) {
            Toast.makeText(
                requireContext(),
                AppConstants.LOSE_TEXT,
                Toast.LENGTH_LONG
            ).show()
            fragmentManager?.popBackStack()
        }
    }

    private fun setRound(round: Int) {
        viewModel.round.postValue(round)
    }

    private fun setImage() {
        currentImage = viewModel.getImageEntity()
        Glide.with(this)
            .load(currentImage.imageUrl)
            .into(action_image)
    }

}