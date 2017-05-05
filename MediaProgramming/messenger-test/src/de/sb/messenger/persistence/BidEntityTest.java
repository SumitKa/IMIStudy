package de.sb.messenger.persistence;

import org.junit.Test;

public class BidEntityTest extends EntityTest {

	
	
	@Test
	public void testConstrains()
	{
		 validator = this.getEntityValidatorFactory().getValidator();
	
		 entity = new Bid();
		 
		 constraintViolations = validator.validate(entity);
	}
	
	@Test
	public testLifeCycle()
	{
		
	}
}
