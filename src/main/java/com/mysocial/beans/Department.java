package com.mysocial.beans;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity("department")
@Indexes(
    @Index(value = "deptName", fields = @Field("deptName"))
)
public class Department {

	@Id
    private String id;
	private String deptName;
	private ObjectId siteId;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String name) {
		this.deptName = name;
	}

	public ObjectId getSiteId() {
		return siteId;
	}

	public void setSiteId(ObjectId siteId) {
		this.siteId = siteId;
	}
}
