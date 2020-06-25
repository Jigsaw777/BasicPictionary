package com.example.basicpictionary.views

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.basicpictionary.R
import com.example.basicpictionary.constants.AppConstants
import com.example.basicpictionary.entities.ImageEntity
import com.example.basicpictionary.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : Fragment() {

    private var currentImage: ImageEntity? = null
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

        viewModel.finishGame.observe(this, Observer {
            if (it)
                fragmentManager?.popBackStack()
        })

        timer = object : CountDownTimer(40000L, 1000L) {
            override fun onFinish() {
                isTimerRunning = false
                processAnswer()
            }

            override fun onTick(millisUntilFinished: Long) {
                isTimerRunning = true
                time_text.text = (millisUntilFinished / 1000).toString()
            }

        }
    }

    override fun onDestroyView() {
        timer.cancel()
        isTimerRunning=false
        super.onDestroyView()
    }

    private fun processAnswer() {
        val name = enter_text.text.toString()
        if (name.equals(currentImage?.answer, true)) {
            viewModel.noCorrect += 1
            if (viewModel.noCorrect % 2 == 0 && viewModel.difficulty<5)
                viewModel.difficulty += 1
            gotoNextImages(true)
        } else {
            if (name.trim().isEmpty() && isTimerRunning) {
                Toast.makeText(requireContext(), AppConstants.EMPTY_WARNING, Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (viewModel.difficulty > 1)
                    viewModel.difficulty -= 1
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
            viewModel.score += 1
        else
            viewModel.score -= 1
        viewModel.round.postValue(++viewModel.roundNumber)
        checkLevel()
        enter_text.text?.clear()
        if (viewModel.roundNumber <= viewModel.maxLevels)
            setImage()
        resetTimer()
    }

    private fun checkLevel() {
        if (viewModel.score == viewModel.maxLevels) {
            timer.cancel()
            isTimerRunning = false
            Toast.makeText(
                requireContext(),
                AppConstants.WIN_TEXT,
                Toast.LENGTH_LONG
            ).show()
        }

        if (viewModel.score == 0) {
            Toast.makeText(
                requireContext(),
                AppConstants.LOSE_TEXT,
                Toast.LENGTH_LONG
            ).show()
            viewModel.finishGame.postValue(true)
        }
    }

    private fun setRound(round: Int) {
        viewModel.round.postValue(round)
    }

    private fun setImage() {
        currentImage = viewModel.checkDiffAndSendImage()
        val scoreStr = "Your Current score is : ${viewModel.score}"
        score_text.text = scoreStr
        Glide.with(this)
            .load(currentImage?.imageUrl)
            .into(action_image)
    }

}