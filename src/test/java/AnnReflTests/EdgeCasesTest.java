package AnnReflTests;

import ReflectionAnnotations.Notnull;
import ReflectionAnnotations.Range;
import ReflectionAnnotations.Size;
import ReflectionAnnotations.ValidationResult;
import ReflectionAnnotations.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeCasesTest {

  @Test
  @DisplayName("Валидация null объекта")
  void validate_NullObject_ReturnsError() {
    ValidationResult result = Validator.validate(null);
    assertFalse(result.getValid());
    assertEquals(1, result.getErrors().size());
    assertTrue(result.getErrors().get(0).contains("object"));
    assertTrue(result.getErrors().get(0).contains("cannot be null"));
  }

  @Test
  @DisplayName("Аннотации на полях несовместимых типов")
  void validate_WrongTypeAnnotations_ReturnsTypeErrors() {
    ValidatorTest.UserWithWrongTypes user = new ValidatorTest.UserWithWrongTypes();
    ValidationResult result = Validator.validate(user);
    assertFalse(result.getValid());
    assertEquals(3, result.getErrors().size(), "Должно быть 3 ошибки несовместимости типов");
    List<String> errors = result.getErrors();
    boolean hasSizeTypeError = errors.stream()
        .anyMatch(e -> e.contains("number") && e.contains("String"));
    boolean hasRangeTypeError = errors.stream()
        .anyMatch(e -> e.contains("text") && e.contains("numeric"));
    boolean hasEmailTypeError = errors.stream()
        .anyMatch(e -> e.contains("flag") && e.contains("String"));
    assertTrue(hasSizeTypeError, "Должна быть ошибка типа для Size");
    assertTrue(hasRangeTypeError, "Должна быть ошибка типа для Range");
    assertTrue(hasEmailTypeError, "Должна быть ошибка типа для Email");
  }

  @Test
  @DisplayName("Поля с null значениями и без @NotNull аннотации")
  void validate_NullFieldsWithoutNotNull_NoValidationErrors() {
    class TestNulls {

      private String nullableField = null;
      private Integer nullableNumber = null;
    }
    ValidationResult result = Validator.validate(new TestNulls());
    assertTrue(result.getValid(), "Null поля без @NotNull должны быть допустимы");
    assertTrue(result.getErrors().isEmpty(), "Не должно быть ошибок");
  }

  @Test
  @DisplayName("Различные числовые типы с @Range аннотацией")
  void validate_DifferentNumericTypes_AllSupported() {
    class TestNumericTypes {

      @Range(min = 1, max = 100)
      private Byte byteValue = 50;

      @Range(min = 1, max = 100)
      private Short shortValue = 50;

      @Range(min = 1, max = 100)
      private Integer intValue = 50;

      @Range(min = 1, max = 100)
      private Long longValue = 50L;

      @Range(min = 0, max = 100)
      private Float floatValue = 50.5f;

      @Range(min = 0, max = 100)
      private Double doubleValue = 50.5;
    }
    ValidationResult result = Validator.validate(new TestNumericTypes());
    assertTrue(result.getValid(), "Все числовые типы должны поддерживаться");
    assertTrue(result.getErrors().isEmpty(), "Не должно быть ошибок");
  }

  @Test
  @DisplayName("Объект с приватными полями - доступ через reflection")
  void validate_PrivateFields_AccessibleViaReflection() {
    class PrivateFields {

      @Notnull(message = "Private field required")
      private String privateField = "value";

      @Size(min = 3)
      private String anotherPrivate = "abc";
    }
    ValidationResult result = Validator.validate(new PrivateFields());
    assertTrue(result.getValid(), "Приватные поля должны быть доступны через reflection");
  }
}
