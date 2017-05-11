package de.sb.messenger.persistence;

import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

public class BidEntityTest extends EntityTest {

	
	
	@Test
	public void testConstrains()
	{
		Validator validator = this.getEntityValidatorFactory().getValidator();

		Person entity = new Bid();

		Set<ConstraintViolation<Person>> constraintViolations = validator.validate(entity);
	}
	
	@Test
	public void testLifeCycle()
	{
		
	}

	private class Bid extends Person {
	}
}
