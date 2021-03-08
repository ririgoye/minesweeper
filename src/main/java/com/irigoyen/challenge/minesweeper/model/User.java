package com.irigoyen.challenge.minesweeper.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String image;
    @Column
    private Integer gamesPlayed;
    @Column
    private Integer gamesLost;
    @Column
    private Integer gamesWon;
    @JsonIgnore
    @Column
    private String password;

    public void encodePassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(5);
        this.password = encoder.encode(this.password);
    }

    public boolean validatePassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(5);
        return encoder.matches(password, this.password);

    }
}
