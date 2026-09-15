package com.berend.nonogram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berend.nonogram.ui.BoardScreen
import com.berend.nonogram.ui.PuzzleListScreen
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
    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        if (viewModel.current != null) BoardScreen(viewModel) else PuzzleListScreen(viewModel)
    }
}
