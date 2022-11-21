package biz.global77.clinic.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String fullName; //

	private String icNumber; //

	private String address; //

	private String contactNumber; //

	private String email;

	private String password;

	private String role;

	private String registrationTime;

}