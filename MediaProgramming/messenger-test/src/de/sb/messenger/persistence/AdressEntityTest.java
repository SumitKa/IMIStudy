package de.sb.messenger.persistence;
import de.sb.messenger.persistence.*;
import org.junit.*;

import static org.junit.Assert.*;

import javax.persistence.*;

public class AdressEntityTest  extends EntityTest {
	
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
		
		Adress entity = new Adress();
		
		entity.setStreet("street");
		entity.setPostcode("postcode");
		entity.setCity("city");
		
		em.persist(entity);
		em.flush();
		
		Adress ad2 = new Adress();
		
		ad2.setStreet("street1");
		ad2.setPostcode("postcode1");
		ad2.setCity("city1");
		
		em.persist(ad2);
		em.flush();
		
		//em.find(Adress.class, )
		
		assertNotSame(entity, ad2);
		
		em.remove(entity);
		em.remove(ad2);
		
		em.getTransaction().commit();
		
		this.getWasteBasket().add(entity.getIdentity());
		
		em.close();
	}
}
