package foo;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import com.google.appengine.api.datastore.Entity;

@WebServlet(name = "PetServlet", urlPatterns = { "/petition" })
public class PetitionServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		Random r = new Random();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		// Create petition
		for (int i = 0; i < 500; i++) {
			Entity e = new Entity("Petition", "P" + i);
			int owner= r.nextInt(1000);
			e.setProperty("Owner", "U" + owner);
			e.setProperty("Date", new Date());
			e.setProperty("Body", "bla bla");
			
			
			// Create random votants
			HashSet<String> fset = new HashSet<String>();
			//while (fset.size() < 200) {
			for(int j=0; j<200; j++) {
				fset.add("U" + r.nextInt(1000));
			}
			e.setProperty("Votants", fset);
			e.setProperty("nbVotants", fset.size());

			datastore.put(e);
			response.getWriter().print("<li> created post:" + e.getKey() + "<br>");
			
		}
	}
}