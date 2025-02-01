package ru.bitniki.cms.controller;

import ru.bitniki.cms.controller.model.HostResponse;
import ru.bitniki.cms.controller.model.PeerResponse;
import ru.bitniki.cms.domain.hosts.dto.Host;
import ru.bitniki.cms.domain.peers.dto.Peer;

public final class ResponseMapper {
    private ResponseMapper() {
    }

    public static HostResponse toHostResponse(Host host) {
        return HostResponse.builder()
                .id(host.id())
                .name(host.name())
                .build();
    }

    public static PeerResponse toPeerResponse(Peer peer) {
        return PeerResponse.builder()
                .id(peer.id())
                .name(peer.name())
                .peerStatus(PeerResponse.PeerStatusEnum.valueOf(peer.peerStatus().name()))
                .ownerId(peer.ownerId())
                .host(toHostResponse(peer.host()))
                .build();
    }
}
