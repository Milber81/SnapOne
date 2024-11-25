package com.snapone.weatherproject.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.snapone.weatherproject.R
import com.snapone.weatherproject.databinding.ActivityMainBinding
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.ui.AddCityFragment
import com.snapone.weatherproject.ui.SharedViewModel
import com.snapone.weatherproject.ui.UiModule
import com.snapone.weatherproject.ui.dailyDetails.DailyDetailsFragment
import com.snapone.weatherproject.usecases.TurnOnGpsUseCase
import com.snapone.weatherproject.utils.GPS_REQUEST_CODE
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var adapter: CitiesAdapter? = null
    private val viewModel: MainViewModel by lazy {
        MainModule.provideMainViewModel()
    }

    private val sharedViewModel: SharedViewModel by lazy {
        UiModule.provideMainViewModel
    }

    private lateinit var binding: ActivityMainBinding

    private var isFirstAppStart: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        createListeners()

        createObservers()

        binding.addCityButton.setOnClickListener {
            val addCityFragment = AddCityFragment()

            if (addCityFragment.isAdded) {
                addCityFragment.dismiss()
            }

            addCityFragment.show(supportFragmentManager, AddCityFragment.TAG)
        }
    }

    fun addCity(city: City) {
        viewModel.addCity(city)
    }

    private fun removeCity(city: CityViewItem) {
        viewModel.removeCity(city)
    }

    private fun createListeners() {
        binding.gpsButton.setOnClickListener {
            if (viewModel.isNetworkAvailable(this)) {
                checkGPSPermission()
            } else {
                showNoInternetMessage()
            }
        }
    }

    private fun createObservers() {
        viewModel.loadingState.observe(this) {
            if (it)
                showProgressBar()
            else
                hideProgressBar()
        }

        lifecycleScope.launch {
            viewModel.cities.collect {
                println("oooooo -------------->>> $it")
                if (adapter == null) {
                    val layoutManager = LinearLayoutManager(this@MainActivity)
                    binding.rec.layoutManager = layoutManager
                }
                it.let {
                    adapter = CitiesAdapter(it.toMutableList(), {
                        showDailyDetails(it)
                    }, {
                        removeCity(it)
                    })
                    binding.rec.adapter = adapter
                }
            }
        }

        // Collect in the observer
        lifecycleScope.launch {
            viewModel.cityUpdate.collect { cityUpdate ->
                println("oooooo Updated city: $cityUpdate")
                adapter?.updateCity(cityUpdate)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.isNetworkAvailable(this)) {
            if (isFirstAppStart) {
                //Selects the user's preferred language as default language
                AppCompatDelegate.getApplicationLocales()

                showSpecialMessage()
            }
        } else {
            showNoInternetMessage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            GPS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val locationRequestBuilder: LocationRequest.Builder =
                        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                    locationRequestBuilder.setMinUpdateIntervalMillis(2000)

                    val locationRequest: LocationRequest = locationRequestBuilder.build()

                }
                return
            }
        }
    }

    /*************************PRIVATE FUNCTIONS*************************/

    private fun showSpecialMessage() {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(this, "Error happened", Toast.LENGTH_LONG).show()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showDailyDetails(
        cityViewItem: CityViewItem
    ) {

        lifecycleScope.launch {
            viewModel.city.collect { city ->
                sharedViewModel.data.postValue(city)
                this.cancel() // Cancels the collection
                val dailyDetailsFragment = DailyDetailsFragment()

                if (dailyDetailsFragment.isAdded) {
                    dailyDetailsFragment.dismiss()
                }

                dailyDetailsFragment.show(supportFragmentManager, DailyDetailsFragment.TAG)

            }
        }
        viewModel.getCity(cityViewItem)
    }

    private fun showNoInternetMessage() {
        showSpecialMessage()
    }

    override fun onDestroy() {

        super.onDestroy()
        Glide.get(this).clearMemory()
    }

    private fun checkGPSPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequestBuilder: LocationRequest.Builder =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            locationRequestBuilder.setMinUpdateIntervalMillis(2000)

            val locationRequest: LocationRequest = locationRequestBuilder.build()
            if (!isGPSEnabled()) {
                val turnOnGpsUseCase = TurnOnGpsUseCase(this)
                turnOnGpsUseCase.turnOnGPS(locationRequest)
            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), GPS_REQUEST_CODE
            )
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager: LocationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}

