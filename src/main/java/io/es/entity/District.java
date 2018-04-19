package io.es.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class District {

  @Id
  @GeneratedValue
  Long id;

  String name;

  @Nullable
  @ManyToOne
  District higher;

  public boolean contains(District d) {
    return d != null && (getId().equals(d.getId()) || contains(d.getHigher()));
  }

}
