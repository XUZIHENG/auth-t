package io.es.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Role {

  @Id
  @GeneratedValue
  Long id;

  @Column(unique = true)
  String name;

  @ManyToMany(fetch = FetchType.EAGER)
  List<Permission> permissions;

}
