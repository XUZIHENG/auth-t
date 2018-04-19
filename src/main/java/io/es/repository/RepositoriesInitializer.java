package io.es.repository;

import io.es.entity.*;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Service
public class RepositoriesInitializer {

  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  private final PermissionRepository permissionRepository;

  private final ResourceRepository resourceRepository;

  private final DistrictRepository districtRepository;

  @Autowired
  public RepositoriesInitializer(
    UserRepository userRepository,
    RoleRepository roleRepository,
    PermissionRepository permissionRepository,
    ResourceRepository resourceRepository,
    DistrictRepository districtRepository
  ) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
    this.resourceRepository = resourceRepository;
    this.districtRepository = districtRepository;
  }

  @Getter
  private Map<String, User> users;

  @Getter
  private Map<String, Role> roles;

  @Getter
  private Map<String, Permission> permissions;

  @Getter
  private Map<String, Resource> resources;

  @Getter
  private Map<String, District> districts;

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
    initializeDistricts();
    initializeResources();
    initializePermissions();
    initializeRoles();
    initializeUsers();
  }

  private void initializeDistricts() {
    assertEmpty(districtRepository);

    val districtRoot = districtRepository.save(new District(0L, "district-root", null));
    val districtL1N1 = districtRepository.save(new District(0L, "district-1-1", districtRoot));
    val districtL1N2 = districtRepository.save(new District(0L, "district-1-2", districtRoot));
    val districtL2N1 = districtRepository.save(new District(0L, "district-2-1", districtL1N1));

    districts = Stream.of(districtRoot, districtL1N1, districtL1N2, districtL2N1).
      collect(Collectors.toMap(District::getName, Function.identity()));

    assertNonEmpty(districtRepository);
  }

  private void initializeResources() {
    assertEmpty(resourceRepository);

    resources = resourceRepository.saveAll(asList(
      new Resource(0L, "resource-1-1", districts.get("district-1-1")),
      new Resource(0L, "resource-1-2", districts.get("district-1-2")),
      new Resource(0L, "resource-2-1", districts.get("district-2-1"))
    )).stream().collect(Collectors.toMap(Resource::getValue, Function.identity()));

    assertNonEmpty(resourceRepository);
  }

  private void initializePermissions() {
    assertEmpty(permissionRepository);

    permissions = permissionRepository.saveAll(asList(
      new Permission(0L, "resource:read"),
      new Permission(0L, "resource:write")
    )).stream().collect(Collectors.toMap(Permission::getValue, Function.identity()));

    assertNonEmpty(permissionRepository);
  }

  private void initializeRoles() {
    assertEmpty(roleRepository);

    roles = roleRepository.saveAll(asList(
      new Role(0L, "guest", singletonList(permissions.get("resource:read"))),
      new Role(0L, "admin", asList(permissions.values().toArray(new Permission[0])))
    )).stream().collect(Collectors.toMap(Role::getName, Function.identity()));

    assertNonEmpty(roleRepository);
  }

  private void initializeUsers() {
    assertEmpty(userRepository);

    val encoder = new BCryptPasswordEncoder();

    users = userRepository.saveAll(asList(
      User.builder().
        username("guest-1-1").
        password(encoder.encode("q")).
        roles(singletonList(roles.get("guest"))).
        district(districts.get("district-1-1")).
        build(),
      User.builder().
        username("admin-1-1").
        password(encoder.encode("q")).
        roles(singletonList(roles.get("admin"))).
        district(districts.get("district-1-1")).
        build(),
      User.builder().
        username("guest").
        password(encoder.encode("q")).
        roles(singletonList(roles.get("guest"))).
        district(districts.get("district-root")).
        build(),
      User.builder().
        username("admin").
        password(encoder.encode("q")).
        roles(singletonList(roles.get("admin"))).
        district(districts.get("district-root")).
        build()
    )).stream().collect(Collectors.toMap(User::getUsername, Function.identity()));

    assertNonEmpty(userRepository);
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
