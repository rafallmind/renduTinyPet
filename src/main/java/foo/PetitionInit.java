package foo;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Random;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import com.google.appengine.api.datastore.Entity;

@WebServlet(name = "petInit", urlPatterns = { "/initPets" })
public class PetitionInit extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String id_user;
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		Random r = new Random();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		
		//for (int i = 0; i < 2; i++) {

            // Create users
			for (int j = 0; j < 10; j++) {
				String userId= "user_" +j;
				Entity e = new Entity("User", userId);
				e.setProperty("nom", "Mon nom est " + j);
				e.setProperty("prenom", "Mon prénom est " + j);
				e.setProperty("email", "Mon adresse mail est " + j);

				datastore.put(e);
				response.getWriter().print("<li> created user: " + e.getKey() + "<br>");
				
				// Create petition
				int nbMaxPetition = r.nextInt(5);
				for (int k = 0; k < nbMaxPetition; k++) {
					
					String dateValue = RandomDate.randDate();
					long date_formated = 0;
					try {
						date_formated = Long.MAX_VALUE-(new SimpleDateFormat("yyyy-MM-dd").parse(dateValue)).getTime();
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
					String petitionId=  date_formated + ":" + userId + ":" + k;
					Entity p = new Entity("Petition", petitionId);
					p.setProperty("titre", "Le titre est " + j);
					p.setProperty("description", "La description est " + j);
					p.setProperty("date", dateValue);
					p.setProperty("update_at", dateValue);
					p.setProperty("proprietaire",  userId);
					p.setProperty("proprietaireName",  userId);
                                    
				    String urlImage = "https://picsum.photos/id/"+r.nextInt(300)+"/367/267";
				    p.setProperty("img_url", urlImage);
										
					// Create signature
					int nbMaxSignataire = r.nextInt(30);
					int nbSignataire = 0;
					while (nbSignataire < nbMaxSignataire) {
						id_user = r.nextInt(50) + "" + r.nextInt(10);
						String s_date= RandomDate.randDate();
						try {
							date_formated = Long.MAX_VALUE-(new SimpleDateFormat("yyyy-MM-dd").parse(s_date)).getTime();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
						Entity s = new Entity("Signature", userId+":"+date_formated+":"+petitionId);
						s.setProperty("petition",  petitionId);
						s.setProperty("proprietaire",  id_user);
						s.setProperty("date",  s_date);	
						datastore.put(s);
						nbSignataire++;
						response.getWriter().print("<li> signature created: " + id_user + "<br>");
						
					}
										
                    p.setProperty("nbSignataire", nbSignataire);
					p.setProperty("objectifSignataire", r.nextInt(50)*10);

					
					datastore.put(p);
					response.getWriter().print("<li> created petition: " + p.getKey() + "<br>");
						
				}
			}
		//}
	}
}