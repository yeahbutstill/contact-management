package com.yeahbutstill.restful.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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
@Table(name = "users")
public class User {

    @Id
    private String username;

    @NotBlank
    @NotEmpty
    @Size(max = 100)
    private String password;

    @NotBlank
    @NotEmpty
    @Size(max = 100)
    private String name;

    @Column(unique = true)
    @Size(max = 100)
    private String token;

    @Column(name = "token_expired_at")
    private Long tokenExpiredAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Contact> contacts;

}
