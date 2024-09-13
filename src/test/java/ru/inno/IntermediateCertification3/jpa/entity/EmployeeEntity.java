package ru.inno.IntermediateCertification3.jpa.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "employee", schema = "public", catalog = "x_clients_fxd0")
public class EmployeeEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "middle_name")
    private String middleName;
    @Column
    private String phone;
    @Column
    private String email;
    @Column
    private java.sql.Date birthdate;
    @Column(name = "is_active")
    private boolean isActive;
    @Column(name = "company_id")
    private int companyId;

    public EmployeeEntity(String firstName, String lastName, String middleName, String phone, String email, boolean isActive, int companyId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.phone = phone;
        this.email = email;
        this.isActive = isActive;
        this.companyId = companyId;
    }

    public EmployeeEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(java.sql.Date birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeEntity that = (EmployeeEntity) o;
        return id == that.id && isActive == that.isActive && companyId == that.companyId && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(middleName, that.middleName) && Objects.equals(phone, that.phone) && Objects.equals(email, that.email) && Objects.equals(birthdate, that.birthdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, middleName, phone, email, birthdate, isActive, companyId);
    }

    @Override
    public String toString() {
        return "EmployeeEntity{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", birthdate=" + birthdate +
                ", isActive=" + isActive +
                ", companyId=" + companyId +
                '}';
    }
}
