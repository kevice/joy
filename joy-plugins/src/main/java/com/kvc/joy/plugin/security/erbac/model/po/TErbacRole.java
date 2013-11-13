package com.kvc.joy.plugin.security.erbac.model.po;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.kvc.joy.commons.support.IListToTreeRestrict;
import com.kvc.joy.core.persistence.entity.UuidCrudEntity;
import com.kvc.joy.core.persistence.orm.jpa.annotations.Comment;

@Entity
@Table(name = "t_erbac_role")
@Comment("角色")
public class TErbacRole extends UuidCrudEntity implements IListToTreeRestrict<String> {

	private String parentId; // 父角色id
	private String name; // 角色名
	private Collection<TErbacUser> users; // 用户
	private Collection<TErbacGroup> groups; // 组
	// private Collection<TErbacAuthority> authorities; // 权限
	private Collection<TErbacRoleAuth> roleAuths; // 角色权限关系

	@Column(name = "ROLE_NAME", length = 64, nullable = false)
	@Comment("名称")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "t_erbac_role_user", // 关联表名
	inverseJoinColumns = @JoinColumn(name = "USER_ID"),// 被维护端外键
	joinColumns = @JoinColumn(name = "ROLE_ID"))
	// 维护端外键
	public Collection<TErbacUser> getUsers() {
		return users;
	}

	public void setUsers(Collection<TErbacUser> tErbacUsers) {
		this.users = tErbacUsers;
	}

	// @ManyToMany(cascade = CascadeType.REFRESH)
	// @JoinTable(name = "t_erbac_role_auth", // 关联表名
	// inverseJoinColumns = @JoinColumn(name = "AUTHORITY_ID"), // 被维护端外键
	// joinColumns = @JoinColumn(name = "ROLE_ID")) // 维护端外键
	// public Collection<TErbacAuthority> getAuthorities() {
	// return authorities;
	// }
	//
	// public void setAuthorities(Collection<TErbacAuthority> tErbacAuthorities)
	// {
	// this.authorities = tErbacAuthorities;
	// }

	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "t_erbac_role_group", // 关联表名
	inverseJoinColumns = @JoinColumn(name = "GROUP_ID"), // 被维护端外键
	joinColumns = @JoinColumn(name = "ROLE_ID"))
	// 维护端外键
	public Collection<TErbacGroup> getGroups() {
		return groups;
	}

	public void setGroups(Collection<TErbacGroup> groups) {
		this.groups = groups;
	}

	@Column(length = 32)
	@Comment("父项ID")
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@OneToMany(mappedBy = "role", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	public Collection<TErbacRoleAuth> getRoleAuths() {
		return roleAuths;
	}

	public void setRoleAuths(Collection<TErbacRoleAuth> roleAuths) {
		this.roleAuths = roleAuths;
	}

}
