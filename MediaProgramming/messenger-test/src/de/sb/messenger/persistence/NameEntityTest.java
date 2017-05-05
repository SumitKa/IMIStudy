package de.sb.messenger.persistence;
import static org.junit.Assert.assertNotSame;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;

public class NameEntityTest extends EntityTest {

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
		
		Name entity = new Name();
		
		entity.setFamily("family");
		entity.setGiven("given");
		
		em.persist(entity);
		em.flush();
		
		Name n2 = new Name();
		
		n2.setFamily("family1");
		n2.setGiven("given1");
		
		em.persist(n2);
		em.flush();
		
		//em.find(Adress.class, )
		
		assertNotSame(entity, n2);
		
		em.remove(entity);
		em.remove(n2);
		
		em.getTransaction().commit();
		
		this.getWasteBasket().add(entity.getIdentity());
		
		em.close();
	}
}