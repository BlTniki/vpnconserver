package ru.bitniki.cms.controller;

import ru.bitniki.cms.controller.model.HostResponse;
import ru.bitniki.cms.domain.hosts.dto.Host;

public final class ResponseMapper {
    private ResponseMapper() {
    }

    public static HostResponse toHostResponse(Host host) {
        return HostResponse.builder()
                .id(host.id())
                .name(host.name())
                .build();
    }
}
