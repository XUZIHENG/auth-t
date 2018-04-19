package io.es.entity.annotation;

import io.es.entity.District;
import io.es.entity.Resource;
import lombok.val;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ExtractorTest {

  @Test
  public void extract() {
    val district = new District(0L, "district", null);
    val resource = new Resource(1L, "resource", district);

    val extracted = Extractor.
      extract(resource, DistrictRestricted.class, District.class).
      collect(Collectors.toList());

    assertEquals(1, extracted.size());
    assertEquals(district, extracted.get(0));
  }

}
