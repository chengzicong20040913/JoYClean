package com.android.skip.vpnservice.protocol

enum class TCBStatus {
    SYN_SENT,
    SYN_RECEIVED,
    ESTABLISHED,
    CLOSE_WAIT,
    LAST_ACK,
    CLOSED
}