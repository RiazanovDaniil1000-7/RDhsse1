package ReflectionAnnotations;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
  private boolean isValid;
  private List<String> errors;

  public ValidationResult() {
    this.isValid = true;
    this.errors = new ArrayList<>();
  }

  public boolean isValid() {
    return isValid;
  }

  public List<String> getErrors() {
    return new ArrayList<>(errors);
  }

  public void addError(String error) {
    this.errors.add(error);
    this.isValid = false;
  }

  public void setValid(boolean isValid) {
    this.isValid = isValid;
  }

  public void setErrors(List<String> errors) {
    this.errors = errors;
    this.isValid = errors.isEmpty();
  }

  @Override
  public String toString() {
    if (isValid) {
      return "ValidationResult{isValid=true}";
    } else {
      return "ValidationResult{isValid=false, errors=" + errors + "}";
    }
  }
}