package genericclasses;

public class Calculator<T extends Number> {

  public double sum(T a, T b) {
    if (a == null || b == null) {
      return 0;
    } else {
      return a.doubleValue() + b.doubleValue();
    }
  }

  public double subtract(T a, T b) {
    if (a == null || b == null) {
      return 0;
    } else {
      return a.doubleValue() - b.doubleValue();
    }
  }

  public double multiply(T a, T b) {
    if (a == null || b == null) {
      return 0;
    } else {
      return a.doubleValue() * b.doubleValue();
    }
  }

  public double divide(T a, T b) {
    if (a == null || b == null) {
      return 0;
    } else {
      if (b.doubleValue() == 0) {
        return Double.NaN;
      } else {
        return a.doubleValue() / b.doubleValue();
      }
    }
  }
}
