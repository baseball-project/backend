package com.example.baseballprediction.domain.team.entity;

import java.util.Objects;

import com.example.baseballprediction.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Team extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "team_id")
	private int id;

	@Column(unique = true, nullable = false, length = 30)
	private String name;

	@Column(unique = true, nullable = false, length = 10)
	private String shortName;
	@Column(length = 16)
	private String color;
	
	@Builder
	public Team(String name, String shortName) {
		this.name = name;
		this.shortName = shortName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Team)) {
			return false;
		}
		Team team = (Team)o;
		return id == team.getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
