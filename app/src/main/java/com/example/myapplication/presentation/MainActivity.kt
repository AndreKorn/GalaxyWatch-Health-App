/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.myapplication.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.myapplication.R
import com.example.myapplication.presentation.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val healthViewModel: HealthViewModel by viewModels()

    private val activityPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            healthViewModel.onActivityPermissionChanged(granted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        healthViewModel.onActivityPermissionChanged(hasActivityRecognitionPermission())

        setContent {
            val uiState by healthViewModel.uiState.collectAsState()
            WearApp(
                uiState = uiState,
                onRequestActivityPermission = {
                    activityPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        healthViewModel.onHostResumed()
    }

    override fun onPause() {
        healthViewModel.onHostPaused()
        super.onPause()
    }

    private fun hasActivityRecognitionPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun WearApp(
    uiState: HealthUiState,
    onRequestActivityPermission: () -> Unit
) {
    MyApplicationTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            HealthDashboard(
                uiState = uiState,
                onRequestActivityPermission = onRequestActivityPermission
            )
        }
    }
}

@Composable
fun HealthDashboard(
    uiState: HealthUiState,
    onRequestActivityPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LabelValue(label = stringResource(R.string.label_time), value = uiState.time)
        LabelValue(label = stringResource(R.string.label_time_text), value = uiState.timeText)
        LabelValue(label = stringResource(R.string.label_date), value = uiState.date)

        when {
            !uiState.isStepSensorAvailable -> {
                Text(
                    text = stringResource(R.string.sensor_unavailable),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary
                )
            }

            !uiState.hasActivityPermission -> {
                Text(
                    text = stringResource(R.string.permission_required),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary
                )
                Button(onClick = onRequestActivityPermission) {
                    Text(text = stringResource(R.string.permission_button))
                }
            }

            else -> {
                LabelValue(
                    label = stringResource(R.string.label_steps),
                    value = uiState.stepsToday.toString()
                )
            }
        }
    }
}

@Composable
fun LabelValue(label: String, value: String) {
    Text(
        text = "$label: $value",
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(
        uiState = HealthUiState(
            time = "08:45",
            timeText = "viertel vor neun",
            date = "12.04.2026",
            stepsToday = 3124,
            hasActivityPermission = true,
            isStepSensorAvailable = true
        ),
        onRequestActivityPermission = {}
    )
}