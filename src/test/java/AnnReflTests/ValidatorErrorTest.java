package AnnReflTests;

import ReflectionAnnotations.ValidationResult;
import ReflectionAnnotations.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ValidatorErrorTest {

  @Test
  @DisplayName("Валидация с ошибками @NotNull")
  void validate_NotNullViolations_ReturnsCorrectErrors() {
    ValidatorTest.UserWithNulls user = new ValidatorTest.UserWithNulls();
    ValidationResult result = Validator.validate(user);
    assertFalse(result.getValid());
    assertEquals(1, result.getErrors().size());
    assertTrue(result.getErrors().contains("name: Name required"));
  }

  @Test
  @DisplayName("Валидация с ошибками @Size")
  void validate_SizeViolations_ReturnsCorrectErrors() {
    ValidatorTest.UserWithSizeErrors user = new ValidatorTest.UserWithSizeErrors();
    ValidationResult result = Validator.validate(user);
    assertFalse(result.getValid());
    assertEquals(2, result.getErrors().size(), "Должно быть 2 ошибки Size");
    List<String> errors = result.getErrors();
    boolean hasShortError = errors.stream()
        .anyMatch(e -> e.contains("shortString") && e.contains("5-10"));
    boolean hasLongError = errors.stream()
        .anyMatch(e -> e.contains("longString") && e.contains("1-3"));
    assertTrue(hasShortError, "Должна быть ошибка для shortString");
    assertTrue(hasLongError, "Должна быть ошибка для longString");
  }

  @Test
  @DisplayName("Валидация с ошибками @Range")
  void validate_RangeViolations_ReturnsCorrectErrors() {
    ValidatorTest.UserWithRangeErrors user = new ValidatorTest.UserWithRangeErrors();
    ValidationResult result = Validator.validate(user);
    assertFalse(result.getValid());
    assertEquals(2, result.getErrors().size(), "Должно быть 2 ошибки Range");
    List<String> errors = result.getErrors();
    boolean hasSmallError = errors.stream()
        .anyMatch(e -> e.contains("smallNumber") && e.contains("10-100"));
    boolean hasLargeError = errors.stream()
        .anyMatch(e -> e.contains("largeNumber") && e.contains("0-50"));
    assertTrue(hasSmallError, "Должна быть ошибка для smallNumber");
    assertTrue(hasLargeError, "Должна быть ошибка для largeNumber");
  }

  @Test
  @DisplayName("Валидация с ошибками @Email")
  void validate_EmailViolations_ReturnsCorrectErrors() {
    ValidatorTest.UserWithEmailErrors user = new ValidatorTest.UserWithEmailErrors();
    ValidationResult result = Validator.validate(user);
    assertFalse(result.getValid());
    assertEquals(5, result.getErrors().size(), "Должно быть 5 ошибок Email");
    List<String> errors = result.getErrors();
    long emailErrors = errors.stream().filter(e -> e.contains("email")).count();
    assertEquals(5, emailErrors, "Все ошибки должны быть для email полей");
  }
}
