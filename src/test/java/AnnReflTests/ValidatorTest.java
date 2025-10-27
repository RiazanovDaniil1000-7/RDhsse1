package AnnReflTests;

import ReflectionAnnotations.Email;
import ReflectionAnnotations.Notnull;
import ReflectionAnnotations.Range;
import ReflectionAnnotations.Size;
import ReflectionAnnotations.ValidationResult;
import ReflectionAnnotations.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {


  static class ValidUser {

    @Notnull(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be {min}-{max} chars")
    private String username = "john_doe";

    @Notnull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email = "john@example.com";

    @Range(min = 18, max = 120, message = "Age must be {min}-{max}")
    private Integer age = 25;

    @Size(min = 8, message = "Password too short")
    private String password = "secure123";

    // Поле без аннотаций
    private String notes = "Some notes";
  }

  static class UserWithNulls {

    @Notnull(message = "Name required")
    private String name; // null - ошибка

    @Email(message = "Email invalid")
    private String email; // null - допустимо (нет @NotNull)

    @Range(min = 1, max = 100)
    private Integer count = 50;
  }

  static class UserWithSizeErrors {

    @Size(min = 5, max = 10, message = "Size error: {min}-{max}")
    private String shortString = "abc"; // слишком коротко

    @Size(min = 1, max = 3)
    private String longString = "very_long_string"; // слишком длинно

    @Size(min = 2, max = 5)
    private String validString = "abcd"; // валидно
  }

  static class UserWithRangeErrors {

    @Range(min = 10, max = 100, message = "Range error: {min}-{max}")
    private Integer smallNumber = 5; // меньше min

    @Range(min = 0, max = 50)
    private Integer largeNumber = 100; // больше max

    @Range(min = 1, max = 10)
    private Integer validNumber = 5; // валидно

    @Range(min = 0, max = 1000)
    private Double decimalNumber = 500.5; // валидно
  }

  static class UserWithEmailErrors {

    @Email(message = "Invalid email")
    private String validEmail = "test@example.com";

    @Email
    private String invalidEmail1 = "not-an-email";

    @Email
    private String invalidEmail2 = "user@";

    @Email
    private String invalidEmail3 = "@domain.com";

    @Email
    private String invalidEmail4 = "user@domain";

    @Email
    private String invalidEmail5 = "user@domain.";
  }

  static class UserWithMultipleAnnotations {

    @Notnull
    @Size(min = 1, max = 10)
    @Email
    private String email = "a@b.c";

    @Notnull
    @Size(min = 5)
    @Email
    private String shortEmail = "a@b.c";
  }

  static class UserWithWrongTypes {

    @Size(min = 1, max = 10)
    private Integer number = 123;

    @Range(min = 1, max = 10)
    private String text = "hello";

    @Email
    private Boolean flag = true;
  }

  static class BoundaryCases {

    @Size(min = 0, max = 0)
    private String emptyString = "";

    @Size(min = 5, max = 5)
    private String exactSize = "12345";

    @Range(min = 0, max = 0)
    private Integer zero = 0;

    @Range(min = -100, max = 100)
    private Integer negative = -50;

    @Range(min = 100, max = 100)
    private Integer exactValue = 100;
  }

  static class EmptyObject {
    private String fieldWithoutAnnotation = "value";
    private Integer numberWithoutAnnotation = 123;
  }
}
