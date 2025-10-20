package Collections;

import java.util.Objects;

public class Student {

  private final int id;
  private final String name;
  private final double grade;

  public Student(int id, String name, double grade) {
    this.id = id;
    this.name = name;
    this.grade = grade;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public double getGrade() {
    return grade;
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Student st = (Student) o;
    return id == st.id &&
        Double.compare(st.grade, grade) == 0 &&
        Objects.equals(name, st.name);
  }

  public int hashCode() {
    return Objects.hash(id, name, grade);
  }

  public String toString() {
    return "Student{id=" + id + ", name='" + name + "', grade=" + grade + '}';
  }
}
