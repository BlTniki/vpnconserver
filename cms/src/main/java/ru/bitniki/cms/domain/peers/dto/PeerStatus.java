package ru.bitniki.cms.domain.peers.dto;

public enum PeerStatus {
    PENDING,
    ACTIVE(),
    INACTIVE_MANUAL(),
    INACTIVE_BURNED()
}
