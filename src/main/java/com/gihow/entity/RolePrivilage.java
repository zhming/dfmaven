package com.gihow.entity;

import com.documentum.fc.client.IDfACL;
import com.gihow.DefaultPersistence;

import javax.persistence.*;

@Entity()
@Table(name="role_privilage")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
public class RolePrivilage extends DefaultPersistence {
	
	private IDfACL role;
	private ModuleFunction moduleFunction;

	@ManyToOne
	public IDfACL getRole() {
		return role;
	}

	public void setRole(IDfACL role) {
		this.role = role;
	}

	@ManyToOne
	@JoinColumn(name="module_function_id")
	public ModuleFunction getModuleFunction() {
		return moduleFunction;
	}

	public void setModuleFunction(ModuleFunction moduleFunction) {
		this.moduleFunction = moduleFunction;
	}
}
