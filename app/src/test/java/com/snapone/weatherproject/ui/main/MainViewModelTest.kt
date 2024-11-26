package com.snapone.weatherproject.ui.main



import android.os.Looper
import com.snapone.weatherproject.base.ListMapper
import com.snapone.weatherproject.base.Merger
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.domain.ForecastData
import com.snapone.weatherproject.domain.repositories.CitiesRepository
import com.snapone.weatherproject.usecases.GetCityInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import io.mockk.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val citiesRepository = mockk<CitiesRepository>()
    private val listMapper = mockk<ListMapper<City, CityViewItem>>()
    private val getCityInfoUseCase = mockk<GetCityInfoUseCase>()
    private val merger = mockk<Merger<ForecastData, City>>()

    private lateinit var viewModel: MainViewModel


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mockkStatic(Looper::class) // This will mock Looper.getMainLooper()
        every { Looper.getMainLooper() } returns mockk()
    }

    @Test
    fun `getCities fetches data and updates StateFlow correctly`() = runTest {
        // Arrange
        val mockCities = listOf(
            City("City1", 33f, 44f, "Serbia", "RS"),
            City("City2", 34f, 45f, "Serbia", "RS")
        )

        val mockCityViewItems = listOf(
            CityViewItem(1, "City1", "icon1", "33"),
            CityViewItem(2, "City2", "icon2", "34")
        )

        coEvery { citiesRepository.getAllCities() } returns mockCities
        every { listMapper.map(mockCities) } returns mockCityViewItems

        viewModel = MainViewModel(
            citiesRepository = citiesRepository,
            getCityInfoUseCase = getCityInfoUseCase,
            dispatcher = testDispatcher,
            listMapper = listMapper,
            merger = merger
        )

        runCurrent()

        val emittedCities = viewModel.cities.first()
        assertEquals(mockCityViewItems, emittedCities)

        coVerify(exactly = 1) { citiesRepository.getAllCities() }
        verify(exactly = 1) { listMapper.map(mockCities) }
    }



}
