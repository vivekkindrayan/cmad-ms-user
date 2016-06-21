package com.mysocial.db;

import static com.mysocial.util.Constants.COLLECTION_NAME_COMPANY;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mysocial.beans.Company;
import com.mysocial.util.MySocialUtil;

public class CompanyPersistence {
	
	private static final MongoDatabase db = MySocialUtil.getMongoDB();
	
	private static final String KEY_ID = "_id";
	private static final String KEY_COMPANYNAME = "companyName";
	private static final String KEY_SUBDOMAIN = "subdomain";

	public static List<Company> getAllCompanies()
	{
		List<Company> companies = new ArrayList<Company>();
		FindIterable<Document> iterable = db.getCollection(COLLECTION_NAME_COMPANY).find();
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		    	companies.add(deSerialize(document));
		    }
		});
		return companies;
	}
	
	public static Company getCompanyById(String id)
	{
		List<Company> companies = new ArrayList<Company>();
		FindIterable<Document> iterable = db.getCollection(COLLECTION_NAME_COMPANY).find(new Document(KEY_ID, new ObjectId(id)));
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		    	companies.add(deSerialize(document));
		    }
		});
		if (companies != null && companies.size() > 0) {
			return companies.get(0);
		} else{
			return null;
		}
	}
	
	private static Company deSerialize(Document document)
	{
		ObjectId id = (ObjectId) document.get(KEY_ID);
        String name = (String) document.get(KEY_COMPANYNAME);
        String subdomain = (String) document.get(KEY_SUBDOMAIN);
        Company c = new Company();
        c.setId(id.toHexString());
        c.setCompanyName(name);
        c.setSubdomain(subdomain);
        return c;
	}
}
