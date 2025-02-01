package ru.bitniki.cms.domain.peers.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.bitniki.cms.domain.exception.EntityAlreadyExistException;
import ru.bitniki.cms.domain.exception.EntityNotFoundException;
import ru.bitniki.cms.domain.exception.RequestValidationFailedException;
import ru.bitniki.cms.domain.hosts.service.HostsService;
import ru.bitniki.cms.domain.peers.dao.R2dbcPeersDao;
import ru.bitniki.cms.domain.peers.dto.Peer;
import ru.bitniki.cms.domain.peers.dto.PeerStatus;
import ru.bitniki.cms.domain.peers.dto.R2dbcPeerEntity;

import java.util.regex.Pattern;

@Service
@Transactional
public class R2dbcPeersService implements PeersService {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Pattern peerNamePattern = Pattern.compile("^[A-Za-z0-9]+$");

    private final R2dbcPeersDao peersDao;
    private final HostsService hostsService;

    @Autowired
    public R2dbcPeersService(R2dbcPeersDao peersDao, HostsService hostsService) {
        this.peersDao = peersDao;
        this.hostsService = hostsService;
    }

    private Mono<Peer> toPeer(R2dbcPeerEntity entity) {
        return hostsService.getById(entity.getHostId())
            .map(host -> new Peer(entity.getId(), entity.getName(), PeerStatus.valueOf(entity.getPeerStatus()), entity.getOwnerId(), host));
    }

    private static void logEntityRetrieving(Peer dto) {
        LOGGER.debug("Found peer `{}`", dto);
    }

    @Override
    public Flux<Peer> getAllByOwnerId(long ownerId) {
        LOGGER.debug("Getting peers with ownerId `{}`", ownerId);
        return peersDao.findByOwnerId(ownerId)
                .flatMap(this::toPeer)
                .doOnNext(R2dbcPeersService::logEntityRetrieving);
    }

    @Override
    public Mono<Peer> getById(long id) {
        LOGGER.debug("Getting peer with id `{}`", id);
        return peersDao.findById(id)
                .flatMap(this::toPeer)
                .switchIfEmpty(produceEntityNotExistError(id))
                .doOnNext(R2dbcPeersService::logEntityRetrieving);
    }

    private static @NotNull Mono<Peer> produceEntityNotExistError(long id) {
        return Mono.error(new EntityNotFoundException("Peer with id `%d` not found".formatted(id)));
    }

    @Override
    public Mono<Peer> createPeer(String name, long ownerId, long hostId) {
        LOGGER.debug("Creating peer with name `{}`, ownerId `{}`, hostId `{}`", name, ownerId, hostId);

        return peersDao.findByOwnerIdAndName(ownerId, name)
                .flatMap(entity -> Mono.error(
                        new EntityAlreadyExistException(
                                "Owner with id `%d` already have peer with name `%s`".formatted(ownerId, name)
                        )
                ))
                .switchIfEmpty(
                        peerNamePattern.matcher(name).matches() ?
                                Mono.empty() :
                                Mono.error(new RequestValidationFailedException(
                                        "Bad name for peer (name: `%S`)".formatted(name)
                                ))
                )
                .switchIfEmpty(peersDao.save(new R2dbcPeerEntity(
                        null, name, PeerStatus.PENDING.name(), ownerId, hostId
                )))
                .map(entity -> toPeer(entity))
    }

    @Override
    public Mono<Peer> updatePeer(long id, Peer newPeer) {
        return null;
    }

    @Override
    public Mono<Peer> deletePeer(long id) {
        LOGGER.debug("Removing peer with id `{}`", id);
        return peersDao.findById(id)
                .flatMap(entity -> peersDao.delete(entity).thenReturn(entity))
                .flatMap(this::toPeer)
                .switchIfEmpty(produceEntityNotExistError(id))
                .doOnNext(dto ->  LOGGER.debug("Removed peer `{}`", dto));
    }
}
