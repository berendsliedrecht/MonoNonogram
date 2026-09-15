package com.berend.nonogram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berend.nonogram.ui.BoardScreen
import com.berend.nonogram.ui.HomeScreen
import com.berend.nonogram.ui.StatsScreen
import com.mudita.mmd.ThemeMMD

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemeMMD {
                NonogramApp()
            }
        }
    }
}

@Composable
fun NonogramApp(viewModel: NonogramViewModel = viewModel()) {
    var showStats by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        when {
            viewModel.current != null -> BoardScreen(viewModel)
            showStats -> StatsScreen(viewModel, onBack = { showStats = false })
            else -> HomeScreen(viewModel, onStats = { showStats = true })
        }
    }
}
