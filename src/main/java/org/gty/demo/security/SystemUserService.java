package org.gty.demo.security;

import org.gty.demo.model.entity.SystemUser;
import org.gty.demo.model.entity.SystemUserRole;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public interface SystemUserService {

    @Nonnull
    Optional<SystemUser> findUserByUsername(@Nonnull String username);

    @Nonnull
    Collection<SystemUserRole> findRolesByUsername(@Nonnull String username);
}
