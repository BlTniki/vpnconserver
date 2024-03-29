package com.bitniki.VPNconServer.modules.role;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Роль юзера. Роли подразумевают различный набор разрешений для доступа к конечным точкам приложения.
 */
@SuppressWarnings("unused")
public enum Role {
    ADMIN(Set.of(Permission.values())),
    ACTIVATED_CLOSE_USER(Set.of(Permission.Personal,
                                Permission.User_READ, Permission.User_WRITE,
                                Permission.Peer_READ, Permission.Peer_WRITE,
                                Permission.Host_READ,
                                Permission.Metacode_USE,
                                Permission.Subscription_READ,
                                Permission.UserSubscription_READ)),
    ACTIVATED_USER(Set.of(  Permission.Personal,
                            Permission.User_READ, Permission.User_WRITE,
                            Permission.Peer_READ, Permission.Peer_WRITE,
                            Permission.Host_READ,
                            Permission.Metacode_USE,
                            Permission.Subscription_READ,
                            Permission.UserSubscription_READ)),
    DEACTIVATED_USER(Set.of(   Permission.Personal,
                            Permission.User_READ, Permission.User_WRITE));
    private final Set<Permission> permissionSet;

    Role(Set<Permission> permissionSet) {
        this.permissionSet = permissionSet;
    }

    public Set<Permission> getPermissionSet() {
        return permissionSet;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return getPermissionSet().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}
