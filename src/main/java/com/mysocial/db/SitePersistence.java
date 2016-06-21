package com.mysocial.db;

import static com.mysocial.util.Constants.COLLECTION_NAME_SITE;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mysocial.beans.Site;
import com.mysocial.util.MySocialUtil;

public class SitePersistence {

	private static final MongoDatabase db = MySocialUtil.getMongoDB();
	
	private static final String KEY_ID = "_id";
	private static final String KEY_SITENAME = "siteName";
	public static final String KEY_COMPANYID = "companyId";
	
	public static List<Site> getAllSites()
	{
		List<Site> sites = new ArrayList<Site>();
		FindIterable<Document> iterable = db.getCollection(COLLECTION_NAME_SITE).find();
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		    	sites.add(deSerialize(document));
		    }
		});
		return sites;
	}
	
	public static Site getSiteById(String siteId)
	{
		List<Site> sites = new ArrayList<Site>();
		FindIterable<Document> iterable = db.getCollection(COLLECTION_NAME_SITE).find(new Document(KEY_ID, new ObjectId(siteId)));
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		    	sites.add(deSerialize(document));
		    }
		});
		if (sites != null && sites.size() > 0) {
			return sites.get(0);
		} else {
			return null;
		}
	}
	
	public static List<Site> getSitesForCompany(String companyId)
	{
		List<Site> sites = new ArrayList<Site>();
		FindIterable<Document> iterable = db.getCollection(COLLECTION_NAME_SITE).find(new Document(KEY_COMPANYID, companyId));
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		    	sites.add(deSerialize(document));
		    }
		});
		return sites;
	}
	
	private static Site deSerialize(Document document)
	{
		ObjectId id = (ObjectId) document.get(KEY_ID);
        String name = (String) document.get(KEY_SITENAME);
        String companyId = (String) document.get(KEY_COMPANYID);
        Site s = new Site();
        s.setId(id.toHexString());
        s.setSiteName(name);
        s.setCompanyId(new ObjectId(companyId));
        return s;
	}
}
