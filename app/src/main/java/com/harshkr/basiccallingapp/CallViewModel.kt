package com.harshkr.basiccallingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class CallState {
    IDLE, CALLING, RINGING, ACTIVE, ENDED
}

class CallViewModel : ViewModel() {

    // Bonus: Contact Name Mapping
    private val contactMap = mapOf(
        "123" to "Mom",
        "456" to "Dad",
        "108" to "Emergency",
        "100" to "Police",
        "1234567890" to "Harsh"
    )

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _callState = MutableStateFlow(CallState.IDLE)
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    private val _callTimer = MutableStateFlow(0L)
    val callTimer: StateFlow<Long> = _callTimer.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _isSpeakerOn = MutableStateFlow(false)
    val isSpeakerOn: StateFlow<Boolean> = _isSpeakerOn.asStateFlow()

    private var timerJob: Job? = null

    fun getDisplayName(number: String): String {
        return contactMap[number] ?: "Unknown Number"
    }

    fun onDigitClick(digit: String) {
        if (_phoneNumber.value.length < 15) {
            _phoneNumber.value += digit
        }
    }

    fun onBackspaceClick() {
        if (_phoneNumber.value.isNotEmpty()) {
            _phoneNumber.value = _phoneNumber.value.dropLast(1)
        }
    }

    fun startOutgoingCall() {
        if (_phoneNumber.value.isNotEmpty()) {
            _callState.value = CallState.CALLING
            viewModelScope.launch {
                delay(2000) // Simulate calling delay
                _callState.value = CallState.ACTIVE
                startTimer()
            }
        }
    }

    fun simulateIncomingCall(number: String = "123") {
        _phoneNumber.value = number
        _callState.value = CallState.RINGING
    }

    fun acceptCall() {
        _callState.value = CallState.ACTIVE
        startTimer()
    }

    fun endCall() {
        _callState.value = CallState.ENDED
        stopTimer()
        viewModelScope.launch {
            delay(1500) // Show ended state briefly
            _callState.value = CallState.IDLE
            _phoneNumber.value = ""
            _callTimer.value = 0L
            _isMuted.value = false
            _isSpeakerOn.value = false
        }
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun toggleSpeaker() {
        _isSpeakerOn.value = !_isSpeakerOn.value
    }

    private fun startTimer() {
        timerJob?.cancel()
        _callTimer.value = 0L
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _callTimer.value += 1
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    fun formatTime(seconds: Long): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(mins, secs)
    }
}
