package com.soilhelp.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.soilhelp.android.data.ReadingResponse
import com.soilhelp.android.viewmodel.ReadingsUiState
import com.soilhelp.android.viewmodel.ReadingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ReadingsViewModel,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SoilHelp") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    TextButton(onClick = onLogout) {
                        Text("Logout")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is ReadingsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ReadingsUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.refresh() }) { Text("Retry") }
                    }
                }
                is ReadingsUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("Latest reading", style = MaterialTheme.typography.titleMedium)
                        }
                        item {
                            if (state.latest != null) {
                                LatestReadingCard(state.latest)
                            } else {
                                Text("No readings yet — check the sensor/gateway is uploading to ThingSpeak.")
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("History", style = MaterialTheme.typography.titleMedium)
                        }
                        items(state.history) { reading ->
                            HistoryRow(reading)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LatestReadingCard(reading: ReadingResponse) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            ReadingRow("Temperature", reading.temperature, "°C")
            ReadingRow("Humidity", reading.humidity, "%")
            ReadingRow("Soil Moisture", reading.soilMoisture, "%")
            ReadingRow("Rainfall", reading.rainfall, "mm")
            reading.recordedAt?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Recorded: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ReadingRow(label: String, value: Double?, unit: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            if (value != null) "$value $unit" else "—",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun HistoryRow(reading: ReadingResponse) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(reading.recordedAt ?: "—", style = MaterialTheme.typography.bodySmall)
            Text(
                "T:${reading.temperature ?: "-"} H:${reading.humidity ?: "-"} " +
                    "S:${reading.soilMoisture ?: "-"} R:${reading.rainfall ?: "-"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
