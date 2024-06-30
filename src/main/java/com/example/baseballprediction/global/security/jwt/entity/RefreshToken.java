package com.example.baseballprediction.global.security.jwt.entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "refresh_token_id")
	private String id;

	private String token;

	@ColumnDefault("false")
	private boolean invalid;

	public RefreshToken(String token) {
		this.token = token;
	}

	public void invalidToken() {
		this.invalid = true;
	}
}
