package com.gihow.entity;


import com.gihow.DefaultPersistence;

import javax.persistence.*;

@Entity()
@Table(name = "descriptor")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Descriptor extends DefaultPersistence {
	private String name;
	private String action = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "action")
	public String getActionName() {
		return action;
	}

	public void setActionName(String action) {
		this.action = action;
	}

}
