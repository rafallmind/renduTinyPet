package foo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.text.DateFormat;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

@Api(name = "myApi",
     version = "v1"
)
public class PetitionEndpoint {
	
	@ApiMethod(name = "getTop100", path="petition/signed/top100", httpMethod = ApiMethod.HttpMethod.GET)
	public List<Entity> getTop100() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Query q = new Query("Petition");
		q.addProjection(new PropertyProjection("nbSignataire", Integer.class));
		q.addSort("nbSignataire", SortDirection.DESCENDING);
		
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> petitions = pq.asList(FetchOptions.Builder.withLimit(100));
		
		return petitions;
		

	}
	
	@ApiMethod(name = "getTop10Recent", path="petition/news/top10/{last}", httpMethod = ApiMethod.HttpMethod.GET)
	public List<Entity> getTop10Recent(@Named("last") String last) throws EntityNotFoundException {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Petition");
		//q.addProjection(new PropertyProjection("nbSignataire", Integer.class));
		
		
		
		if (!last.equals("0")) {
			
			Key petitionKey = new Entity("Petition", last).getKey();
			//Entity e = datastore.get(petitionKey);
			q.setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, petitionKey)); 
					
		}
		
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> petitions = pq.asList(FetchOptions.Builder.withLimit(10));
		
		return petitions;
	
	}
	@ApiMethod(name = "getTop1OSigned", path="petition/signed/top10/{last}", httpMethod = ApiMethod.HttpMethod.GET)
	public List<Entity> getTop1OSigned(@Named("last") String last) throws EntityNotFoundException {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Petition");
		//q.addProjection(new PropertyProjection("nbSignataire", Integer.class));
		q.addSort("nbSignataire", SortDirection.DESCENDING);
		
		
		if (!last.equals("0")) {
			
			Key petitionKey = new Entity("Petition", last).getKey();
			//Entity e = datastore.get(petitionKey);
			q.setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, petitionKey)); 
					
		}
		
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> petitions = pq.asList(FetchOptions.Builder.withLimit(10));
		
		return petitions;
	
	}
	@ApiMethod(name = "addObjectifSignataire", path="petition/objectif-signataire/{petitionID}/{objectif}", httpMethod = ApiMethod.HttpMethod.GET)
	public Entity addObjectifSignataire(@Named("petitionID") String petitionID, @Named("objectif") int objectif) throws EntityNotFoundException {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key petitionKey = new Entity("Petition", petitionID).getKey();
		Entity e = datastore.get(petitionKey);
		e.setProperty("objectifSignataire", objectif);
		datastore.put(e);

		
		return e;
	}
	@ApiMethod(name = "infoPetition", path="petition/info/{petitionID}", httpMethod = ApiMethod.HttpMethod.GET)
	public Entity infoPetition(@Named("petitionID") String petitionID) throws EntityNotFoundException {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key petitionKey = new Entity("Petition", petitionID).getKey();
		Entity e = datastore.get(petitionKey);
		return e;
	}
	
	@ApiMethod(name = "addPetition", path="petition/add", httpMethod = ApiMethod.HttpMethod.POST)
	public Entity addPetition(PetitionItem pi) {
		Random r = new Random();
		int k = r.nextInt(50000);
		Date date_creation = new Date();
		DateFormat mediumDateFormat = DateFormat.getDateTimeInstance(
		        DateFormat.MEDIUM,
		        DateFormat.MEDIUM);
		
		Entity e = new Entity("Petition", Long.MAX_VALUE-(date_creation).getTime()+":"+pi.proprietaire+":"+k);
		e.setProperty("titre", pi.titre);
		e.setProperty("description", pi.description);
		
		e.setProperty("date", mediumDateFormat.format(date_creation));
		e.setProperty("update_at",mediumDateFormat.format(date_creation));
		e.setProperty("proprietaire", pi.proprietaire);
		e.setProperty("proprietaireName", pi.proprietaireName);
		e.setProperty("nbSignataire", pi.nbSignataire);
		e.setProperty("objectifSignataire", pi.objectifSignataire);
		e.setProperty("img_url", pi.img_url);
				
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(e);
		return e;
	}
	@ApiMethod(name = "updatePetition", path="petition/update", httpMethod = ApiMethod.HttpMethod.POST)
	public Entity updatePetition(PetitionItem pi) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key petitionKey = new Entity("Petition", pi.ID).getKey();
		Entity e = datastore.get(petitionKey);
		DateFormat mediumDateFormat = DateFormat.getDateTimeInstance(
		        DateFormat.MEDIUM,
		        DateFormat.MEDIUM);
		Date date_update = new Date();
		e.setProperty("titre", pi.titre);
		e.setProperty("description", pi.description);
		e.setProperty("update_at", mediumDateFormat.format(date_update));
		e.setProperty("objectifSignataire", pi.objectifSignataire);
		e.setProperty("img_url", pi.img_url);
		
		//Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		//txn.commit();
		return e;
	}
	
    @ApiMethod(name = "deletePetition", path="petition/delete/{petitionID}", httpMethod = ApiMethod.HttpMethod.GET)
	public Entity deletePetition(@Named("petitionID") String petitionID) throws EntityNotFoundException {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key petitionKey = new Entity("Petition", petitionID).getKey();
		Entity e = datastore.get(petitionKey);
		datastore.delete(e.getKey());
		
		Query q = new Query("Signature");
		q.setFilter(new FilterPredicate("petition", FilterOperator.EQUAL, petitionID)); 
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
		Entity response = new Entity("response");
		int nbSignature = result.size();
		if (nbSignature>0) {
			for (Entity entity : result) {
				datastore.delete(entity.getKey());
			}
		} 
		response.setProperty("type", "OK");
		response.setProperty("Message", "Petition supprimé avec succès");
		response.setProperty("SignatureSupprimer", nbSignature);
		
		return response;
		
		
		
	
	}
	
    @ApiMethod(name = "addSignataire", path="signature/add/{petitionID}/{userID}", httpMethod = ApiMethod.HttpMethod.GET)
	public Entity addSignataire(@Named("petitionID") String petitionID, @Named("userID") int userID) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Signature");
		q.setFilter(CompositeFilterOperator.and(
				new FilterPredicate("proprietaire", FilterOperator.EQUAL, userID),
				new FilterPredicate("petition", FilterOperator.EQUAL, petitionID) 
				)); 
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
		Entity response = new Entity("response");
		if (result.size()>0) {
			response.setProperty("type", "ERREUR");
			response.setProperty("message", "Signature dejà existante");
		} else {
			Date date= new Date();
			Entity s = new Entity("Signature", userID+":"+date+":"+petitionID);
			s.setProperty("petition",  petitionID);
			s.setProperty("proprietaire",  userID);
			s.setProperty("date",  date);	
			datastore.put(s);
			
			Key petitionKey = new Entity("Petition", petitionID).getKey();
			Entity e = datastore.get(petitionKey);
			long nbSignataire = (long) e.getProperty("nbSignataire");
			nbSignataire++;
			e.setProperty("nbSignataire", nbSignataire);
			datastore.put(e);
			
			response.setProperty("type", "OK");
			response.setProperty("message", "Signature ajouté avec success");
			response.setProperty("nbSignataire", nbSignataire);
		}
		return response;
	}
	
	@ApiMethod(name = "verifieSignature", path="signature/verifie/{petitionID}/{userID}", httpMethod = ApiMethod.HttpMethod.GET)
	public Entity verifieSignature(@Named("petitionID") String petitionID, @Named("userID") int userID) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Signature");
		q.setFilter(CompositeFilterOperator.and(
				new FilterPredicate("proprietaire", FilterOperator.EQUAL, userID),
				new FilterPredicate("petition", FilterOperator.EQUAL, petitionID) 
				)); 
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
		Entity response = new Entity("response");
		if (result.size()>0) {
			response.setProperty("signed", true);
			response.setProperty("type", "ATTENTION");
			response.setProperty("message", "Signature dejà existante");
		} else {
			response.setProperty("signed", false);
			response.setProperty("type", "Ok");
			response.setProperty("message", "Signature non existante");
		}
		return response;
	}

	@ApiMethod(name = "deleteSignataire", path="signature/delete/{petitionID}/{userID}", httpMethod = ApiMethod.HttpMethod.GET)
	public Entity deleteSignataire(@Named("petitionID") String petitionID, @Named("userID") int userID) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Signature");
		q.setFilter(CompositeFilterOperator.and(
				new FilterPredicate("proprietaire", FilterOperator.EQUAL, userID),
				new FilterPredicate("petition", FilterOperator.EQUAL, petitionID) 
				)); 
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
		Entity response = new Entity("response");
		if (result.size()>0) {
			for (Entity entity : result) {
				datastore.delete(entity.getKey());
			}

			Key petitionKey = new Entity("Petition", petitionID).getKey();
			Entity e = datastore.get(petitionKey);
			long nbSignataire = (long) e.getProperty("nbSignataire");
			nbSignataire--;
			e.setProperty("nbSignataire", nbSignataire);
			datastore.put(e);
			
			response.setProperty("type", "OK");
			response.setProperty("message", "Signature supprimé avec success");
			response.setProperty("nbSignataire", nbSignataire);
			
		} else {
			response.setProperty("type", "ERREUR");
			response.setProperty("message", "Signature inexistante");
		}
		return response;
	}
	
	@ApiMethod(name = "myPetitionCreated", path="petition/created/{userID}/{last}", httpMethod = ApiMethod.HttpMethod.GET)
	public List<Entity> myPetitionCreated(@Named("userID") String userID, @Named("last") String last) throws EntityNotFoundException {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Petition");	
		
		if (last.equals("0")) {
			q.setFilter(new FilterPredicate("proprietaire", FilterOperator.EQUAL, userID));
		} else {
			
			Key petitionKey = new Entity("Petition", last).getKey();
			q.setFilter(CompositeFilterOperator.and(
					new FilterPredicate("proprietaire", FilterOperator.EQUAL, userID),
					new FilterPredicate("__key__", FilterOperator.GREATER_THAN, petitionKey) 
					)); 
					
		}
		
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> petitions = pq.asList(FetchOptions.Builder.withLimit(10));
		
		return petitions;
	
	}
	
	@ApiMethod(name = "myPetitionSigned", path="petition/signed/{userID}/{last}", httpMethod = ApiMethod.HttpMethod.GET)
	public List<Entity> myPetitionSigned(@Named("userID") String userID, @Named("last") String last) throws Exception {





		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Signature");
		q.setFilter(new FilterPredicate("proprietaire", FilterOperator.EQUAL, userID)); 
		if (!last.equals("0")) {
			
			Query q2 = new Query("Signature");
			q2.setFilter(CompositeFilterOperator.and(
					new FilterPredicate("proprietaire", FilterOperator.EQUAL, userID),
					new FilterPredicate("petition", FilterOperator.EQUAL, last) 
					)); 
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
			Entity e=null;
			for (Entity entity : result) {
				e = entity;
			}
			
			
			q.setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, e.getKey()));
					
		}
				
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));		
		
	    List<Key> keys = new ArrayList<>();
	    String petitionID;
	    for(Entity e : result) {
	    	petitionID = (String) e.getProperty("petition");
	    	Key petitionKey = new Entity("Petition", petitionID).getKey();
	    	
	    	keys.add(petitionKey);
	    }
	    if(keys.size() > 0) {
		    q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.IN, keys));
		    
			if (!last.equals("0")) {
				
				Key petitionKey = new Entity("Petition", last).getKey();
				q.setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, petitionKey)); 
						
			}
			
			 pq = datastore.prepare(q);
			List<Entity> petitions = pq.asList(FetchOptions.Builder.withLimit(10));
			
			return petitions;
	    }
	    else {
	    	throw new Exception("Aucune petition n'a été signé");
	    }

	}	
	
	@ApiMethod(name = "listSignature", path="petition/list_signed/{userID}/{last}", httpMethod = ApiMethod.HttpMethod.GET)
	public List<Entity> listSignature(@Named("userID") String userID, @Named("last") String last) throws Exception {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Signature");
		q.setFilter(new FilterPredicate("proprietaire", FilterOperator.EQUAL, userID)); 
		if (!last.equals("0")) {
			
			Query q2 = new Query("Signature");
			q2.setFilter(CompositeFilterOperator.and(
					new FilterPredicate("proprietaire", FilterOperator.EQUAL, userID),
					new FilterPredicate("petition", FilterOperator.EQUAL, last) 
					)); 
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
			Entity e=null;
			for (Entity entity : result) {
				e = entity;
			}
			
			
			q.setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, e.getKey()));
					
		}
		
		
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
		
		
		return result;

	}
	
	
	@ApiMethod(name = "searchPetitionByTitre", path="search/titre/{titre}/{last}", httpMethod = ApiMethod.HttpMethod.GET)
	public List<Entity> searchPetitionByTitre(@Named("titre") String titre, @Named("last") String last) throws EntityNotFoundException {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Petition");
		//q.addProjection(new PropertyProjection("nbSignataire", Integer.class));
		
		
		
		if (last.equals("0")) {
			q.setFilter(new FilterPredicate("titre", FilterOperator.GREATER_THAN_OR_EQUAL, titre));
		} else {
			
			Key petitionKey = new Entity("Petition", last).getKey();
			//Entity e = datastore.get(petitionKey);
			q.setFilter(CompositeFilterOperator.and(
					new FilterPredicate("titre", FilterOperator.GREATER_THAN_OR_EQUAL, titre),
					new FilterPredicate("__key__", FilterOperator.GREATER_THAN, petitionKey) 
					)); 
					
		}
		
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> petitions = pq.asList(FetchOptions.Builder.withLimit(10));
		
		return petitions;
	
	}	
	
}