package io.es.configuration;

import io.es.entity.Permission;
import io.es.entity.User;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
public class PermissionConfiguration implements PermissionEvaluator {

  @Override
  public boolean hasPermission(Authentication authentication, Object target, Object permission) {
    if (permission instanceof String) return hasPermission(authentication, ((String) permission));
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
    throw new UnsupportedOperationException();
  }

  private boolean hasPermission(Authentication authentication, String permission) {
    val user = (User) authentication.getPrincipal();
    val permitted = user.getPermissions().stream().map(Permission::getValue).anyMatch(permission::equals);
    logger.trace("{} Permit '{}' for user '{}'", permitted ? "✓" : "✗", permission, user.getUsername());
    return permitted;
  }

}
