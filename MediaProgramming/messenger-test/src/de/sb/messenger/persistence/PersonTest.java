package de.sb.messenger.persistence;
import static org.junit.Assert.assertNotSame;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;

import de.sb.messenger.persistence.Person.Group;

public class PersonTest {
	private static EntityManager em = null;

	@BeforeClass
    public static void setUpClass() throws Exception {
        if (em == null) {
            em = (EntityManager) Persistence.createEntityManagerFactory("testPerson").createEntityManager();
        }
    }
	
	@Test
	public void checkPerson() {
		em.getTransaction().begin();
		
		Person p1 = new Person();
		
		p1.setEmail("email");
		p1.setGroup(Group.USER);
		
		em.persist(p1);
		em.flush();
		
		Person p2 = new Person();
		
		p2.setEmail("email1");
		p2.setGroup(Group.ADMIN);
		
		em.persist(p2);
		em.flush();
		
		//em.find(Adress.class, )
		
		assertNotSame(p1, p2);
		
		em.remove(p1);
		em.remove(p2);
		
		em.getTransaction().commit();
	}
}