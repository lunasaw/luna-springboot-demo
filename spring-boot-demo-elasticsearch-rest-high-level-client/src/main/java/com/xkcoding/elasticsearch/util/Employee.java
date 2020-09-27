package com.xkcoding.elasticsearch.util;

/**
 * @Description:Book实体 加上了@Document注解之后，默认情况下这个实体中所有的属性都会被建立索引、并且分词
 */
public class Employee {
    private String id;
    private Long   version;
    String         firstName;
    String         lastName;
    String         age;
    String[]       interests;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String[] getInterests() {
        return interests;
    }

    public void setInterests(String[] interests) {
        this.interests = interests;
    }
}
