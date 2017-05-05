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
		validator = this.getEntityValidatorFactory().getValidator();
		
		Person entity = new Person();
		 
		constraintViolations = validator.validate(entity);
	}
	
	@Test
	public void testLifeCycle()
	{
		em = this.getEntityValidatorFactory().createEntityManager();
		em.getTransition().begin();
		
		Person entity = new Person();
		
		entity.setEmail("email");
		entity.setGroup(Group.USER);
		
		em.persist(entity);
		em.flush();
		
		Person entity2 = new Person();
		
		entity2.setEmail("email1");
		entity2.setGroup(Group.ADMIN);
		
		em.persist(entity2);
		//em.flush();
		
		//em.find(Adress.class, )
		
		assertNotSame(entity, entity2);
		
		em.getTransaction().commit();
		
		this.getWasteBasket().add(entity.getIdentity());
		
		em.close();
	}
}
