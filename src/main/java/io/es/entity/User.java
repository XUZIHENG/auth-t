package io.es.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

  @Id
  @GeneratedValue
  Long id;

  @Column(unique = true)
  String username;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  String password;

  // @JsonIgnore
  // String secret;

  // @JsonIgnore
  // String salt;

  @ManyToMany(fetch = FetchType.EAGER)
  List<Role> roles;

  @ManyToOne
  District district;

  @JsonIgnore
  public List<Permission> getPermissions() {
    return getRoles().stream().map(Role::getPermissions).flatMap(Collection::stream).collect(Collectors.toList());
  }

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getPermissions();
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isEnabled() {
    return true;
  }

}
