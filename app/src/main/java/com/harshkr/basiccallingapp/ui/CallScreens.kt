package com.harshkr.basiccallingapp.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshkr.basiccallingapp.CallState
import com.harshkr.basiccallingapp.CallViewModel

@Composable
fun CallScreen(viewModel: CallViewModel, modifier: Modifier = Modifier) {
    val callState by viewModel.callState.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Bonus: Smooth animations/transitions between screens
        AnimatedContent(
            targetState = callState,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "CallScreenTransition"
        ) { state ->
            when (state) {
                CallState.IDLE -> DialPadScreen(viewModel, phoneNumber)
                CallState.CALLING -> OutgoingCallScreen(viewModel, phoneNumber)
                CallState.RINGING -> IncomingCallScreen(viewModel, phoneNumber)
                CallState.ACTIVE -> ActiveCallScreen(viewModel, phoneNumber)
                CallState.ENDED -> EndedCallScreen(viewModel, phoneNumber)
            }
        }
    }
}

@Composable
fun DialPadScreen(viewModel: CallViewModel, phoneNumber: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Bonus: Show contact name if mapped
        val displayName = viewModel.getDisplayName(phoneNumber)
        if (phoneNumber.isNotEmpty() && displayName != "Unknown Number") {
            Text(text = displayName, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
        }

        Text(
            text = phoneNumber,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.height(60.dp)
        )
        
        // Simulating incoming call for demo
        TextButton(onClick = { viewModel.simulateIncomingCall() }) {
            Text("Simulate Incoming (123)")
        }

        Spacer(modifier = Modifier.weight(1f))

        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("*", "0", "#")
        )

        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    DialButton(key) { viewModel.onDigitClick(key) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(64.dp))
            
            FloatingActionButton(
                onClick = { viewModel.startOutgoingCall() },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(Icons.Filled.Call, contentDescription = "Call")
            }

            IconButton(onClick = { viewModel.onBackspaceClick() }) {
                Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Backspace")
            }
        }
    }
}

@Composable
fun OutgoingCallScreen(viewModel: CallViewModel, phoneNumber: String) {
    CallLayout(
        name = viewModel.getDisplayName(phoneNumber),
        number = phoneNumber,
        status = "Calling...",
        onEndCall = { viewModel.endCall() }
    )
}

@Composable
fun IncomingCallScreen(viewModel: CallViewModel, phoneNumber: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1A1A1A),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Incoming Call", color = Color.Gray, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(viewModel.getDisplayName(phoneNumber), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text(phoneNumber, color = Color.LightGray, fontSize = 20.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CallActionButton(Icons.Filled.CallEnd, "Decline", Color.Red) { viewModel.endCall() }
                CallActionButton(Icons.Filled.Call, "Accept", Color.Green) { viewModel.acceptCall() }
            }
        }
    }
}

@Composable
fun ActiveCallScreen(viewModel: CallViewModel, phoneNumber: String) {
    val timer by viewModel.callTimer.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val isSpeakerOn by viewModel.isSpeakerOn.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(viewModel.getDisplayName(phoneNumber), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text(phoneNumber, fontSize = 18.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Text(viewModel.formatTime(timer), fontSize = 24.sp, fontWeight = FontWeight.Medium)
            }

            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ToggleActionButton(
                        icon = if (isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                        label = "Mute",
                        isActive = isMuted,
                        onClick = { viewModel.toggleMute() }
                    )
                    ToggleActionButton(
                        icon = if (isSpeakerOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeDown,
                        label = "Speaker",
                        isActive = isSpeakerOn,
                        onClick = { viewModel.toggleSpeaker() }
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CallActionButton(Icons.Filled.CallEnd, "End", Color.Red) { viewModel.endCall() }
                }
            }
        }
    }
}

@Composable
fun EndedCallScreen(viewModel: CallViewModel, phoneNumber: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(viewModel.getDisplayName(phoneNumber), fontSize = 24.sp)
            Text("Call Ended", color = Color.Red, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DialButton(text: String, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        shape = CircleShape,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Text(text = text, fontSize = 28.sp, fontWeight = FontWeight.Normal)
    }
}

@Composable
fun CallActionButton(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = color,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(icon, contentDescription = label)
        }
        Text(label, modifier = Modifier.padding(top = 8.dp), fontSize = 12.sp)
    }
}

@Composable
fun ToggleActionButton(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconToggleButton(
            checked = isActive,
            onCheckedChange = { onClick() },
            modifier = Modifier.size(64.dp),
            colors = IconButtonDefaults.filledIconToggleButtonColors(
                checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(icon, contentDescription = label)
        }
        Text(label, modifier = Modifier.padding(top = 8.dp), fontSize = 12.sp)
    }
}

@Composable
fun CallLayout(name: String, number: String, status: String, onEndCall: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(name, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text(number, fontSize = 18.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(status, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
        }

        CallActionButton(Icons.Filled.CallEnd, "End", Color.Red, onEndCall)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DialPadPreview() {
    MaterialTheme {
        DialPadScreen(viewModel = CallViewModel(), phoneNumber = "123456")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun IncomingCallPreview() {
    MaterialTheme {
        IncomingCallScreen(viewModel = CallViewModel(), phoneNumber = "123")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ActiveCallPreview() {
    MaterialTheme {
        ActiveCallScreen(viewModel = CallViewModel(), phoneNumber = "1234567890")
    }
}
