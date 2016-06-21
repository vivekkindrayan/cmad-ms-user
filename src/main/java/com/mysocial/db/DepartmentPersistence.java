package com.mysocial.db;

import static com.mysocial.util.Constants.COLLECTION_NAME_DEPARTMENT;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mysocial.beans.Department;
import com.mysocial.util.MySocialUtil;

public class DepartmentPersistence {

	private static final MongoDatabase db = MySocialUtil.getMongoDB();
	
	private static final String KEY_ID = "_id";
	private static final String KEY_DEPTNAME = "deptName";
	public static final String KEY_SITEID = "siteId";
	
	public static List<Department> getAllDepartments()
	{
		List<Department> depts = new ArrayList<Department>();
		FindIterable<Document> iterable = db.getCollection(COLLECTION_NAME_DEPARTMENT).find();
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		    	depts.add(deSerialize(document));
		    }
		});
		return depts;
	}
	
	public static Department getDepartmentById(String deptId)
	{
		List<Department> depts = new ArrayList<Department>();
		FindIterable<Document> iterable = db.getCollection(COLLECTION_NAME_DEPARTMENT).find(new Document(KEY_ID, new ObjectId(deptId)));
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		    	depts.add(deSerialize(document));
		    }
		});
		if (depts != null && depts.size() > 0) {
			return depts.get(0);
		} else {
			return null;
		}
	}
	
	public static List<Department> getDepartmentsForSite (String siteId)
	{
		List<Department> depts = new ArrayList<Department>();
		FindIterable<Document> iterable = db.getCollection(COLLECTION_NAME_DEPARTMENT).find(new Document(KEY_SITEID, siteId));
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		        depts.add(deSerialize(document));
		    }
		});
		return depts;
	}
	
	private static Department deSerialize(Document document)
	{
		ObjectId id = (ObjectId) document.get(KEY_ID);
        String name = (String) document.get(KEY_DEPTNAME);
        String siteId = (String) document.get(KEY_SITEID);
        Department d = new Department();
        d.setId(id.toHexString());
        d.setDeptName(name);
        d.setSiteId(new ObjectId(siteId));
        return d;
	}
}
