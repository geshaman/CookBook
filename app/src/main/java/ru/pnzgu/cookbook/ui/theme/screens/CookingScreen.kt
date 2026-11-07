package ru.pnzgu.cookbook.ui.theme.screens

import android.os.VibrationEffect
import android.os.Vibrator
import ru.pnzgu.cookbook.R
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

data class Step(@StringRes val textRes: Int, val timerSeconds: Int? = null)

@Composable
fun CookingScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var completedSteps by rememberSaveable { mutableIntStateOf(0) }
    val steps = listOf(
        Step(R.string.step_1),
        Step(R.string.step_2, timerSeconds = 120),
        Step(R.string.step_3)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        steps.forEachIndexed { index, step ->
            AnimatedVisibility(visible = index <= completedSteps) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(step.textRes),
                            textDecoration = if (index < completedSteps) TextDecoration.LineThrough else null
                        )
                    },
                    leadingContent = {
                        Checkbox(
                            checked = index < completedSteps,
                            enabled = index == completedSteps || index == completedSteps - 1,
                            onCheckedChange = { checked ->
                                if (checked) completedSteps++ else completedSteps--
                            }
                        )
                    },
                    supportingContent = {
                        if (step.timerSeconds != null && index == completedSteps) {
                            StepTimer(
                                totalSeconds = step.timerSeconds,
                                onFinished = {
                                    val vibrator = context.getSystemService(Vibrator::class.java)
                                    vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                                    if (index == completedSteps) completedSteps++
                                }
                            )
                        }
                    }
                )
            }
        }

    }
}

@Composable
fun StepTimer(totalSeconds: Int, onFinished: () -> Unit ) {
    var remaining by rememberSaveable { mutableIntStateOf(totalSeconds)}
    var running by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(running) {
        while (running && remaining > 0) {
            delay(1000)
            remaining = remaining - 1
        }
        if (running && remaining == 0) {
            running = false
            onFinished()
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("%02d:%02d".format(remaining / 60, remaining % 60),
            style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(12.dp))
        FilledTonalButton(onClick = {running = true }, enabled = !running && remaining > 0) {
            Text(stringResource(R.string.timer_start))
        }
        Spacer(Modifier.width(8.dp))
        OutlinedButton(onClick = {running = false; remaining = totalSeconds}) {
            Text(stringResource(R.string.timer_reset))
        }
    }
}