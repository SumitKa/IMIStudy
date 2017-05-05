package de.sb.messenger.persistence;

public class ValidatorFactory {

	public EntityManager createEntityManager() 
	{	
		return new EntityManager();
	}

	public Validator getValidator() 
	{	
		return new Validator();
	}

}
