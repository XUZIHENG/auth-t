package io.es.entity.annotation;

import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.stream.Stream;

@UtilityClass
public class Extractor {

  public <T> Stream<T> extract(Object target, Class<? extends Annotation> annotation, Class<T> fieldType) {
    return Arrays.stream(target.getClass().getMethods()).
      filter(m -> m.isAnnotationPresent(annotation)).
      flatMap(getter -> {
        try {
          return Stream.of((fieldType.cast(getter.invoke(target))));
        } catch (Exception e) {
          return Stream.empty();
        }
      });
  }

}
