package io.es.entity;

import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DistrictTest {

  @Test
  public void contains() {
    val d00 = new District(0L, "d00", null);
    val d11 = new District(11L, "d11", d00);
    val d12 = new District(12L, "d12", d00);
    val d21 = new District(21L, "d21", d11);
    val d22 = new District(22L, "d22", d12);

    assertTrue(d00.contains(d00));
    assertTrue(d00.contains(d11));
    assertTrue(d00.contains(d12));
    assertTrue(d00.contains(d21));
    assertTrue(d00.contains(d22));

    assertFalse(d11.contains(d00));
    assertTrue(d11.contains(d11));
    assertFalse(d11.contains(d12));
    assertTrue(d11.contains(d21));
    assertFalse(d11.contains(d22));

    assertFalse(d21.contains(d00));
    assertFalse(d21.contains(d11));
    assertFalse(d21.contains(d12));
    assertTrue(d21.contains(d21));
    assertFalse(d21.contains(d22));
  }

}
