package com.biursite.application.shared.security;

import java.util.Optional;

public interface CurrentUserPort {
    Optional<CurrentUser> getCurrentUser();
    Long getCurrentUserId();
    boolean currentUserHasRole(String role);
}
