package genericclasses;

import java.io.*;
import java.util.*;

public class CollectionUtils {

  public static <T> List<T> mergeLists(List<? extends T> list1, List<? extends T> list2) {
    List<T> list = new ArrayList<>();
    for (T x : list1) {
      list.add(x);
    }
    for (T x : list2) {
      list.add(x);
    }
    return list;
  }

  public static <T> void addAll(List<? super T> destination, List<? extends T> source) {
    for (T x : source) {
      destination.add(x);
    }
  }
}
