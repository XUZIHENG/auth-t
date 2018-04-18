package io.es.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Permission implements GrantedAuthority {

  @Id
  @GeneratedValue
  Long id;

  @Column(unique = true)
  String value;

  @JsonIgnore
  @Override
  public String getAuthority() {
    return getValue();
  }

}
