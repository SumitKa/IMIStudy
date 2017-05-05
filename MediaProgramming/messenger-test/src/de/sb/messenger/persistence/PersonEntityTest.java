package de.sb.messenger.persistence;

import static org.junit.Assert.assertNotSame;

import org.junit.BeforeClass;
import org.junit.Test;

import de.sb.messenger.persistence.Person.Group;

public class PersonEntityTest extends EntityTest {

	private static EntityManager em = null;
	
	@Test
	public void testConstrains()
	{
		
	}
	
	@Test
	public void testLifeCycle()
	{
		em = this.getEntityValidatorFactory().createEntityManager();
		em.getTransition().begin();
		
		Person p1 = new Person();
		
		p1.setEmail("email");
		p1.setGroup(Group.USER);
		
		em.persist(p1);
		em.flush();
		
		Person p2 = new Person();
		
		p2.setEmail("email1");
		p2.setGroup(Group.ADMIN);
		
		em.persist(p2);
		//em.flush();
		
		//em.find(Adress.class, )
		
		assertNotSame(p1, p2);
		
		em.getTransaction().commit();
		
		this.getWasteBasket().add(p1.getIdentity());
		
		em.close();
	}
}
