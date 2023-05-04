package com.bitniki.VPNconServer.modules.host.controller;

import com.bitniki.VPNconServer.modules.host.entity.HostEntity;
import com.bitniki.VPNconServer.modules.host.exception.HostAlreadyExistException;
import com.bitniki.VPNconServer.modules.host.exception.HostNotFoundException;
import com.bitniki.VPNconServer.modules.host.exception.HostValidationFailedException;
import com.bitniki.VPNconServer.modules.host.model.Host;
import com.bitniki.VPNconServer.modules.host.service.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/hosts")
public class HostController {
    @Autowired
    private HostService hostService;

    /**
     * Получает список всех хостов. Для использования требуется авторизация с ролью "host:read"
     * @return ResponseEntity с объектом типа List и статусом ответа.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('host:read')")
    public ResponseEntity<List<Host>> getAllHosts() {
        return ResponseEntity.ok(
                StreamSupport.stream(hostService.getAll(), false)
                        .map(Host::toModel)
                        .toList()
        );
    }

    /**
     * Получает хост по его идентификатору. Для использования требуется авторизация с ролью "host:read"
     * @param id идентификатор хоста.
     * @return ResponseEntity с объектом типа Host и статусом ответа.
     * @throws HostNotFoundException если хост не найден.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('host:read')")
    public ResponseEntity<Host> getHost (@PathVariable Long id)
            throws HostNotFoundException {
        return ResponseEntity.ok(
                Host.toModel(hostService.getOne(id))
        );
    }

    /**
     * Создает новый хост на основе переданных данных. Для использования требуется авторизация с ролью "host:write"
     * @param host данные нового хоста.
     * @return ResponseEntity с объектом типа Host и статусом ответа.
     * @throws HostAlreadyExistException если хост с данными именем или ip уже существует.
     * @throws HostValidationFailedException если данные хоста не прошли валидацию.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('host:write')")
    public ResponseEntity<Host> createHost(@RequestBody HostEntity host)
            throws HostAlreadyExistException, HostValidationFailedException {
        return ResponseEntity.ok(
                Host.toModel(hostService.create(host))
        );
    }

    /**
     * Обновляет данные существующего хоста. Для использования требуется авторизация с ролью "host:write"
     * @param id идентификатор хоста.
     * @param host новые данные хоста.
     * @return ResponseEntity с объектом типа Host и статусом ответа.
     * @throws HostNotFoundException если хост не найден.
     * @throws HostAlreadyExistException если хост с данными именем или ip уже существует.
     * @throws HostValidationFailedException если данные хоста не прошли валидацию.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('host:write')")
    public ResponseEntity<Host> updateHost (@PathVariable Long id, @RequestBody HostEntity host)
            throws HostNotFoundException, HostAlreadyExistException, HostValidationFailedException {
        return ResponseEntity.ok(
                Host.toModel(hostService.update(id, host))
        );
    }

    /**
     * Удаляет хост по его идентификатору.
     * @param id идентификатор хоста.
     * @return ResponseEntity с объектом типа Host, который был удален, и статусом ответа.
     * @throws HostNotFoundException если хост не найден.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('host:write')")
    public ResponseEntity<Host> deleteHost (@PathVariable Long id) throws HostNotFoundException {
        return ResponseEntity.ok(
                Host.toModel(hostService.delete(id))
        );
    }
}
