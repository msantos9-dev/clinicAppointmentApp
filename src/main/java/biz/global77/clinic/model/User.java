package biz.global77.clinic.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

// import biz.global77.clinic.config.passwordValidation.annotations.PasswordValueMatch;
// import biz.global77.clinic.config.passwordValidation.annotations.ValidPassword;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

// @PasswordValueMatch.List({
// 	@PasswordValueMatch(
// 			field = "password",
// 			fieldMatch = "confirmPassword",
// 			message = "Passwords do not match!"
// 	)
// })
@Data
@Entity
@EqualsAndHashCode
@ToString
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Size(min = 2, max = 30, message = "Name must have 4 to 30 characters")
	private String fullName; //

	@NotNull
	@NotBlank(message = "Address is mandatory")
	private String address; //

	@NotNull
	@NotBlank(message = "Contact number is mandatory")
	private String contactNumber;

	@NotNull
	@NotBlank(message = "Valid ID number is mandatory")
	private String validIdNumber;//

	@NotNull(message = "Email cannot be null")
	@NotEmpty(message = "Email cannot be empty")
	@Email(message = "Invalid email")
	private String email;

	private boolean isEnabled;

	private boolean isAccountNonLocked;
	// @ValidPassword

	@NotNull
	@Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$", message = "Password must be have minimum of 8 characters and must contain at least 1 uppercase, 1 lowercase, 1 special character and 1 digit.")
	private String password;

	// @ValidPassword
	@NotNull
	@Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$", message = "Password must be have minimum of 8 characters and must contain at least 1 uppercase, 1 lowercase, 1 special character and 1 digit.")
	private String confirmPassword;

	private String role;

	private String registrationTime;

}