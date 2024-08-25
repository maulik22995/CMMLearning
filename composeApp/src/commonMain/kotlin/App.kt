import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dependencies.MyViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import utils.NetworkError
import utils.Result

@Composable
@Preview
fun App(batteryManager: BatteryManager) {
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

    val scope = rememberCoroutineScope()

    MaterialTheme {
        KoinContext {
            NavHost(
                rememberNavController(),
                startDestination = "home"
            ) {
                composable(route = "home") {
                    val viewModel = koinViewModel<MyViewModel>()

                    // Collecting the uiState as a State
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle("")

                    var unCensoredText = remember {
                        mutableStateOf("")
                    }
                    var censoredText = remember {
                        mutableStateOf("")
                    }

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


//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.fillMaxSize()
//                    ) {
//                        Image(
//                            painterResource(Res.drawable.ic_android),
//                            null,
//                            modifier = Modifier.clickable {
//                                visible = !visible
//                            })
////            AnimatedVisibility(visible){
//                        Column(verticalArrangement = Arrangement.Center,
//                            modifier = Modifier.graphicsLayer {
//                                alpha = animatedAlpha
//                            }.drawBehind {
//                                drawRect(animatedColor)
//                            }
//                                .animateContentSize()
//                                .height(if (visible) 400.dp else 200.dp)
//                        ) {
//                            // your composable here
//                            Text(
//                                "Current battery level is ${batteryManager.getBatteryLevel()} %",
//                                modifier = Modifier
//                                    .graphicsLayer {
//                                        scaleX = animateScale
//                                        scaleY = animateScale
//                                        transformOrigin = TransformOrigin.Center
//                                    }
//                            )
//                            Text(text = viewmodel.getHelloWorldString())
//                        }
////            }
//                    }
                }
            }


        }
    }
}