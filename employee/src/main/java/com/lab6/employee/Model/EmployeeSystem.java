package com.lab6.employee.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class EmployeeSystem {
    @NotBlank(message = "id can not be empty")
    @Size(min = 3, message = "id must be longer than 2 characters")
    private String id;
    @NotBlank(message = "name can not be empty")
    @Size(min = 5, message = "Name must be longer than 4 characters")
    @Pattern(regexp = "^\\p{L}++$", message = "name must only have letters")
    private String name;
    @Email(message = "please enter a valid email")
    private String email;
    @Size(max = 10, min = 10, message = "phone number must be 10 digits")
    @Pattern(regexp = "^05[0-9]+$", message = "phone must only have numbers")
    private String phone;
    @NotNull(message = "age can not be empty")
    @Positive(message = "age must be a positive number")
    @Min(value = 26, message = "age must be larger than 25")
    @Max(value = 120, message = "age must be less than 120")
    private int age;
    @Pattern(message = "must equal to 'supervisor' or 'coordinator' only",
            regexp = "^(?i)(supervisor|coordinator)$")
    @NotBlank(message = "position can not be empty")
    private String position;
    @AssertFalse(message = "must be set to false initially")
    private boolean onLeave;
    @NotNull(message = "data can not be null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "hire date must be in the past or present")
    private LocalDate hireDate;
    @NotNull(message = "annual leave can not be empty")
    @PositiveOrZero(message = "annual leave must be positive")
    private int annualLeave;


}
