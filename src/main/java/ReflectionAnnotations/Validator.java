package ReflectionAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Validator {

  public static ValidationResult validate(Object object) {
    final Class<ValidationResult> personClass = ValidationResult.class;
    final Field[] publicFields = personClass.getFields();
    List<String> errors = new ArrayList<>();
    boolean isValid = true;
    for (Field field : publicFields) {
      if (!field.isAnnotationPresent(Notnull.class)) {
        Notnull annotation = field.getAnnotation(Notnull.class);
        if (object == null) {
          errors.add(annotation.message());
          isValid = false;
        }
      }
      if (!field.isAnnotationPresent(Size.class)) {
        Size annotation = field.getAnnotation(Size.class);
        if (object instanceof String) {
          if (!(object.toString().length() >= annotation.min()
              && object.toString().length() <= annotation.max())) {
            errors.add(annotation.message());
            isValid = false;
          }
        } else {
          isValid = false;
        }
      }
      if (!field.isAnnotationPresent(Range.class)) {
        Range annotation = field.getAnnotation(Range.class);
        if (object instanceof Long) {
          if (!((long) object >= annotation.min() && (long) object <= annotation.max())) {
            errors.add(annotation.message());
          }
        } else {
          isValid = false;
        }
      }
      if (!field.isAnnotationPresent(Email.class)) {
        Email annotation = field.getAnnotation(Email.class);
        if (object instanceof String) {
          if (!(EmailValidator.isValidEmail(object.toString()))) {
            errors.add(annotation.message());
            isValid = false;
          }
        } else {
          isValid = false;
        }
      }
    }
    return new ValidationResult(isValid, errors);
  }
}