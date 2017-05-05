package de.sb.messenger.persistence;
import static org.junit.Assert.assertNotSame;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;

public class MessageEntityTest  extends EntityTest {

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
		
		Message entity = new Message();
		
		entity.setBody("body");
		
		em.persist(entity);
		em.flush();
		
		Message m2 = new Message();
		
		m2.setBody("body1");
		
		em.persist(m2);
		em.flush();
		
		//em.find(Adress.class, )
		
		assertNotSame(entity, m2);
		
		em.remove(entity);
		em.remove(m2);
		
		em.getTransaction().commit();
		
		this.getWasteBasket().add(entity.getIdentity());
		
		em.close();
	}
}