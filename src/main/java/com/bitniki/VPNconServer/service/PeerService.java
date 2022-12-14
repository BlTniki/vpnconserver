package com.bitniki.VPNconServer.service;

import com.bitniki.VPNconServer.connectHandler.PeerConnectHandler;
import com.bitniki.VPNconServer.entity.HostEntity;
import com.bitniki.VPNconServer.entity.PeerEntity;
import com.bitniki.VPNconServer.entity.UserEntity;
import com.bitniki.VPNconServer.exception.notFoundException.EntityNotFoundException;
import com.bitniki.VPNconServer.exception.notFoundException.HostNotFoundException;
import com.bitniki.VPNconServer.exception.alreadyExistException.PeerAlreadyExistException;
import com.bitniki.VPNconServer.exception.notFoundException.PeerNotFoundException;
import com.bitniki.VPNconServer.exception.notFoundException.UserNotFoundException;
import com.bitniki.VPNconServer.exception.validationFailedException.PeerValidationFailedException;
import com.bitniki.VPNconServer.model.PeerWithAllRelations;
import com.bitniki.VPNconServer.repository.HostRepo;
import com.bitniki.VPNconServer.repository.PeerRepo;
import com.bitniki.VPNconServer.repository.UserRepo;
import com.bitniki.VPNconServer.validator.PeerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PeerService {
    @Autowired
    private PeerRepo peerRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private HostRepo hostRepo;

    public List<PeerWithAllRelations> getAll() {
        List<PeerEntity> peerEntities = new ArrayList<>();
        peerRepo.findAll().forEach(peerEntities::add);
        return peerEntities.stream().map(PeerWithAllRelations::toModel).collect(Collectors.toList());
    }

    public List<PeerWithAllRelations> getAll(Principal principal) throws UserNotFoundException {
        // load user
        UserEntity user = userRepo.findByLogin(principal.getName());
        if(user == null) throw new UserNotFoundException("User not found");

        return user.getPeers().stream().map(PeerWithAllRelations::toModel).collect(Collectors.toList());
    }

    public PeerWithAllRelations getOne(Long id) throws PeerNotFoundException {
        Optional<PeerEntity> peerEntityOptional;
        peerEntityOptional = peerRepo.findById(id);
        if(peerEntityOptional.isPresent()) return PeerWithAllRelations.toModel(peerEntityOptional.get());
        else throw new PeerNotFoundException("Peer not found");
    }

    public PeerWithAllRelations getOne(Long id, Principal principal) throws EntityNotFoundException {
        // load user
        UserEntity user = userRepo.findByLogin(principal.getName());
        if(user == null) throw new UserNotFoundException("User not found");

        return PeerWithAllRelations.toModel(user.getPeers().stream()
                .filter(peerEntity -> peerEntity.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new PeerNotFoundException("Peer does not exist or you have no permission for this peer")));
    }

    private PeerEntity createPeer(UserEntity user, HostEntity host, PeerEntity peer) throws EntityNotFoundException, PeerAlreadyExistException, PeerValidationFailedException {
        // validate peer
        PeerValidator peerValidator = PeerValidator.validateAllFields(peer);
        if(peerValidator.hasFails()) {
            throw new PeerValidationFailedException(peerValidator.toString());
        }

        //check the uniqueness of the confName for a specific host and user
        if(peerRepo.findByUserAndHostAndPeerConfName(user, host, peer.getPeerConfName()) != null) {
            throw new PeerAlreadyExistException("Peer already exist");
        }

        //check the uniqueness of the peerIp
        if(peerRepo.findByPeerIp(peer.getPeerIp()) != null) {
            throw new PeerAlreadyExistException("This peer ip already exist");
        }

        peer.setUser(user);
        peer.setHost(host);
        //create peer on host and complete peer entity
        PeerConnectHandler peerConnectHandler = new PeerConnectHandler(peer);
        peerConnectHandler.createPeerOnHostAndFillEntity();

        return peerRepo.save(peer);
    }

    public PeerWithAllRelations create(Long user_id, Long host_id, PeerEntity peerEntity) throws EntityNotFoundException, PeerAlreadyExistException, PeerValidationFailedException {
        //find user entity
        UserEntity user = userRepo.findById(user_id).orElseThrow(() -> new UserNotFoundException("User not found"));

        //find host entity
        HostEntity host = hostRepo.findById(host_id).orElseThrow(() -> new HostNotFoundException("User not found"));

        return PeerWithAllRelations.toModel(createPeer(user, host, peerEntity));
    }

    public PeerWithAllRelations create(Principal principal, Long host_id, PeerEntity peerEntity) throws EntityNotFoundException, PeerAlreadyExistException, PeerValidationFailedException {
        // load user
        UserEntity user = userRepo.findByLogin(principal.getName());
        if(user == null) throw new UserNotFoundException("User not found");

        //find host entity
        HostEntity host = hostRepo.findById(host_id).orElseThrow(() -> new HostNotFoundException("User not found"));

        return PeerWithAllRelations.toModel(createPeer(user, host, peerEntity));
    }

    private PeerEntity updatePeer(PeerEntity oldPeer, PeerEntity newPeer)
            throws PeerNotFoundException, PeerAlreadyExistException, PeerValidationFailedException {
        // validate new peer
        PeerValidator peerValidator = PeerValidator.validateNonNullFields(newPeer);
        if(peerValidator.hasFails()) {
            throw new PeerValidationFailedException(peerValidator.toString());
        }

        //check the uniqueness of the confName for a specific host and user
        //this is placeholder because we cant properly update confName on host
        if(newPeer.getPeerConfName() != null && peerRepo.findByUserAndHostAndPeerConfName(
                oldPeer.getUser(), oldPeer.getHost(), newPeer.getPeerConfName()) != null) {
            throw new PeerAlreadyExistException("Peer already exist");
        }

        //if we have new peer ip ??? update
        if(newPeer.getPeerIp() != null) {
            oldPeer.setPeerIp(newPeer.getPeerIp());
        }

        //update peer on host
        PeerConnectHandler peerConnectHandler = new PeerConnectHandler(oldPeer);
        peerConnectHandler.updatePeerOnHost();

        return peerRepo.save(oldPeer);
    }


    public PeerWithAllRelations update(Long id, PeerEntity newPeer)
            throws PeerNotFoundException, PeerAlreadyExistException, PeerValidationFailedException {
        //find old peer
        Optional<PeerEntity> peerEntityOptional;
        peerEntityOptional = peerRepo.findById(id);
        PeerEntity oldPeer;
        if(peerEntityOptional.isPresent()) oldPeer = peerEntityOptional.get();
        else throw new PeerNotFoundException("Peer not found");

        return PeerWithAllRelations.toModel(updatePeer(oldPeer, newPeer));
    }

    public PeerWithAllRelations update(Principal principal, Long id, PeerEntity newPeer)
            throws EntityNotFoundException, PeerAlreadyExistException, PeerValidationFailedException {
        // load user
        UserEntity user = userRepo.findByLogin(principal.getName());
        if(user == null) throw new UserNotFoundException("User not found");

        //find old peer
        PeerEntity oldPeer = user.getPeers().stream()
                .filter(peerEntity -> peerEntity.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new PeerNotFoundException("Peer does not exist or you have no permission for this peer"));

        return PeerWithAllRelations.toModel(updatePeer(oldPeer, newPeer));
    }

    private PeerEntity deletePeer(PeerEntity peer) {
        //deleting peer on host
        PeerConnectHandler peerConnectHandler = new PeerConnectHandler(peer);
        peerConnectHandler.deletePeerOnHost();
        //deleting in db
        peerRepo.delete(peer);
        return peer;
    }

    public PeerWithAllRelations delete(Long id) throws PeerNotFoundException {
        // find peer
        Optional<PeerEntity> peerEntityOptional;
        peerEntityOptional = peerRepo.findById(id);
        PeerEntity peer;
        if(peerEntityOptional.isPresent()) peer = peerEntityOptional.get();
        else throw new PeerNotFoundException("Peer not found");

        return PeerWithAllRelations.toModel(deletePeer(peer));
    }

    public PeerWithAllRelations delete(Principal principal, Long id) throws EntityNotFoundException {
        // load user
        UserEntity user = userRepo.findByLogin(principal.getName());
        if(user == null) throw new UserNotFoundException("User not found");

        //find peer
        PeerEntity peer = user.getPeers().stream()
                .filter(peerEntity -> peerEntity.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new PeerNotFoundException("Peer does not exist or you have no permission for this peer"));

        return PeerWithAllRelations.toModel(deletePeer(peer));
    }

    private String makeRequestToHostForDownloadToken(PeerEntity peer) {
        //make request to host
        PeerConnectHandler peerConnectHandler = new PeerConnectHandler(peer);
        return peerConnectHandler.getDownloadConfToken();
    }

    public String getDownloadTokenForPeer(Long id) throws PeerNotFoundException {
        PeerEntity peer = peerRepo.findById(id).orElseThrow(
                () -> new PeerNotFoundException("Peer not found")
        );
        return makeRequestToHostForDownloadToken(peer);
    }

    public String getDownloadTokenForPeer(Principal principal, Long id) throws EntityNotFoundException {
        // load user
        UserEntity user = userRepo.findByLogin(principal.getName());
        if(user == null) throw new UserNotFoundException("User not found");

        PeerEntity peer = user.getPeers().stream()
                .filter(peerEntity -> peerEntity.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new PeerNotFoundException("Peer does not exist or you have no permission for this peer"));

        return makeRequestToHostForDownloadToken(peer);
    }
}
