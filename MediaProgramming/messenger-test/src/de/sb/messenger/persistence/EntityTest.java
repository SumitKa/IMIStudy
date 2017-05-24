package de.sb.messenger.persistence;
import org.junit.BeforeClass;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import javax.validation.*;
import java.util.*;
import java.util.List.*;

public class EntityTest {
	private EntityManagerFactory factory;
	private ValidatorFactory validator;


	private List<Long> wasteBasket = new ArrayList<Long>() {

    };

    public EntityManagerFactory getFactory() {
        return factory;
    }

    public ValidatorFactory getValidator() {
        return validator;
    }


    @BeforeClass
	public void initEntityManagerFactory()
	{
        this.factory = Persistence.createEntityManagerFactory("messenger");
	}

    @BeforeClass
	public void initEntityValidatorFactory()
	{
		this.validator = Validation.buildDefaultValidatorFactory();
	}
	
	public List<Long> getWasteBasket()
	{
		return wasteBasket;
	}
}
