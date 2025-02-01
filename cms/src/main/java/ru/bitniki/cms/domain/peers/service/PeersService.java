package ru.bitniki.cms.domain.peers.service;

import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.bitniki.cms.domain.peers.dto.Peer;

public interface PeersService {
    /**
     * Return list of {@link Peer} with given owner id.
     * @param ownerId owner id
     * @return list of {@link Peer}
     */
    Flux<Peer> getAllByOwnerId(long ownerId);

    /**
     * Return Peer by given id.
     * @param id Peer id
     * @return {@link Peer} or
     * {@link ru.bitniki.cms.domain.exception.EntityNotFoundException} if a Peer with this id not exists
     */
    Mono<Peer> getById(long id);

    /**
     * Creates new peer in system and creates connection on host
     * Note: after successful method return the connection on host may be not created yet.
     * You can check its creation by checking peer status.
     * @param name peer name
     * @param ownerId user id
     * @param hostId host id
     * @return Created peer
     */
    Mono<Peer> createPeer(@NotNull String name, long ownerId, long hostId);

    /**
     * Updates peer
     * @param id existing peer id
     * @param newPeer new peer fields
     * @return updated peer
     */
    Mono<Peer> updatePeer(long id, Peer newPeer);

    /**
     * Deletes peer
     * @param id existing peer id
     * @return deleted peer
     */
    Mono<Peer> deletePeer(long id);
}
