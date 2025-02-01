package ru.bitniki.cms.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.bitniki.cms.controller.model.HostResponse;
import ru.bitniki.cms.controller.model.PeerResponse;
import ru.bitniki.cms.domain.hosts.dto.Host;
import ru.bitniki.cms.domain.peers.dto.Peer;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseMapperTest {

    @Test
    @DisplayName("Host: check dto to response mapping")
    void toHostResponse() {
        var expectResponse = HostResponse.builder()
                .id(123L)
                .name("lolkek")
                .build();

        var actualResponse = ResponseMapper.toHostResponse(new Host(
                123L, "lolkek", "I pee", 123, "pass"
        ));

        assertThat(actualResponse).isEqualTo(expectResponse);
    }

    @Test
    @DisplayName("Peer: check dto to response mapping")
    void toPeerResponse() {
        var expectResponse = PeerResponse.builder()
                .id(123L)
                .name("lel")
                .peerStatus(PeerResponse.PeerStatusEnum.PENDING)
                .ownerId(321L)
                .host(
                        HostResponse.builder()
                        .id(123L)
                        .name("lolkek")
                        .build()
                )
                .build();

        var actualResponse = ResponseMapper.toPeerResponse(new Peer(
                123L,
                "lel",
                "PENDING",
                321L,
                new Host(123L, "lolkek", "I pee", 123, "pass")
        ));

        assertThat(actualResponse).isEqualTo(expectResponse);
    }
}