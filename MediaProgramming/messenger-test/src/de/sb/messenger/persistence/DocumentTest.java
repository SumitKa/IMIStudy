package de.sb.messenger.persistence;
import static org.junit.Assert.assertNotSame;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;

public class DocumentTest {
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
		
		Document d1 = new Document();
		
		d1.setContent(new byte[0]);
		d1.setContentHash(new byte[0]);
		d1.setContentType("contentType");
		
		em.persist(d1);
		em.flush();
		
		Document d2 = new Document();
		
		d2.setContent(new byte[0]);
		d2.setContentHash(new byte[0]);
		d2.setContentType("contentType1");;
		
		em.persist(d2);
		em.flush();
		
		//em.find(Adress.class, )
		
		assertNotSame(d1, d2);
		
		em.remove(d1);
		em.remove(d2);
		
		em.getTransaction().commit();
	}
}