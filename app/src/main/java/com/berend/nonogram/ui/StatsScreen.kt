package com.berend.nonogram.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berend.nonogram.BoardSizes
import com.berend.nonogram.NonogramViewModel
import com.berend.nonogram.formatDuration
import com.mudita.mmd.components.divider.HorizontalDividerMMD
import com.mudita.mmd.components.text.TextMMD
import com.mudita.mmd.components.top_app_bar.TopAppBarMMD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: NonogramViewModel, onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBarMMD(
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            },
            title = { TextMMD("Stats", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            BoardSizes.forEachIndexed { index, size ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                ) {
                    TextMMD(
                        text = "$size x $size",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        TextMMD("Solved ${viewModel.gamesPlayed(size)}", fontSize = 16.sp)
                        viewModel.bestMillis(size)?.let {
                            TextMMD("Best ${formatDuration(it)}", fontSize = 16.sp)
                        }
                    }
                }
                if (index < BoardSizes.lastIndex) HorizontalDividerMMD()
            }
        }
    }
}
