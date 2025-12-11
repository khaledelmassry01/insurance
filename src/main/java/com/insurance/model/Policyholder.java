package com.insurance.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "policyholders")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Policyholder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policyholder_id")
    private Integer policyholderId;

    @NotNull
    @Column(name = "ssn", unique = true)
    private String ssn;

    @NotBlank
    @Size(max = 45)
    @Column(name = "first_name")
    private String firstName;

    @NotBlank
    @Size(max = 45)
    @Column(name = "last_name")
    private String lastName;

    @NotBlank
    @Size(max = 45)
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotBlank
    @Email
    @Size(max = 45)
    @Column(name = "email")
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(name = "password")
    private String password;

    @NotBlank
    @Size(max = 45)
    @Column(name = "gender")
    private String gender;

    @NotNull
    @Column(name = "birthdate")
    private LocalDate birthdate;

    @NotNull
    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @NotBlank
    @Size(max = 45)
    @Column(name = "state")
    private String state;

    @NotBlank
    @Size(max = 45)
    @Column(name = "city")
    private String city;

    @NotBlank
    @Size(max = 45)
    @Column(name = "street")
    private String street;

    @Column(name = "zip_code")
    private Integer zipCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependant_id")
    @JsonBackReference("dependantRef")
    private Policyholder dependant;

    @Size(max = 45)
    @Column(name = "dependant_relation")
    private String dependantRelation;

    @OneToMany(mappedBy = "policyholder", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference("insuranceCoveragesRef")
    private Set<InsuranceCoverage> insuranceCoverages = new HashSet<>();

    @OneToMany(mappedBy = "policyholder", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference("claimsRef")
    private Set<Claim> claims = new HashSet<>();

    @OneToMany(mappedBy = "policyholder", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference("insuranceHistoryRef")
    private Set<InsuranceHistory> insuranceHistory = new HashSet<>();

    // ======== Getters and Setters ========

    public Integer getPolicyholderId() {
        return policyholderId;
    }

    public void setPolicyholderId(Integer policyholderId) {
        this.policyholderId = policyholderId;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    public Policyholder getDependant() {
        return dependant;
    }

    public void setDependant(Policyholder dependant) {
        this.dependant = dependant;
    }

    public String getDependantRelation() {
        return dependantRelation;
    }

    public void setDependantRelation(String dependantRelation) {
        this.dependantRelation = dependantRelation;
    }

    public Set<InsuranceCoverage> getInsuranceCoverages() {
        return insuranceCoverages;
    }

    public void setInsuranceCoverages(Set<InsuranceCoverage> insuranceCoverages) {
        this.insuranceCoverages = insuranceCoverages;
    }

    public Set<Claim> getClaims() {
        return claims;
    }

    public void setClaims(Set<Claim> claims) {
        this.claims = claims;
    }

    public Set<InsuranceHistory> getInsuranceHistory() {
        return insuranceHistory;
    }

    public void setInsuranceHistory(Set<InsuranceHistory> insuranceHistory) {
        this.insuranceHistory = insuranceHistory;
    }
}
