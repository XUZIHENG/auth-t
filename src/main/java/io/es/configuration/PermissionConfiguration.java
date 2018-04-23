package io.es.configuration;

import io.es.entity.District;
import io.es.entity.Permission;
import io.es.entity.User;
import io.es.entity.annotation.DistrictRestricted;
import io.es.entity.annotation.Extractor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionConfiguration implements PermissionEvaluator {

  @Override
  public boolean hasPermission(Authentication authentication, @NonNull Object target, @NonNull Object permission) {
    if (authentication.getPrincipal() == null || !(authentication.getPrincipal() instanceof User)) return false;
    val user = (User) authentication.getPrincipal();
    val permitted = hasPermission(user, permission.toString()) && hasPermission(user.getDistrict(), target);
    logger.debug("{} '{}' @ '{}'", permitted ? "✓" : "✗", permission, user.getUsername());
    logger.trace("user = {}", user);
    logger.trace("target = {}", target);
    return permitted;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
    val target = Optional.<Object>ofNullable(manager.find(getEntityClass(targetType), targetId)).orElse("");
    return hasPermission(authentication, target, permission);
  }

  private boolean hasPermission(@NonNull User user, String permission) {
    return user.getPermissions().stream().map(Permission::getValue).anyMatch(permission::equals);
  }

  private boolean hasPermission(@NonNull District district, Object target) {
    return !target.getClass().isAnnotationPresent(Entity.class) ||
      extractDistricts(target).allMatch(district::contains);
  }

  private Stream<District> extractDistricts(Object target) {
    return Extractor.extract(target, DistrictRestricted.class, District.class);
  }

  private Class<?> getEntityClass(String targetType) {
    try {
      return Class.forName(getPackageName(Permission.class) + capitalize(targetType));
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  private String getPackageName(Class<?> c) {
    val n = c.getCanonicalName();
    return n.substring(0, n.lastIndexOf('.') + 1);
  }

  private String capitalize(@NonNull String s) {
    return (s.length() == 0 || Character.isUpperCase(s.charAt(0))) ? s :
      Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

  private final EntityManager manager;

}
