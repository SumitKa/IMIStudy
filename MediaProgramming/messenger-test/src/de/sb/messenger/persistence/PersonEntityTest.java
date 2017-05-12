package de.sb.messenger.persistence;

import static org.junit.Assert.assertNotSame;

import org.junit.BeforeClass;
import org.junit.Test;

import de.sb.messenger.persistence.Person.Group;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

public class PersonEntityTest extends EntityTest {

	private static EntityManager em = null;
	
	@Test
	public void testConstrains()
	{
		Validator validator = this.getEntityValidatorFactory().getValidator();
		
		Person entity = new Person();

        Set<ConstraintViolation<Person>> constraintViolations = validator.validate(entity);
	}
	
	@Test
	public void testLifeCycle()
	{
		em = this.getFactory().createEntityManager();
		em.getTransaction().begin();
		
		Person entity = new Person();
		
		entity.setEmail("email");
		entity.setGroup(Group.USER);
		
		em.persist(entity);
		
		em.getTransaction().commit();

		final long id = entity.getIdentity();

		em = this.getFactory().createEntityManager();
		em.find(Person.class, id);
		
		this.getWasteBasket().add(entity.getIdentity());
		
		em.close();
	}
}
