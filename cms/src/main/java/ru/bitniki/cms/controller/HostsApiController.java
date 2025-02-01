package ru.bitniki.cms.controller;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.bitniki.cms.controller.model.HostResponse;
import ru.bitniki.cms.domain.hosts.service.HostsService;

@RestController
public class HostsApiController implements HostsApi {
    private static final Logger LOGGER = LogManager.getLogger();

    private final HostsService hostsService;

    public HostsApiController(HostsService hostsService) {
        this.hostsService = hostsService;
    }

    public Mono<ResponseEntity<List<HostResponse>>> hostsGet() {
        return hostsService.getAll()
                .map(ResponseMapper::toHostResponse)
                .collectList()
                .doOnNext(list -> LOGGER.info("Response successfully with hosts `{}`  ", list))
                .map(ResponseEntity::ok);
    }

    public Mono<ResponseEntity<HostResponse>> hostsIdGet(Long id) {
        return hostsService.getById(id)
                .map(ResponseMapper::toHostResponse)
                .doOnNext(
                    response -> LOGGER.info("Response successfully to GET request at /hosts/{}", id)
                )
                .map(ResponseEntity::ok);
    }

}
