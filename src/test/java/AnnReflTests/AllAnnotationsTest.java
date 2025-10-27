package AnnReflTests;

import ReflectionAnnotations.Email;
import ReflectionAnnotations.Notnull;
import ReflectionAnnotations.Range;
import ReflectionAnnotations.Size;
import ReflectionAnnotations.ValidationResult;
import ReflectionAnnotations.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class AllAnnotationsTest {

  @Test
  @DisplayName("Проверка всех видов аннотаций на одном объекте")
  void validate_AllAnnotationTypes_CombinedValidation() {
    // Given
    class TestAll {

      @Notnull(message = "Field1 required")
      private String field1 = "value";

      @Size(min = 2, max = 5, message = "Field2 size {min}-{max}")
      private String field2 = "valid";

      @Range(min = 10, max = 20, message = "Field3 range {min}-{max}")
      private Integer field3 = 15;

      @Email(message = "Field4 email invalid")
      private String field4 = "test@example.com";
    }

    // When
    ValidationResult result = Validator.validate(new TestAll());

    // Then
    assertTrue(result.getValid(), "Все поля должны быть валидны");
    assertTrue(result.getErrors().isEmpty(), "Не должно быть ошибок");
  }

  @Test
  @DisplayName("Несколько аннотаций на одном поле - все проверки выполняются")
  void validate_MultipleAnnotationsOnField_AllValidationsExecuted() {
    // Given
    ValidatorTest.UserWithMultipleAnnotations user = new ValidatorTest.UserWithMultipleAnnotations();

    // When
    ValidationResult result = Validator.validate(user);

    // Then
    assertFalse(result.getValid(), "Должна быть ошибка Size для shortEmail");
    assertEquals(1, result.getErrors().size());

    String error = result.getErrors().get(0);
    assertTrue(error.contains("shortEmail"), "Ошибка должна быть для shortEmail");
    assertTrue(error.contains("5"), "Ошибка должна содержать минимальную длину 5");
  }

  @Test
  @DisplayName("Сообщения об ошибках содержат подставленные параметры")
  void validate_ErrorMessages_ContainSubstitutedParameters() {
    // Given
    class TestMessages {

      @Size(min = 5, max = 10, message = "Must be between {min} and {max}")
      private String field = "abc";
    }

    // When
    ValidationResult result = Validator.validate(new TestMessages());

    // Then
    assertFalse(result.getValid());
    String errorMessage = result.getErrors().get(0);
    assertTrue(errorMessage.contains("5"), "Сообщение должно содержать min значение");
    assertTrue(errorMessage.contains("10"), "Сообщение должно содержать max значение");
    assertTrue(errorMessage.contains("Must be between 5 and 10"),
        "Сообщение должно иметь подставленные параметры");
  }
}