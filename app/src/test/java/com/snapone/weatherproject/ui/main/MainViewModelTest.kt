package com.snapone.weatherproject.ui.main


import android.os.Handler
import android.os.Looper
import com.snapone.weatherproject.base.ListMapper
import com.snapone.weatherproject.base.Merger
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.domain.ForecastData
import com.snapone.weatherproject.domain.repositories.CitiesRepository
import com.snapone.weatherproject.usecases.GetCityInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import io.mockk.*
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
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

        mockkStatic(Looper::class)
        val mockLooper = mockk<Looper>(relaxed = true)
        every { Looper.getMainLooper() } returns mockLooper
        every { mockLooper.thread } returns Thread.currentThread()

        mockkConstructor(Handler::class)
        every { anyConstructed<Handler>().post(any()) } answers {
            firstArg<Runnable>().run()
            true
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Looper::class)
        unmockkConstructor(Handler::class)
        unmockkAll()
    }

    @Test
    fun `getCities fetches data and updates SharedFlow correctly`() = runTest {
        val mockCities = listOf(
            City("City1", 33f, 44f, "Serbia", "RS"), City("City2", 34f, 45f, "Serbia", "RS")
        )
        val mockCityViewItems = listOf(
            CityViewItem(1, "City1", "icon1", "33"), CityViewItem(2, "City2", "icon2", "34")
        )

        coEvery { citiesRepository.getAllCities() } returns mockCities
        every { listMapper.map(mockCities) } returns mockCityViewItems

        val emittedCities = mutableListOf<Pair<UpdateDataPolicy, List<CityViewItem>>>()
        val job = launch {
            viewModel.cities.take(1)
                .collect { emittedCities.add(it) }
        }

        viewModel = MainViewModel(
            citiesRepository = citiesRepository,
            getCityInfoUseCase = getCityInfoUseCase,
            dispatcher = testDispatcher,
            listMapper = listMapper,
            merger = merger
        )

        runCurrent() // Run coroutines

        assertEquals(UpdateDataPolicy.SOURCE, emittedCities.first().first)
        assertEquals(mockCityViewItems, emittedCities.first().second)

        coVerify(exactly = 1) { citiesRepository.getAllCities() }
        verify(exactly = 1) { listMapper.map(mockCities) }

        job.cancel() // Clean up collection
    }


    @Test
    fun `addCity adds a city and updates StateFlow correctly`() = runTest(timeout = 10.seconds) {
        val mockCities = listOf(
            City("City1", 33f, 44f, "Serbia", "RS"), City("City2", 34f, 45f, "Serbia", "RS")
        )
        val mockCityViewItems = listOf(
            CityViewItem(1, "City1", "icon1", "33"), CityViewItem(2, "City2", "icon2", "34")
        )

        val newCity = City("City3", 35f, 46f, "Serbia", "RS")
        val newCityViewItem = CityViewItem(3, "City3", "icon3", "35")

        coEvery { citiesRepository.getAllCities() } returns mockCities
        every { listMapper.map(mockCities) } returns mockCityViewItems
        coEvery { citiesRepository.addCity(newCity) } just Runs
        every { listMapper.map(listOf(newCity)) } returns listOf(newCityViewItem)

        viewModel = MainViewModel(
            citiesRepository = citiesRepository,
            getCityInfoUseCase = getCityInfoUseCase,
            dispatcher = testDispatcher,
            listMapper = listMapper,
            merger = merger
        )

        val emittedCities = mutableListOf<Pair<UpdateDataPolicy, List<CityViewItem>>>()
        val job = launch {
            viewModel.cities.take(1)
                .collect { emittedCities.add(it) }
        }

        viewModel.addCity(newCity)
        runCurrent()

        assertEquals(UpdateDataPolicy.ADD, emittedCities.first().first)
        assertEquals(listOf(newCityViewItem), emittedCities.first().second)

        coVerify(exactly = 1) { citiesRepository.addCity(newCity) }
        verify(exactly = 1) { listMapper.map(listOf(newCity)) }

        job.cancel()
    }

    @Test
    fun `removeCity removes a city and updates StateFlow correctly`() =
        runTest(timeout = 10.seconds) {
            val mockCities = listOf(
                City("City1", 33f, 44f, "Serbia", "RS"),
                City("City2", 34f, 45f, "Serbia", "RS"),
                City("City3", 35f, 46f, "Serbia", "RS")
            )
            val mockCityViewItems = listOf(
                CityViewItem(1, "City1", "icon1", "33"),
                CityViewItem(2, "City2", "icon2", "34"),
                CityViewItem(3, "City3", "icon3", "35")
            )

            val newCity = City("City3", 35f, 46f, "Serbia", "RS")
            val newCityViewItem = CityViewItem(3, "City3", "icon3", "35")

            coEvery { citiesRepository.getAllCities() } returns mockCities
            every { listMapper.map(mockCities) } returns mockCityViewItems
            coEvery { citiesRepository.removeCity(newCity) } just Runs

            viewModel = MainViewModel(
                citiesRepository = citiesRepository,
                getCityInfoUseCase = getCityInfoUseCase,
                dispatcher = testDispatcher,
                listMapper = listMapper,
                merger = merger
            )

            val emittedCities = mutableListOf<Pair<UpdateDataPolicy, List<CityViewItem>>>()
            val job = launch {
                viewModel.cities.take(1)
                    .collect { emittedCities.add(it) }
            }

            viewModel.removeCity(city = newCityViewItem)
            runCurrent()

            assertEquals(UpdateDataPolicy.REMOVE, emittedCities.first().first)
            assertEquals(listOf(newCityViewItem), emittedCities.first().second)

            coVerify(exactly = 1) { citiesRepository.removeCity(newCity) }

            job.cancel()
        }
}
