package io.es.repository;

import io.es.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

  @PreAuthorize("hasPermission(#id, 'resource', 'resource:read')")
  @Override
  Optional<Resource> findById(Long id);

  @PreAuthorize("hasPermission(#id, 'resource', 'resource:write')")
  @Override
  void deleteById(Long id);

  @PreAuthorize("hasPermission(#resource, 'resource:write')")
  @Override
  void delete(Resource resource);

}
