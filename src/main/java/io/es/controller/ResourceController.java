package io.es.controller;

import io.es.entity.User;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Slf4j
@RepositoryRestController
@RequestMapping("/resources")
public class ResourceController {

  @GetMapping("/p/r")
  @PreAuthorize("hasPermission('', 'resource:read')")
  public ResponseEntity hasReadPermission(@NonNull Principal principal) {
    val user = (User) ((Authentication) principal).getPrincipal();
    return ResponseEntity.ok(user);
  }

  @GetMapping("/p/w")
  @PreAuthorize("hasPermission('', 'resource:write')")
  public ResponseEntity hasCreatePermission(@NonNull Principal principal) {
    val user = (User) ((Authentication) principal).getPrincipal();
    return ResponseEntity.ok(user);
  }

}
