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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
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

/**
 * Haupt-Composable der App.
 * Verwendet [ScalingLazyColumn] fuer korrekte Darstellung auf runden Displays:
 * Inhalte werden am Rand automatisch skaliert und verblasst.
 */
@Composable
fun WearApp(
    uiState: HealthUiState,
    onRequestActivityPermission: () -> Unit
) {
    MyApplicationTheme {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Uhrzeit – groesste Darstellung als Hauptinformation
            item {
                LabelValue(
                    label = stringResource(R.string.label_time),
                    value = uiState.time,
                    valueStyle = MaterialTheme.typography.title1
                )
            }

            // Uhrzeit als Text – sekundaere Information
            item {
                LabelValue(
                    label = stringResource(R.string.label_time_text),
                    value = uiState.timeText
                )
            }

            // Datum
            item {
                LabelValue(
                    label = stringResource(R.string.label_date),
                    value = uiState.date
                )
            }

            // Schritte / Berechtigungs-UI / Sensor-Status
            when {
                !uiState.isStepSensorAvailable -> {
                    item {
                        Text(
                            text = stringResource(R.string.sensor_unavailable),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                !uiState.hasActivityPermission -> {
                    item {
                        Text(
                            text = stringResource(R.string.permission_required),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.secondary
                        )
                    }
                    item {
                        Button(
                            onClick = onRequestActivityPermission,
                            modifier = Modifier.fillMaxWidth(0.75f)
                        ) {
                            Text(
                                text = stringResource(R.string.permission_button),
                                style = MaterialTheme.typography.button
                            )
                        }
                    }
                }

                else -> {
                    item {
                        LabelValue(
                            label = stringResource(R.string.label_steps),
                            value = uiState.stepsToday.toString()
                        )
                    }
                }
            }

            // Copyright-Hinweis – caption2 (12sp) statt 10sp fuer bessere Lesbarkeit
            item {
                Text(
                    text = stringResource(R.string.copyright_notice),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption2,
                    color = MaterialTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Stellt ein Label-Value-Paar mit visueller Hierarchie dar.
 *
 * - **Label**: klein (caption1, 14sp), hellgrau (onSurfaceVariant) – beschreibend
 * - **Value**: gross (body1/title1, 16–24sp), weiss (onBackground) – Hauptinformation
 *
 * @param label Beschreibungstext (z.B. "Uhrzeit")
 * @param value Anzeigewert (z.B. "08:45")
 * @param valueStyle Textstil fuer den Wert, Standard: body1 (16sp)
 */
@Composable
fun LabelValue(
    label: String,
    value: String,
    valueStyle: TextStyle = MaterialTheme.typography.body1
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption1,
            color = MaterialTheme.colors.onSurfaceVariant
        )
        Text(
            text = value,
            textAlign = TextAlign.Center,
            style = valueStyle,
            color = MaterialTheme.colors.onBackground
        )
    }
}

@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
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