import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cmmcomposelearning.composeapp.generated.resources.Res
import cmmcomposelearning.composeapp.generated.resources.ic_android
import dependencies.MyViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import utils.NetworkError
import utils.Result

@Composable
@Preview
fun App(batteryManager: BatteryManager) {
    MaterialTheme {
        KoinContext {
            NavHost(
                rememberNavController(),
                startDestination = "home"
            ) {
                composable(route = "home") {
//                    BatteryUI(batteryManager)
//                    CensoredToUnCensorTextComposableUI()
                    DataStoreCounterSample()
                }
            }


        }
    }
}

@Composable
fun DataStoreCounterSample(){
    val dataStorePref = koinInject<DataStore<Preferences>>()

    val counter by dataStorePref
        .data
        .map {
            val counterKey = intPreferencesKey("counter")
            it[counterKey] ?: 0
        }
        .collectAsStateWithLifecycle(0)

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = counter.toString(),
            textAlign = TextAlign.Center,
            fontSize = 50.sp
        )
        Button(onClick = {
            scope.launch {
                dataStorePref.edit { dataStore ->
                    val counterKey = intPreferencesKey("counter")
                    dataStore[counterKey] = counter + 1
                }
            }
        }) {
            Text("Increment!")
        }
    }
}

@Composable
fun CensoredToUnCensorTextComposableUI(){
    val viewModel = koinViewModel<MyViewModel>()

    // Collecting the uiState as a State
    val uiState by viewModel.uiState.collectAsStateWithLifecycle("")

    val unCensoredText = remember {
        mutableStateOf("")
    }
    val censoredText = remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()


    LaunchedEffect(uiState) {
        // Reacting to the state changes
        when (uiState) {
            is utils.Result.Success<*> -> {
                val data = (uiState as utils.Result.Success<String>).data
                censoredText.value = data
            }

            is utils.Result.Error<*> -> {
                val error = (uiState as utils.Result.Error<NetworkError>).error
                println(error)
            }

            else -> {}
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(value = unCensoredText.value, onValueChange = { it ->
            unCensoredText.value = it
        }, placeholder = {
            Text("Enter censored text")
        })
        Button(onClick = {
            scope.launch {
                viewModel.getCensoredData(unCensoredText.value)
            }
        }) {
            Text("Click")
        }
        if (uiState is Result.Loading) {
            CircularProgressIndicator()
        }
        if (censoredText.value.isNotEmpty()) {
            Text(censoredText.value)
        }

    }
}

@Composable
fun BatteryUI(batteryManager: BatteryManager) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        var visible by remember {
            mutableStateOf(true)
        }

        val animatedAlpha by animateFloatAsState(
            targetValue = if (visible) 1.0f else 0f,
            label = "alpha"
        )

        val colorGreen = Color(0xFF53D9A1)
        val colorBlue = Color(0xFF4FC3F7)

        val animatedColor by animateColorAsState(
            if (visible) colorGreen else colorBlue,
            label = "color"
        )

        val animateScale by animateFloatAsState(
            targetValue = if (visible) 1f else 0f
        )

        val viewModel = koinViewModel<MyViewModel>()

        Image(
            painterResource(Res.drawable.ic_android),
            null,
            modifier = Modifier.clickable {
                visible = !visible
            })
        AnimatedVisibility(visible) {
            Column(verticalArrangement = Arrangement.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = animatedAlpha
                }.drawBehind {
                    drawRect(animatedColor)
                }
                    .animateContentSize()
                    .height(if (visible) 400.dp else 200.dp)
            ) {
                // your composable here
                Text(
                    "Current battery level is ${batteryManager.getBatteryLevel()} %",
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = animateScale
                            scaleY = animateScale
                            transformOrigin = TransformOrigin.Center
                        }
                )
                Text(text = viewModel.getHelloWorldString())
            }
        }
    }
}