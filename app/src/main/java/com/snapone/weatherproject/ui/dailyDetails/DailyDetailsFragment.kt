package com.snapone.weatherproject.ui.dailyDetails

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snapone.weatherproject.R
import com.snapone.weatherproject.databinding.FragmentDailyDetailsBinding
import com.snapone.weatherproject.ui.UiModule

class DailyDetailsFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentDailyDetailsBinding

    companion object {
        const val TAG = "dailyDetailsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vm = UiModule.provideMainViewModel

        vm.data.observe(this){
            println("oooooo --------------- >>>>> $it")
            binding.cityName.text = it.name
            binding.averageTemperature.text = it.forecastData?.currentTemperature.toString()
            binding.lowestTemp.text = it.forecastData?.low.toString()
            binding.highestTemp.text = it.forecastData?.high.toString()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val styleResId: Int = R.style.ModalBottomSheetDialog

        setStyle(DialogFragment.STYLE_NORMAL, styleResId)

        return BottomSheetDialog(requireContext(), styleResId)
    }

    override fun onDestroyView() {
        binding.dayImage.setImageBitmap(null)
        binding.cityName.text = null
        binding.averageTemperature.text = null
        binding.lowestTemp.text = null
        binding.highestTemp.text = null

        super.onDestroyView()
    }
}
