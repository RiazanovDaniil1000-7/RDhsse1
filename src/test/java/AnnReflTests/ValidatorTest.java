package AnnReflTests;

import ReflectionAnnotations.Email;
import ReflectionAnnotations.Range;
import ReflectionAnnotations.Size;
import ReflectionAnnotations.ValidationResult;
import ReflectionAnnotations.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

  private User validUser;

  @BeforeEach
  void setUp() {
    // Создаем валидного пользователя для тестов
    validUser = new User();
    validUser.setName("Иван Иванов");
    validUser.setEmail("ivan@example.com");
    validUser.setAge(25);
    validUser.setPassword("securePassword123");
  }

  @Test
  @DisplayName("Успешная валидация корректного объекта")
  void testSuccessfulValidation() {
    // Arrange
    User user = new User("Анна Петрова", "anna@domain.ru", 30, "strongPwd456");

    // Act
    ValidationResult result = Validator.validate(user);

    // Assert
    assertTrue(result.isValid(), "Валидный объект должен пройти проверку");
    assertEquals(0, result.getErrors().size(), "Не должно быть ошибок валидации");
  }

  @Test
  @DisplayName("Валидация с различными типами ошибок")
  void testValidationWithMultipleErrors() {
    // Arrange
    User invalidUser = new User();
    invalidUser.setName("A"); // слишком короткое имя
    invalidUser.setEmail("invalid.email"); // некорректный email
    invalidUser.setAge(-5); // отрицательный возраст
    invalidUser.setPassword("123"); // слишком короткий пароль

    // Act
    ValidationResult result = Validator.validate(invalidUser);

    // Assert
    assertFalse(result.isValid(), "Объект с ошибками должен не проходить валидацию");
    List<String> errors = result.getErrors();
    assertTrue(errors.size() >= 4, "Должно быть как минимум 4 ошибки");

    // Проверяем конкретные ошибки
    assertTrue(errors.stream().anyMatch(e -> e.contains("Имя должно быть от 2 до 50")),
        "Должна быть ошибка о длине имени");
    assertTrue(errors.stream().anyMatch(e -> e.contains("Некорректный формат email")),
        "Должна быть ошибка о email");
    assertTrue(errors.stream().anyMatch(e -> e.contains("Возраст должен быть от 0 до 150")),
        "Должна быть ошибка о возрасте");
    assertTrue(errors.stream().anyMatch(e -> e.contains("Пароль должен быть от 6 до 20")),
        "Должна быть ошибка о длине пароля");
  }

  @Test
  @DisplayName("Проверка аннотации @NotNull")
  void testNotNullValidation() {
    // Arrange
    User userWithNullFields = new User();
    userWithNullFields.setName(null); // null имя
    userWithNullFields.setEmail(null); // null email
    userWithNullFields.setAge(25); // валидный возраст
    userWithNullFields.setPassword("password123"); // валидный пароль

    // Act
    ValidationResult result = Validator.validate(userWithNullFields);

    // Assert
    assertFalse(result.isValid());
    List<String> errors = result.getErrors();
    assertTrue(errors.stream().anyMatch(e -> e.contains("Имя не может быть null")));
    assertTrue(errors.stream().anyMatch(e -> e.contains("Email не может быть null")));
  }

  @Test
  @DisplayName("Проверка аннотации @Size - граничные значения")
  void testSizeValidationBoundaryValues() {
    // Test 1: Минимальная допустимая длина
    User userWithMinSize = new User();
    userWithMinSize.setName("AB"); // ровно 2 символа
    userWithMinSize.setEmail("test@example.com");
    userWithMinSize.setAge(25);
    userWithMinSize.setPassword("123456"); // ровно 6 символов

    ValidationResult result1 = Validator.validate(userWithMinSize);
    assertTrue(result1.isValid(), "Поля с минимальной допустимой длиной должны быть валидны");

    // Test 2: Максимальная допустимая длина
    User userWithMaxSize = new User();
    userWithMaxSize.setName("A".repeat(50)); // ровно 50 символов
    userWithMaxSize.setEmail("test@example.com");
    userWithMaxSize.setAge(25);
    userWithMaxSize.setPassword("A".repeat(20)); // ровно 20 символов

    ValidationResult result2 = Validator.validate(userWithMaxSize);
    assertTrue(result2.isValid(), "Поля с максимальной допустимой длиной должны быть валидны");

    // Test 3: Слишком короткое (ниже минимума)
    User userTooShort = new User();
    userTooShort.setName("A"); // 1 символ
    userTooShort.setEmail("test@example.com");
    userTooShort.setAge(25);
    userTooShort.setPassword("12345"); // 5 символов

    ValidationResult result3 = Validator.validate(userTooShort);
    assertFalse(result3.isValid());
    assertEquals(2, result3.getErrors().size());

    // Test 4: Слишком длинное (выше максимума)
    User userTooLong = new User();
    userTooLong.setName("A".repeat(51)); // 51 символ
    userTooLong.setEmail("test@example.com");
    userTooLong.setAge(25);
    userTooLong.setPassword("A".repeat(21)); // 21 символ

    ValidationResult result4 = Validator.validate(userTooLong);
    assertFalse(result4.isValid());
    assertEquals(2, result4.getErrors().size());
  }

  @Test
  @DisplayName("Проверка аннотации @Range - граничные значения")
  void testRangeValidationBoundaryValues() {
    // Test 1: Минимальное допустимое значение
    User userWithMinAge = new User();
    userWithMinAge.setName("Тест Тестов");
    userWithMinAge.setEmail("test@example.com");
    userWithMinAge.setAge(0); // минимальный возраст
    userWithMinAge.setPassword("password123");

    ValidationResult result1 = Validator.validate(userWithMinAge);
    assertTrue(result1.isValid(), "Возраст 0 должен быть допустим");

    // Test 2: Максимальное допустимое значение
    User userWithMaxAge = new User();
    userWithMaxAge.setName("Тест Тестов");
    userWithMaxAge.setEmail("test@example.com");
    userWithMaxAge.setAge(150); // максимальный возраст
    userWithMaxAge.setPassword("password123");

    ValidationResult result2 = Validator.validate(userWithMaxAge);
    assertTrue(result2.isValid(), "Возраст 150 должен быть допустим");

    // Test 3: Ниже минимума
    User userWithNegativeAge = new User();
    userWithNegativeAge.setName("Тест Тестов");
    userWithNegativeAge.setEmail("test@example.com");
    userWithNegativeAge.setAge(-1);
    userWithNegativeAge.setPassword("password123");

    ValidationResult result3 = Validator.validate(userWithNegativeAge);
    assertFalse(result3.isValid());
    assertTrue(result3.getErrors().get(0).contains("Возраст должен быть от 0 до 150"));

    // Test 4: Выше максимума
    User userWithTooBigAge = new User();
    userWithTooBigAge.setName("Тест Тестов");
    userWithTooBigAge.setEmail("test@example.com");
    userWithTooBigAge.setAge(151);
    userWithTooBigAge.setPassword("password123");

    ValidationResult result4 = Validator.validate(userWithTooBigAge);
    assertFalse(result4.isValid());
    assertTrue(result4.getErrors().get(0).contains("Возраст должен быть от 0 до 150"));
  }

  @Test
  @DisplayName("Проверка аннотации @Email - различные форматы")
  void testEmailValidation() {
    // Test 1: Валидные email форматы
    String[] validEmails = {
        "user@example.com",
        "user.name@domain.co.uk",
        "user+tag@example.org",
        "user_name@domain.com",
        "123@numbers.com"
    };

    for (String email : validEmails) {
      User user = new User();
      user.setName("Тест Тестов");
      user.setEmail(email);
      user.setAge(25);
      user.setPassword("password123");

      ValidationResult result = Validator.validate(user);
      assertTrue(result.isValid(), "Email '" + email + "' должен быть валидным");
    }

    // Test 2: Невалидные email форматы
    String[] invalidEmails = {
        "invalid.email",
        "@domain.com",
        "user@.com",
        "user@domain.",
        "user@domain..com",
        "user@com"
    };

    for (String email : invalidEmails) {
      User user = new User();
      user.setName("Тест Тестов");
      user.setEmail(email);
      user.setAge(25);
      user.setPassword("password123");

      ValidationResult result = Validator.validate(user);
      assertFalse(result.isValid(), "Email '" + email + "' должен быть невалидным");
      assertTrue(result.getErrors().get(0).contains("Некорректный формат email"));
    }
  }

  @Test
  @DisplayName("Проверка полей без аннотаций")
  void testFieldsWithoutAnnotations() {
    // Arrange
    User user = new User();
    user.setName("Тест Тестов");
    user.setEmail("test@example.com");
    user.setAge(25);
    user.setPassword("password123");
    user.setOptionalField(null); // Поле без аннотаций может быть null

    // Act
    ValidationResult result = Validator.validate(user);

    // Assert
    assertTrue(result.isValid(), "Поля без аннотаций не должны проверяться");
  }

  @Test
  @DisplayName("Проверка null объекта")
  void testNullObjectValidation() {
    // Act
    ValidationResult result = Validator.validate(null);

    // Assert
    assertFalse(result.isValid());
    assertEquals(1, result.getErrors().size());
    assertEquals("Validated object cannot be null", result.getErrors().get(0));
  }

  @Test
  @DisplayName("Проверка разных числовых типов с @Range")
  void testDifferentNumericTypes() {
    // Создаем тестовый класс с разными числовыми типами
    class NumericTestClass {

      @Range(min = 1, max = 100, message = "Byte должен быть от 1 до 100")
      private Byte byteField = 50;

      @Range(min = 1, max = 1000, message = "Short должен быть от 1 до 1000")
      private Short shortField = 500;

      @Range(min = 0, max = 10000, message = "Integer должен быть от 0 до 10000")
      private Integer intField = 5000;

      @Range(min = -1000, max = 1000, message = "Long должен быть от -1000 до 1000")
      private Long longField = 0L;

      @Range(min = 0, max = 100, message = "Float должен быть от 0 до 100")
      private Float floatField = 50.5f;

      @Range(min = 0, max = 100, message = "Double должен быть от 0 до 100")
      private Double doubleField = 99.99;

      // Конструктор
      public NumericTestClass() {
      }
    }

    // Test 1: Валидные значения
    NumericTestClass validNumeric = new NumericTestClass();
    ValidationResult result1 = Validator.validate(validNumeric);
    assertTrue(result1.isValid(), "Все числовые поля в допустимых диапазонах");

    // Test 2: Невалидные значения
    NumericTestClass invalidNumeric = new NumericTestClass();
    invalidNumeric.byteField = 0; // меньше минимума
    invalidNumeric.shortField = 1001; // больше максимума
    invalidNumeric.intField = -1; // меньше минимума
    invalidNumeric.longField = 2000L; // больше максимума
    invalidNumeric.floatField = 101.0f; // больше максимума
    invalidNumeric.doubleField = -0.1; // меньше минимума

    // Установим значения через reflection, так как поля приватные
    try {
      java.lang.reflect.Field byteField = invalidNumeric.getClass().getDeclaredField("byteField");
      byteField.setAccessible(true);
      byteField.set(invalidNumeric, (byte) 0);

      java.lang.reflect.Field shortField = invalidNumeric.getClass().getDeclaredField("shortField");
      shortField.setAccessible(true);
      shortField.set(invalidNumeric, (short) 1001);

      // ... аналогично для других полей
    } catch (Exception e) {
      fail("Не удалось установить значения полей через reflection: " + e.getMessage());
    }

    ValidationResult result2 = Validator.validate(invalidNumeric);
    assertFalse(result2.isValid());
    assertTrue(result2.getErrors().size() >= 6, "Должны быть ошибки для всех числовых полей");
  }

  @Test
  @DisplayName("Проверка некорректного типа поля для аннотации")
  void testIncorrectFieldTypeForAnnotation() {
    class IncorrectTypeTestClass {

      @Size(min = 1, max = 10, message = "Только для строк")
      private Integer notAStringField = 123; // ОШИБКА: Integer вместо String

      @Email(message = "Только для строк")
      private Integer notAnEmailField = 456; // ОШИБКА: Integer вместо String

      @Range(min = 1, max = 10, message = "Только для чисел")
      private String notANumberField = "text"; // ОШИБКА: String вместо числа

      public IncorrectTypeTestClass() {
      }
    }

    IncorrectTypeTestClass testObject = new IncorrectTypeTestClass();
    ValidationResult result = Validator.validate(testObject);
    assertFalse(result.isValid());
    assertTrue(result.getErrors().stream()
        .anyMatch(e -> e.contains("must be a String for @Size validation")));
    assertTrue(result.getErrors().stream()
        .anyMatch(e -> e.contains("must be a String for @Email validation")));
    assertTrue(result.getErrors().stream()
        .anyMatch(e -> e.contains("must be a number for @Range validation")));
  }
}