package com.example.basicpictionary.views

import android.os.Bundle
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
    }

    private fun initListeners() {
        next_button.setOnClickListener {
            val name = enter_text.text.toString()
            viewModel.round.postValue(++viewModel.roundNumber)
            if (name.equals(currentImage.answer, true)) {
                viewModel.level += 1
            } else {
                viewModel.level -= 1
            }
            checkLevel()
            enter_text.text?.clear()
            if (viewModel.roundNumber <= viewModel.maxLevels)
                setImage()

        }
    }

    private fun checkLevel() {
        if (viewModel.level == viewModel.maxLevels) {
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