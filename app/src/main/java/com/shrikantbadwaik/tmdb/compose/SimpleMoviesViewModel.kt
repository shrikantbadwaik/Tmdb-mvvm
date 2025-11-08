package com.shrikantbadwaik.tmdb.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shrikantbadwaik.tmdb.core.Result
import com.shrikantbadwaik.tmdb.data.remote.MovieDto
import com.shrikantbadwaik.tmdb.data.remote.TMDbService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MoviesUiState(
    val isLoading: Boolean = false,
    val movies: List<MovieDto> = emptyList(),
    val error: Throwable? = null
)

class SimpleMoviesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState(isLoading = true))
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    init {
        fetchPopularMovies()
    }

    fun fetchPopularMovies() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val result = loadPopular()) {
                is Result.Success -> {
                    _uiState.value = MoviesUiState(isLoading = false, movies = result.data, error = null)
                }
                is Result.Error -> {
                    _uiState.value = MoviesUiState(isLoading = false, movies = emptyList(), error = result.throwable)
                }
            }
        }
    }

    private suspend fun loadPopular(): Result<List<MovieDto>> {
        return try {
            val response = TMDbService.api.getPopularMovies()
            Result.Success(response.results)
        } catch (ce: CancellationException) {
            throw ce
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }
}


