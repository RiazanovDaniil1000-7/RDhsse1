package AnnReflTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ReflectionAnnotations.ValidationResult;
import ReflectionAnnotations.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ValidatorSuccessTest {

  @Test
  @DisplayName("Успешная валидация корректного объекта")
  void validate_ValidObject_ReturnsNoErrors() {
    ValidatorTest.ValidUser user = new ValidatorTest.ValidUser();
    ValidationResult result = Validator.validate(user);
    assertTrue(result.getValid(), "Валидация должна пройти успешно");
    assertTrue(result.getErrors().isEmpty(), "Список ошибок должен быть пустым");
    assertEquals(0, result.getErrors().size(), "Не должно быть ошибок");
  }

  @Test
  @DisplayName("Валидация объекта без аннотаций")
  void validate_ObjectWithoutAnnotations_ReturnsNoErrors() {
    ValidatorTest.EmptyObject empty = new ValidatorTest.EmptyObject();
    ValidationResult result = Validator.validate(empty);
    assertTrue(result.getValid(), "Объект без аннотаций должен быть валидным");
    assertTrue(result.getErrors().isEmpty(), "Не должно быть ошибок");
  }

  @Test
  @DisplayName("Валидация пограничных значений")
  void validate_BoundaryValues_ReturnsNoErrors() {
    ValidatorTest.BoundaryCases boundary = new ValidatorTest.BoundaryCases();
    ValidationResult result = Validator.validate(boundary);
    assertTrue(result.getValid(), "Пограничные значения должны быть валидны");
    assertTrue(result.getErrors().isEmpty(), "Не должно быть ошибок");
  }
}
