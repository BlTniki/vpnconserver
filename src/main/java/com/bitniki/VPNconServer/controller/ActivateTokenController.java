package com.bitniki.VPNconServer.controller;

import com.bitniki.VPNconServer.entity.ActivateTokenEntity;
import com.bitniki.VPNconServer.exception.notFoundException.EntityNotFoundException;
import com.bitniki.VPNconServer.exception.notFoundException.RoleNotFoundException;
import com.bitniki.VPNconServer.service.ActivateTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/activate_token")
public class ActivateTokenController {
    @Autowired
    private ActivateTokenService activateTokenService;

    @PostMapping("/gen")
    @PreAuthorize("hasAuthority('any')" +
            "&& hasAuthority('gen_token')")
    public ResponseEntity<ActivateTokenEntity> generateToken(@RequestParam String role) throws RoleNotFoundException {
        return ResponseEntity.ok(activateTokenService.generate(role));
    }

    @GetMapping("/use")
    @PreAuthorize("hasAuthority('any')")
    public ResponseEntity<String> changeMineUserRole(@RequestParam Long user_id, @RequestParam String token)
            throws EntityNotFoundException {
        return ResponseEntity.ok(activateTokenService.changeRole(user_id, token));
    }

    @GetMapping("/use/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changeUserRole(Principal principal, @RequestParam String token) throws EntityNotFoundException {
        return ResponseEntity.ok(activateTokenService.changeMineRole(principal, token));
    }
}
