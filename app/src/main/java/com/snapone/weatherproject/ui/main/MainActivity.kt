package com.snapone.weatherproject.ui.main

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.snapone.weatherproject.databinding.ActivityMainBinding
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.ui.AddCityFragment
import com.snapone.weatherproject.ui.SharedViewModel
import com.snapone.weatherproject.ui.UiModule
import com.snapone.weatherproject.ui.dailyDetails.DailyDetailsFragment
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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rec) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
                topMargin = insets.top
            }

            WindowInsetsCompat.CONSUMED
        }

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

    private fun createObservers() {
        viewModel.loadingState.observe(this) {
            if (it) showProgressBar()
            else hideProgressBar()
        }

        lifecycleScope.launch {
            viewModel.cities.collect { dataPair ->

                if (adapter == null) {
                    val layoutManager = LinearLayoutManager(this@MainActivity)
                    binding.rec.layoutManager = layoutManager
                }

                println("oooooo --------------- $dataPair")

                dataPair.second.let { cityViewItems ->

                    when (dataPair.first) {
                        is UpdateDataPolicy.ADD -> adapter?.updateCity(cityViewItems[0])
                        is UpdateDataPolicy.REMOVE -> adapter?.removeCity(cityViewItems[0])
                        is UpdateDataPolicy.FULL_SOURCE -> {
                            adapter = CitiesAdapter(cityViewItems.toMutableList(), {
                                showDailyDetails(it)
                            }, {
                                removeCity(it)
                            })
                            binding.rec.adapter = adapter
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.cityUpdate.collect { cityUpdate ->
                adapter?.updateCity(cityUpdate)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.isNetworkAvailable(this)) {
            if (isFirstAppStart) {
                AppCompatDelegate.getApplicationLocales()

                showSpecialMessage()
            }
        } else {
            showNoInternetMessage()
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
                city?.let {
                    sharedViewModel.postCity(it)
                    this.cancel() // Cancels the coroutine
                    val dailyDetailsFragment = DailyDetailsFragment()

                    if (dailyDetailsFragment.isAdded) {
                        dailyDetailsFragment.dismiss()
                    }

                    dailyDetailsFragment.show(supportFragmentManager, DailyDetailsFragment.TAG)
                } ?: run {
                    Toast.makeText(
                        this@MainActivity, "Forecast data not available", Toast.LENGTH_LONG
                    ).show()
                }
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
}

