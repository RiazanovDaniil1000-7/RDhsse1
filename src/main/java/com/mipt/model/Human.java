package model;

public class Human {

  private String Family_name;
  private String Name;
  private int Age;
  private boolean Working;

  public String getFamily_name() {
    return Family_name;
  }

  public String getName() {
    return Name;
  }

  public int getAge() {
    return Age;
  }

  public boolean isWorking() {
    return Working;
  }

  public void setFamily_name(String Family_name) {
    this.Family_name = Family_name;
  }

  public void setName(String Name) {
    this.Name = Name;
  }

  public void setAge(int Age) {
    this.Age = Age;
  }

  public void setWorking(boolean Working) {
    this.Working = Working;
  }
}
