package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarthome.DeviceType
import com.example.smarthome.SmartDevice
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.viewmodel.VoiceLockViewModel

@Composable
fun SmartHomeScreen(viewModel: VoiceLockViewModel) {
    val devices by viewModel.smartDevices.collectAsState()
    var selectedRoom by remember { mutableStateOf("सभी") }

    val rooms = listOf("सभी", "लिविंग रूम", "बेडरूम", "मुख्य द्वार", "किचन")
    val filteredDevices = if (selectedRoom == "सभी") devices else devices.filter { it.roomHindi == selectedRoom }

    val activeCount = devices.count { it.isPoweredOn }
    val totalWatts = devices.filter { it.isPoweredOn }.sumOf { it.powerWatts }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("smart_home_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Smart Home",
                            tint = CyberCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "स्मार्ट होम कंट्रोल हब",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Arya AI होम ऑटोमेशन व वॉयस कंट्रोल",
                            fontSize = 11.sp,
                            color = CyberCyan
                        )
                    }
                }

                // Voice Command Hint Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "वॉयस रेडी 🎙️",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }
        }

        // 2. Overview Energy & Master Control Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().testTag("smart_home_energy_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "सक्रिय उपकरण: $activeCount / ${devices.size}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "अनुमानित खपत: $totalWatts W",
                                    fontSize = 12.sp,
                                    color = Color(0xFFFFB74D),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Quick All ON / All OFF buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { viewModel.setAllDevicesPower(if (selectedRoom == "सभी") null else selectedRoom, true) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = DeepNavy),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("सभी ON", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.setAllDevicesPower(if (selectedRoom == "सभी") null else selectedRoom, false) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155), contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("सभी OFF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2.5 Smart Automation Routines (Good Morning, Night, Cinema, Away)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().testTag("smart_routines_section_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFB74D).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB74D),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "स्मार्ट रूटीन्स (Smart Automations)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "एक टैप या आवाज़ से पूरा घर नियंत्रित करें",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SuccessGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ऑटो-सिंक",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        viewModel.smartRoutines.forEach { routine ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.executeSmartRoutine(routine.id) }
                                    .testTag("routine_card_${routine.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = routine.iconEmoji,
                                            fontSize = 24.sp,
                                            modifier = Modifier.padding(end = 10.dp)
                                        )
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = routine.titleHindi,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(CyberCyan.copy(alpha = 0.15f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = routine.badge,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = CyberCyan
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = routine.descriptionHindi,
                                                fontSize = 11.sp,
                                                color = Color.LightGray,
                                                lineHeight = 15.sp
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "समय: ${routine.timeHintHindi}",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = { viewModel.executeSmartRoutine(routine.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = CyberCyan,
                                            contentColor = DeepNavy
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp).testTag("trigger_routine_${routine.id}")
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("चलाएँ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Room Selection Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(rooms) { room ->
                    val isSelected = selectedRoom == room
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CyberCyan else SurfaceElevated)
                            .clickable { selectedRoom = room }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = room,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) DeepNavy else Color.White
                        )
                    }
                }
            }
        }

        // 4. Device Items
        items(filteredDevices) { device ->
            SmartDeviceCard(
                device = device,
                onToggle = { viewModel.toggleDevicePower(device.id) },
                onValueChange = { newVal -> viewModel.updateDeviceValue(device.id, newVal) }
            )
        }

        // 5. Voice Commands Quick Guide Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1F24)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "आर्या से बोलकर ऐसे कंट्रोल करें (Voice Commands):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• \"लाइट चालू करो\" / \"लाइट बंद करो\"\n• \"AC चालू करो\" / \"AC बंद करो\"\n• \"पंखा चलाओ\" / \"दरवाजा लॉक करो\"",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = Color.LightGray
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SmartDeviceCard(
    device: SmartDevice,
    onToggle: () -> Unit,
    onValueChange: (Int) -> Unit
) {
    val icon: ImageVector = when (device.type) {
        DeviceType.LIGHT -> Icons.Default.Lightbulb
        DeviceType.AC_THERMOSTAT -> Icons.Default.AcUnit
        DeviceType.FAN -> Icons.Default.WindPower
        DeviceType.SMART_LOCK -> if (device.isPoweredOn) Icons.Default.Lock else Icons.Default.LockOpen
        DeviceType.SMART_TV -> Icons.Default.Tv
        DeviceType.SMART_PLUG -> Icons.Default.ElectricalServices
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (device.isPoweredOn) SurfaceCard else Color(0xFF16181D)
        ),
        shape = RoundedCornerShape(18.dp),
        border = if (device.isPoweredOn) androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)) else null,
        modifier = Modifier.fillMaxWidth().testTag("device_card_${device.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (device.isPoweredOn) CyberCyan.copy(alpha = 0.2f) else Color(0xFF282A30)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (device.isPoweredOn) CyberCyan else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = device.nameHindi,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${device.roomHindi} • ",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = if (device.isPoweredOn) device.stateLabel else "बंद (OFF)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (device.isPoweredOn) CyberCyan else Color.Gray
                            )
                        }
                    }
                }

                // Power Toggle Switch
                Switch(
                    checked = device.isPoweredOn,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DeepNavy,
                        checkedTrackColor = CyberCyan,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFF282A30)
                    )
                )
            }

            // Interactive Sliders for Light, AC, and Fan
            if (device.isPoweredOn) {
                when (device.type) {
                    DeviceType.AC_THERMOSTAT -> {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("तापमान नियंत्रण", fontSize = 12.sp, color = Color.LightGray)
                            Text("${device.value}°C", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        }
                        Slider(
                            value = device.value.toFloat(),
                            onValueChange = { onValueChange(it.toInt()) },
                            valueRange = 16f..30f,
                            steps = 13,
                            colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                        )
                    }
                    DeviceType.LIGHT -> {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("चमक (Brightness)", fontSize = 12.sp, color = Color.LightGray)
                            Text("${device.value}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        }
                        Slider(
                            value = device.value.toFloat(),
                            onValueChange = { onValueChange(it.toInt()) },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                        )
                    }
                    DeviceType.FAN -> {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("स्पीड (Speed 1-5)", fontSize = 12.sp, color = Color.LightGray)
                            Text("स्पीड ${device.value}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        }
                        Slider(
                            value = device.value.toFloat(),
                            onValueChange = { onValueChange(it.toInt()) },
                            valueRange = 1f..5f,
                            steps = 3,
                            colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}
