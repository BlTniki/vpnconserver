package ru.bitniki.cms.domain.peers.dao;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.bitniki.cms.domain.peers.dto.R2dbcPeerEntity;

public interface R2dbcPeersDao extends ReactiveCrudRepository<R2dbcPeerEntity, Long> {
    Flux<R2dbcPeerEntity> findByOwnerId(long ownerId);

    Mono<R2dbcPeerEntity> findByOwnerIdAndName(long ownerId, String name);
}
