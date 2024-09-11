package com.hurontg.tiger.domain;

public class Child {
  private String name;
  private String grade;
  private String section;
  private int campus;

  public Child(String name, String grade, String section) {
    this.name = name;
    this.grade = grade;
    this.section = section;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGrade() {
    return grade;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }

  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }

  public int getCampus() { return campus; }

  public void setCampus(int campus) {
    this.campus = campus;
  }

  public boolean isCampusMatch(int campus) { return this.campus == campus; }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(name);
    sb.append(" (")
        .append(grade)
        .append(section)
        .append(")");
    return sb.toString();
  }
}
