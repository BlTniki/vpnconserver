package ru.bitniki.cms.controller;

import org.junit.jupiter.api.Test;
import ru.bitniki.cms.controller.model.HostResponse;
import ru.bitniki.cms.domain.hosts.dto.Host;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseMapperTest {

    @Test
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
}