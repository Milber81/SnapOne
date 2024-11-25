package com.snapone.weatherproject.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snapone.weatherproject.R
import com.snapone.weatherproject.databinding.AddCityBinding
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.ui.main.MainActivity

class AddCityFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "AddCityFragment"
    }

    private lateinit var binding: AddCityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = AddCityBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val styleResId = R.style.ModalBottomSheetDialog
        setStyle(DialogFragment.STYLE_NORMAL, styleResId)

        return BottomSheetDialog(requireContext(), styleResId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bntApply.setOnClickListener {

            val cityName = binding.edtCityName.text
            val cityLat = binding.edtLatitude.text.toString()
            val cityLng = binding.edtLongitude.text.toString()
            val cityState = binding.edtState.text
            val cityAbbr = binding.edtStateAbbr.text

            if (cityName.isEmpty()
                || cityLat.isEmpty()
                || cityLng.isEmpty()
                || cityState.isEmpty()
                || cityAbbr.isEmpty()
            )
                return@setOnClickListener

            val city = City(cityName.toString(),
                cityLat.toFloat(),
                cityLng.toFloat(),
                cityState.toString(),
                cityAbbr.toString()
            )

            (requireActivity() as MainActivity).addCity(city)
            dismiss()
        }
    }

}
