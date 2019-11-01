package com.ihardanilchanka.sampleapp.presentation.movielist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ihardanilchanka.sampleapp.domain.MovieInteractor
import com.ihardanilchanka.sampleapp.domain.model.Movie
import com.ihardanilchanka.sampleapp.helper.TestBlockingObserver
import com.ihardanilchanka.sampleapp.helper.generateMockMovieList
import com.ihardanilchanka.sampleapp.helper.mockMovie
import com.ihardanilchanka.sampleapp.presentation.misc.StatefulLiveData.State
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import okhttp3.internal.immutableListOf
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.exceptions.base.MockitoException
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MovieListViewModelTest {

    @Mock lateinit var movieInteractor: MovieInteractor

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var movieListViewModel: MovieListViewModel
    private lateinit var movieListObserver: TestBlockingObserver<Pair<List<Movie>?, State>>
    private lateinit var movieDetailEventObserver: TestBlockingObserver<Movie>

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() = runBlocking<Unit> {
        Dispatchers.setMain(mainThreadSurrogate)

        movieListObserver = TestBlockingObserver()
        movieDetailEventObserver = TestBlockingObserver()
        movieListViewModel = MovieListViewModel(movieInteractor)
        movieListViewModel.getMovieList().observeForever(movieListObserver)
        movieListViewModel.getEventOpenMovieDetail().observeForever(movieDetailEventObserver)

        whenever(movieInteractor.loadMovieList(any(), any()))
            .thenReturn(generateMockMovieList(pages = 1))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `Load movie list - load first page data on ViewModel initialized`() = runBlockingTest {
        movieListViewModel.initViewModel()

        Assert.assertEquals(
            movieListObserver.waitForValue(),
            null to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieListObserver.waitForValue(),
            generateMockMovieList(pages = 1) to State(State.Type.READY)
        )
    }

    @Test
    fun `Load movie list - show empty state on no movies received`() = runBlockingTest {
        whenever(movieInteractor.loadMovieList(any(), any()))
            .thenReturn(immutableListOf())

        movieListViewModel.initViewModel()

        Assert.assertEquals(
            movieListObserver.waitForValue(),
            null to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieListObserver.waitForValue(),
            null to State(State.Type.EMPTY)
        )
    }

    @Test
    fun `Load movie list - load new bunch of data on RecyclerView scrolled`() = runBlockingTest {
        movieListViewModel.initViewModel()

        Assert.assertEquals(
            movieListObserver.waitForValue(),
            null to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieListObserver.waitForValue(),
            generateMockMovieList(pages = 1) to State(State.Type.READY)
        )

        movieListViewModel.onNeedLoadMore()

        Assert.assertEquals(
            movieListObserver.waitForValue(),
            generateMockMovieList(pages = 1) to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieListObserver.waitForValue(),
            generateMockMovieList(pages = 2) to State(State.Type.READY)
        )

        movieListViewModel.onNeedLoadMore()

        Assert.assertEquals(
            movieListObserver.waitForValue(),
            generateMockMovieList(pages = 2) to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieListObserver.waitForValue(),
            generateMockMovieList(pages = 3) to State(State.Type.READY)
        )
    }

    @Test
    fun `Load movie list - clear loaded data and load first page data on swipe-to-refresh`() =
        runBlockingTest {
            movieListViewModel.initViewModel()

            Assert.assertEquals(
                movieListObserver.waitForValue(), null to State(State.Type.LOADING)
            )
            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 1) to State(State.Type.READY)
            )

            movieListViewModel.onNeedLoadMore()

            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 1) to State(State.Type.LOADING)
            )
            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 2) to State(State.Type.READY)
            )

            movieListViewModel.onSwipeToRefresh()

            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 2) to State(State.Type.LOADING)
            )
            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 1) to State(State.Type.READY)
            )
        }

    @Test
    fun `Load movie list - don't clear loaded data on swipe-to-refresh action fails`() =
        runBlockingTest {
            whenever(movieInteractor.loadMovieList(any(), any()))
                .thenReturn(generateMockMovieList(pages = 1))
                .thenReturn(generateMockMovieList(pages = 1))
                .thenThrow(MockitoException("test"))

            movieListViewModel.initViewModel()

            Assert.assertEquals(
                movieListObserver.waitForValue(),
                null to State(State.Type.LOADING)
            )
            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 1) to State(State.Type.READY)
            )

            movieListViewModel.onNeedLoadMore()

            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 1) to State(State.Type.LOADING)
            )
            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 2) to State(State.Type.READY)
            )

            movieListViewModel.onSwipeToRefresh()

            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 2) to State(State.Type.LOADING)
            )
            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 2) to State(State.Type.ERROR)
            )
        }

    @Test
    fun `Load movie list - set error state in case of error`() = runBlockingTest {
        whenever(movieInteractor.loadMovieList(any(), any()))
            .thenThrow(MockitoException("test"))

        movieListViewModel.initViewModel()

        Assert.assertEquals(
            movieListObserver.waitForValue(),
            null to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieListObserver.waitForValue(),
            null to State(State.Type.ERROR)
        )
    }

    @Test
    fun `Load movie list - load new data on reload`() = runBlockingTest {
        whenever(movieInteractor.loadMovieList(any(), any()))
            .thenThrow(MockitoException("test"))
            .thenReturn(generateMockMovieList(pages = 1))

        movieListViewModel.initViewModel()

        Assert.assertEquals(
            movieListObserver.waitForValue(),
            null to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieListObserver.waitForValue(),
            null to State(State.Type.ERROR)
        )

        movieListViewModel.onReloadDataClicked()

        Assert.assertEquals(
            movieListObserver.waitForValue(),
            null to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieListObserver.waitForValue(),
            generateMockMovieList(pages = 1) to State(State.Type.READY)
        )
    }

    @Test
    fun `Load movie list - don't clear loaded data in case of error`() = runBlockingTest {
        whenever(movieInteractor.loadMovieList(any(), any()))
            .thenReturn(generateMockMovieList(pages = 1))
            .thenThrow(MockitoException("test"))

        movieListViewModel.initViewModel()

        Assert.assertEquals(
            movieListObserver.waitForValue(),
            null to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieListObserver.waitForValue(),
            generateMockMovieList(pages = 1) to State(State.Type.READY)
        )

        movieListViewModel.onNeedLoadMore()

        Assert.assertEquals(
            movieListObserver.waitForValue(),
            generateMockMovieList(pages = 1) to State(State.Type.LOADING)
        )
        Assert.assertEquals(
            movieListObserver.waitForValue(),
            generateMockMovieList(pages = 1) to State(State.Type.ERROR)
        )
    }

    @Test
    fun `Load movie list - load new data on reload in case of error after successful loading`() =
        runBlockingTest {
            whenever(movieInteractor.loadMovieList(any(), any()))
                .thenReturn(generateMockMovieList(pages = 1))
                .thenThrow(MockitoException("test"))
                .thenReturn(generateMockMovieList(pages = 1))

            movieListViewModel.initViewModel()

            Assert.assertEquals(
                movieListObserver.waitForValue(),
                null to State(State.Type.LOADING)
            )
            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 1) to State(State.Type.READY)
            )

            movieListViewModel.onNeedLoadMore()

            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 1) to State(State.Type.LOADING)
            )
            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 1) to State(State.Type.ERROR)
            )

            movieListViewModel.onNeedLoadMore()

            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 1) to State(State.Type.LOADING)
            )
            Assert.assertEquals(
                movieListObserver.waitForValue(),
                generateMockMovieList(pages = 2) to State(State.Type.READY)
            )
        }

    @Test
    fun `Open movie detail`() {
        movieListViewModel.onMovieSelected(mockMovie)
        Assert.assertEquals(
            movieDetailEventObserver.waitForValue(),
            mockMovie
        )
    }
}
