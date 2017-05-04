package de.sb.messenger.persistence;
import static org.junit.Assert.assertNotSame;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;

public class NameTest {
	private static EntityManager em = null;

	@BeforeClass
    public static void setUpClass() throws Exception {
        if (em == null) {
            em = (EntityManager) Persistence.createEntityManagerFactory("testDocument").createEntityManager();
        }
    }
	
	@Test
	public void checkDocument() {
		em.getTransaction().begin();
		
		Name n1 = new Name();
		
		n1.setFamily("family");
		n1.setGiven("given");
		
		em.persist(n1);
		em.flush();
		
		Name n2 = new Name();
		
		n2.setFamily("family1");
		n2.setGiven("given1");
		
		em.persist(n2);
		em.flush();
		
		//em.find(Adress.class, )
		
		assertNotSame(n1, n2);
		
		em.remove(n1);
		em.remove(n2);
		
		em.getTransaction().commit();
	}
}