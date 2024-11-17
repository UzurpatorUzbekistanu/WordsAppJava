package com.bkleszcz.WordApp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Entity
@Data
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  @Pattern(regexp = "^[a-zA-Z]+$", message = "Pole login musi zawierać jedynie litery")
  private String name;

  @Column(nullable = false)
  @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}|\\[\\]:\";'<>?,./]).+$",
      message = "Hasło musi zawierać co najmniej jedną wielką literę, jedną cyfrę i jeden znak specjalny.")
  private String password;

}
