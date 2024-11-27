package com.snapone.weatherproject.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snapone.weatherproject.R
import com.snapone.weatherproject.databinding.AddCityBinding
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.ui.helpers.cities
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

        binding.edtCityName.requestFocus()

        fillCityViews()

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

    private fun fillCityViews(){
        val linearLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        cities.forEach { city ->
            val textView = TextView(requireContext()).apply {
                text = city.name
                textSize = 16f
                setPadding(16, 16, 16, 16)
                // Set a background with a solid color and rounded corners
                setOnClickListener {
                    binding.edtCityName.setText(city.name)
                    binding.edtLatitude.setText(city.latitude.toString())
                    binding.edtLongitude.setText(city.longitude.toString())
                    binding.edtState.setText(city.country)
                    binding.edtStateAbbr.setText(city.countryAbbr)

                    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(binding.edtState.windowToken, 0)
                }
            }
            linearLayout.addView(textView)
        }

        binding.cities.addView(linearLayout)
    }

}
