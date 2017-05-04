package de.sb.messenger.persistence;
import de.sb.messenger.persistence.*;
import org.junit.*;

import static org.junit.Assert.*;

import javax.persistence.*;

public class AdressTest {
	
	private static EntityManager em = null;

	@BeforeClass
    public static void setUpClass() throws Exception {
        if (em == null) {
            em = (EntityManager) Persistence.createEntityManagerFactory("testAdress").createEntityManager();
        }
    }
	
	@Test
	public void checkAdress() {
		em.getTransaction().begin();
		
		Adress ad1 = new Adress();
		
		ad1.setStreet("street");
		ad1.setPostcode("postcode");
		ad1.setCity("city");
		
		em.persist(ad1);
		em.flush();
		
		Adress ad2 = new Adress();
		
		ad2.setStreet("street1");
		ad2.setPostcode("postcode1");
		ad2.setCity("city1");
		
		em.persist(ad2);
		em.flush();
		
		//em.find(Adress.class, )
		
		assertNotSame(ad1, ad2);
		
		em.remove(ad1);
		em.remove(ad2);
		
		em.getTransaction().commit();
	}
}
