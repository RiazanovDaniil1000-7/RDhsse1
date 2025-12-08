package Collections;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.TreeMap;

class MapOperations {

  public static HashMap<Integer, Student> createHashMap() {
    return new HashMap<>();
  }
  public static TreeMap<Integer, Student> createTreeMap() {
    return new TreeMap<>(Collections.reverseOrder());
  }
  public static List<Student> findStudentsByGradeRange(Map<Integer, Student> map,
      double minGrade, double maxGrade) {
    return map.values().stream()
        .filter(student -> student.getGrade() >= minGrade && student.getGrade() <= maxGrade)
        .collect(Collectors.toList());
  }
  public static List<Student> getTopNStudents(TreeMap<Integer, Student> map, int n) {
    return map.entrySet().stream()
        .limit(n)
        .map(Map.Entry::getValue)
        .collect(Collectors.toList());
  }
}