package de.sb.messenger.persistence;
import org.junit.BeforeClass;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import javax.validation.*;
import java.util.*;
import java.util.List.*;

public class EntityTest {

	private List<Long> wasteBasket = new ArrayList<Long>() {

    };

    @BeforeClass
	public EntityManagerFactory getEntityManagerFactory()
	{
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("");
        return factory;
	}

    @BeforeClass
	public ValidatorFactory getEntityValidatorFactory()
	{
	    ValidatorFactory validator = Validation.buildDefaultValidatorFactory();
		return validator;
	}
	
	public List<Long> getWasteBasket()
	{
		return wasteBasket;
	}
}
