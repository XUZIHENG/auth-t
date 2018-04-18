package io.es.repository;

import io.es.entity.Permission;
import io.es.entity.Role;
import io.es.entity.User;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class RepositoriesInitializer {

  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  private final PermissionRepository permissionRepository;

  @Autowired
  public RepositoriesInitializer(
    UserRepository userRepository,
    RoleRepository roleRepository,
    PermissionRepository permissionRepository
  ) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
  }

  private final Object lock = new Object();

  private volatile boolean initialized = false;

  public void initialize() {
    if (!initialized) {
      synchronized (lock) {
        if (!initialized) {
          initializeAll();
          initialized = true;
        }
      }
    }
  }

  private void initializeAll() {
    initializeUsers();
  }

  @SuppressWarnings({"ArraysAsListWithZeroOrOneArgument", "unused"})
  private void initializeUsers() {
    assertEmpty(userRepository);
    assertEmpty(roleRepository);
    assertEmpty(permissionRepository);

    val encoder = new BCryptPasswordEncoder();

    val permissionResourceRead = permissionRepository.save(new Permission(0L,
      "resource:read"
    ));
    val permissionResourceWrite = permissionRepository.save(new Permission(0L,
      "resource:write"
    ));

    val roleGuest = roleRepository.save(new Role(0L,
      "guest", Arrays.asList(permissionResourceRead)
    ));
    val roleAdmin = roleRepository.save(new Role(0L,
      "admin", Arrays.asList(permissionResourceRead, permissionResourceWrite)
    ));

    val guest = userRepository.save(new User(0L,
      "guest", encoder.encode("q"), Arrays.asList(roleGuest)
    ));
    val admin = userRepository.save(new User(0L,
      "admin", encoder.encode("q"), Arrays.asList(roleAdmin)
    ));

    assertNonEmpty(userRepository);
    assertNonEmpty(roleRepository);
    assertNonEmpty(permissionRepository);
  }

  private void assertEmpty(CrudRepository<?, ?> repository) {
    assertCount(repository, 0L);
  }

  private void assertNonEmpty(CrudRepository<?, ?> repository) {
    assertCountGTE(repository, 1L);
  }

  private void assertCount(CrudRepository<?, ?> repository, long count) {
    assertTrue(repository.count() == count);
  }

  private void assertCountGTE(CrudRepository<?, ?> repository, long count) {
    assertTrue(repository.count() >= count);
  }

  private void assertTrue(boolean condition) {
    if (!condition) throw new AssertionError();
  }

}
