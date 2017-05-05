package de.sb.messenger.persistence;
import static org.junit.Assert.assertNotSame;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;

public class DocumentEntityTest extends EntityTest {

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
		
		Document entity = new Document();
		
		entity.setContent(new byte[0]);
		entity.setContentHash(new byte[0]);
		entity.setContentType("contentType");
		
		em.persist(entity);
		em.flush();
		
		Document d2 = new Document();
		
		d2.setContent(new byte[0]);
		d2.setContentHash(new byte[0]);
		d2.setContentType("contentType1");;
		
		em.persist(d2);
		em.flush();
		
		//em.find(Adress.class, )
		
		assertNotSame(entity, d2);
		
		em.remove(entity);
		em.remove(d2);
		
		em.getTransaction().commit();
		
		this.getWasteBasket().add(entity.getIdentity());
		
		em.close();
	}
}