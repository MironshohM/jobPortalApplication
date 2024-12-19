package com.luv2code.jobportal.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "recruiter_profile")
public class RecruiterProfile {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userAccountId;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private Users userId;

    private String firstName;
    private String lastName;
    private String city;
    private String state;
    private String country;
    private String company;
    @Column(nullable = true,length = 64)
    private String profilePhoto;

    public RecruiterProfile(Users userId, String firstName, String lastName, String city, String state, String country, String company, String profilePhoto) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.company = company;
        this.profilePhoto = profilePhoto;
    }

    public RecruiterProfile(Users users) {
        this.userId=users;
    }

    @Transient
    public String getPhotosImagePath() {
        if (profilePhoto == null) return null;
        System.out.println("********************* " + "photos/recruiter/" + userAccountId + "/" + profilePhoto);
        return "/photos/recruiter/" + userAccountId + "/" + profilePhoto;
    }
}
