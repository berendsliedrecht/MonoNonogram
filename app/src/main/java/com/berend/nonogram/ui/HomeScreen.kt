package com.berend.nonogram.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berend.nonogram.BoardSizes
import com.berend.nonogram.NonogramViewModel
import com.mudita.mmd.components.buttons.OutlinedButtonMMD
import com.mudita.mmd.components.text.TextMMD

@Composable
fun HomeScreen(viewModel: NonogramViewModel, onStats: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        TextMMD("Nonogram", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            for (size in BoardSizes) {
                OutlinedButtonMMD(
                    onClick = { viewModel.open(size) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextMMD(
                        text = "$size x $size",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButtonMMD(onClick = onStats, modifier = Modifier.fillMaxWidth()) {
            TextMMD("Stats", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
