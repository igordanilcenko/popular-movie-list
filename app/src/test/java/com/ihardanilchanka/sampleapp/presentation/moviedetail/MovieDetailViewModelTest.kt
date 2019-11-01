package com.ihardanilchanka.sampleapp.presentation.moviedetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ihardanilchanka.sampleapp.domain.MovieInteractor
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.domain.model.MovieDetail
import com.ihardanilchanka.sampleapp.helper.TestBlockingObserver
import com.ihardanilchanka.sampleapp.helper.mockMovie
import com.ihardanilchanka.sampleapp.helper.mockMovieDetail
import com.ihardanilchanka.sampleapp.presentation.misc.StatefulLiveData.State
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.exceptions.base.MockitoException
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MovieDetailViewModelTest {

    @Mock lateinit var movieInteractor: MovieInteractor

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var movieDetailViewModel: MovieDetailViewModel
    private lateinit var movieDetailObserver: TestBlockingObserver<Pair<MovieDetail?, State>>
    private lateinit var movieDetailEventObserver: TestBlockingObserver<Movie>

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() = runBlocking<Unit> {
        Dispatchers.setMain(mainThreadSurrogate)

        movieDetailObserver = spy(TestBlockingObserver())
        movieDetailEventObserver = TestBlockingObserver()
        movieDetailViewModel = MovieDetailViewModel(movieInteractor)
        movieDetailViewModel.getMovieDetail().observeForever(movieDetailObserver)
        movieDetailViewModel.getEventOpenMovieDetail().observeForever(movieDetailEventObserver)

        whenever(movieInteractor.loadMovieDetail(any())).thenReturn(mockMovieDetail)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `Load movie detail - load movie detail on ViewModel initialized`() = runBlockingTest {
        movieDetailViewModel.initViewModel(mockMovie)

        Assert.assertEquals(
            movieDetailObserver.waitForValue(),
            null to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieDetailObserver.waitForValue(),
            mockMovieDetail to State(State.Type.READY)
        )
    }

    @Test
    fun `Load movie detail - load movie detail on ViewModel initialized just once`() =
        runBlockingTest {
            movieDetailViewModel.initViewModel(mockMovie)

            Assert.assertEquals(
                movieDetailObserver.waitForValue(),
                null to State(State.Type.LOADING)
            )
            Assert.assertEquals(
                movieDetailObserver.waitForValue(),
                mockMovieDetail to State(State.Type.READY)
            )

            movieDetailViewModel.initViewModel(mockMovie)

            verify(movieDetailObserver, times(2)).onChanged(any())
        }

    @Test
    fun `Load movie list - set error state in case of error`() = runBlockingTest {
        whenever(movieInteractor.loadMovieDetail(any())).thenThrow(MockitoException("test"))

        movieDetailViewModel.initViewModel(mockMovie)

        Assert.assertEquals(
            movieDetailObserver.waitForValue(),
            null to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieDetailObserver.waitForValue(),
            null to State(State.Type.ERROR)
        )
    }

    @Test
    fun `Load movie list - load new data on reload`() = runBlockingTest {
        whenever(movieInteractor.loadMovieDetail(any()))
            .thenThrow(MockitoException("test"))
            .thenReturn(mockMovieDetail)

        movieDetailViewModel.initViewModel(mockMovie)

        Assert.assertEquals(
            movieDetailObserver.waitForValue(),
            null to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieDetailObserver.waitForValue(),
            null to State(State.Type.ERROR)
        )

        movieDetailViewModel.onReloadDataClicked()

        Assert.assertEquals(
            movieDetailObserver.waitForValue(),
            null to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieDetailObserver.waitForValue(),
            mockMovieDetail to State(State.Type.READY)
        )
    }

    @Test
    fun `Open movie detail`() = runBlockingTest {
        movieDetailViewModel.onMovieSelected(mockMovie)
        Assert.assertEquals(
            movieDetailEventObserver.waitForValue(),
            mockMovie
        )
    }
}