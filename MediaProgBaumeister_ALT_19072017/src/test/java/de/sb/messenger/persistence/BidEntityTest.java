package de.sb.messenger.persistence;

import org.junit.Test;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

public class BidEntityTest extends EntityTest {

	private static EntityManager em = null;
	
	@Test
	public void testConstrains()
	{
		Validator validator = this.getEntityValidatorFactory().getValidator();

		Bid entity = new Bid();

		Set<ConstraintViolation<Person>> constraintViolations = validator.validate(entity);
	}
	
	@Test
	public void testLifeCycle()
	{
		em = this.getEntityManagerFactory().createEntityManager();
		em.getTransaction().begin();

		Bid entity = new Bid();

		entity.setGroup(Person.Group.USER);

		em.persist(entity);

		em.getTransaction().commit();

		this.getWasteBasket().add(entity.getIdentity());

		em.close();
	}

	private class Bid extends Person {
	}
}
