package ReflectionAnnotations;

import java.util.List;

public class ValidationResult {

  private boolean isValid;
  private List<String> errors;

  public ValidationResult(boolean isValid, List<String> errors) {
    this.isValid = isValid;
    this.errors = errors;
  }

  public boolean getValid() {
    return isValid;
  }

  public List<String> getErrors() {
    return errors;
  }

  public void setValid(boolean isValid) {
    this.isValid = isValid;
  }

  public void setErrors(List<String> errors) {
    this.errors = errors;
  }
}