package com.yeahbutstill.restful.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    private String id;

    @Column(name = "first_name")
    @Size(max = 100)
    @NotEmpty
    @NotBlank
    private String firstName;

    @Column(name = "last_name")
    @Size(max = 100)
    private String lastName;

    @Size(max = 100)
    private String phone;

    @Email
    @Size(max = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @OneToMany(mappedBy = "contact", fetch = FetchType.EAGER)
    private List<Address> addresses;

}
