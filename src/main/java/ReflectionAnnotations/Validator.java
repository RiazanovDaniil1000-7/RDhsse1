package ReflectionAnnotations;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class Validator {
  // Регулярное выражение для проверки email
  private static final String EMAIL_PATTERN =
      "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
  private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

  public static ValidationResult validate(Object object) {
    ValidationResult result = new ValidationResult();

    if (object == null) {
      result.addError("Validated object cannot be null");
      return result;
    }

    Class<?> clazz = object.getClass();
    Field[] fields = clazz.getDeclaredFields();

    for (Field field : fields) {
      field.setAccessible(true);

      try {
        Object fieldValue = field.get(object);

        // Проверка @NotNull
        if (field.isAnnotationPresent(NotNull.class)) {
          validateNotNull(field, fieldValue, result);
        }

        // Проверка @Size (только для строк)
        if (field.isAnnotationPresent(Size.class) && fieldValue != null) {
          validateSize(field, fieldValue, result);
        }

        // Проверка @Range (только для чисел)
        if (field.isAnnotationPresent(Range.class) && fieldValue != null) {
          validateRange(field, fieldValue, result);
        }

        // Проверка @Email (только для строк)
        if (field.isAnnotationPresent(Email.class) && fieldValue != null) {
          validateEmail(field, fieldValue, result);
        }

      } catch (IllegalAccessException e) {
        result.addError("Cannot access field: " + field.getName());
      }
    }

    return result;
  }

  private static void validateNotNull(Field field, Object fieldValue, ValidationResult result) {
    NotNull annotation = field.getAnnotation(NotNull.class);
    if (fieldValue == null) {
      result.addError(annotation.message());
    }
  }

  private static void validateSize(Field field, Object fieldValue, ValidationResult result) {
    if (!(fieldValue instanceof String)) {
      result.addError("Field " + field.getName() + " must be a String for @Size validation");
      return;
    }

    Size annotation = field.getAnnotation(Size.class);
    String stringValue = (String) fieldValue;
    int length = stringValue.length();

    if (length < annotation.min() || length > annotation.max()) {
      String errorMessage = annotation.message()
          .replace("{min}", String.valueOf(annotation.min()))
          .replace("{max}", String.valueOf(annotation.max()));
      result.addError(errorMessage);
    }
  }

  private static void validateRange(Field field, Object fieldValue, ValidationResult result) {
    if (!isNumber(fieldValue)) {
      result.addError("Field " + field.getName() + " must be a number for @Range validation");
      return;
    }

    Range annotation = field.getAnnotation(Range.class);
    long numericValue = getNumericValue(fieldValue);

    if (numericValue < annotation.min() || numericValue > annotation.max()) {
      String errorMessage = annotation.message()
          .replace("{min}", String.valueOf(annotation.min()))
          .replace("{max}", String.valueOf(annotation.max()));
      result.addError(errorMessage);
    }
  }

  private static void validateEmail(Field field, Object fieldValue, ValidationResult result) {
    if (!(fieldValue instanceof String)) {
      result.addError("Field " + field.getName() + " must be a String for @Email validation");
      return;
    }

    Email annotation = field.getAnnotation(Email.class);
    String email = (String) fieldValue;

    if (!pattern.matcher(email).matches()) {
      result.addError(annotation.message());
    }
  }

  private static boolean isNumber(Object value) {
    return value instanceof Byte || value instanceof Short ||
        value instanceof Integer || value instanceof Long ||
        value instanceof Float || value instanceof Double;
  }

  private static long getNumericValue(Object value) {
    if (value instanceof Byte) return ((Byte) value).longValue();
    if (value instanceof Short) return ((Short) value).longValue();
    if (value instanceof Integer) return ((Integer) value).longValue();
    if (value instanceof Long) return (Long) value;
    if (value instanceof Float) return ((Float) value).longValue();
    if (value instanceof Double) return ((Double) value).longValue();
    return 0;
  }
}