package de.sb.messenger.persistence;
import java.util.List;

public class EntityTest {

	private List<long> wasteBasket = new List<long>();
	
	public EntityManagerFactory EntityManagerFactory()
	{
		return new EntityManagerFactory();
	}
	
	public ValidatorFactory getEntityValidatorFactory()
	{
		return new ValidatorFactory();
	}
	
	public List<long> getWasteBasket()
	{
		return wasteBasket;
	}
}
