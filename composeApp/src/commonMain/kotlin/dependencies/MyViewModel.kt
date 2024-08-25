package dependencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import utils.NetworkError
import utils.Result
import utils.onError
import utils.onSuccess

class MyViewModel(
    private val repository: MyRepository
) : ViewModel() {

    private val _uiState : MutableStateFlow<Result<String,NetworkError>> = MutableStateFlow(Result.None)
    val uiState = _uiState.asStateFlow()

    init {
//        getCensoredData()
    }

    fun getHelloWorldString(): String = repository.helloWorld()

    fun getCensoredData(unCensoredText: String) {
        viewModelScope.launch {
            _uiState.value = Result.Loading
            repository.getCenceredText(unCensoredText).onSuccess { it ->
                _uiState.value = Result.Success(it)
            }.onError {
               _uiState.value = Result.Error(it)
            }
        }
    }

}