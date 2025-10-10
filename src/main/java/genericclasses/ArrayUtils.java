package genericclasses;

public class ArrayUtils {

  public static <T> int findFirst(T[] array, T element) {
    if (element != null && array != null) {
      for (int i = 0; i < array.length; i++) {
        if (array[i].equals(element)) {
          return i;
        }
      }
    }
    return -1;
  }
}