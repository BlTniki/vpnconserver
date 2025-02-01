package ru.bitniki.cms.domain.peers.dto;

import jakarta.validation.constraints.NotNull;
import ru.bitniki.cms.domain.hosts.dto.Host;

public record Peer(
    @NotNull Long id,
    @NotNull String name,
    @NotNull PeerStatus peerStatus,
    @NotNull Long ownerId,
    @NotNull Host host
) {}
