package de.sb.messenger.persistence;
import static org.junit.Assert.assertNotSame;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;

public class MessageTest {
	private static EntityManager em = null;

	@BeforeClass
    public static void setUpClass() throws Exception {
        if (em == null) {
            em = (EntityManager) Persistence.createEntityManagerFactory("testMessage").createEntityManager();
        }
    }
	
	@Test
	public void checkMessage() {
		em.getTransaction().begin();
		
		Message m1 = new Message();
		
		m1.setBody("body");
		
		em.persist(m1);
		em.flush();
		
		Message m2 = new Message();
		
		m2.setBody("body1");
		
		em.persist(m2);
		em.flush();
		
		//em.find(Adress.class, )
		
		assertNotSame(m1, m2);
		
		em.remove(m1);
		em.remove(m2);
		
		em.getTransaction().commit();
	}
}