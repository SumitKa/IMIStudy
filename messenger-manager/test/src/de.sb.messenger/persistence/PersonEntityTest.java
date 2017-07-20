package de.sb.messenger.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import javax.persistence.EntityManager;
import javax.validation.Validator;

public class PersonEntityTest extends EntityTest {

	Person testPerson = new Person();

	@Test
	public void testConstraints() {
		final Validator validator = this.getEntityValidatorFactory().getValidator();
		Person testPerson = populateTestPerson();

		assertEquals(0, validator.validate(testPerson).size());

		testPerson.getName().setFamily(null);
		testPerson.getName().setGiven(null);
		assertEquals(2, validator.validate(testPerson).size());
		populateTestPerson();

		testPerson.setEmail("sascha@baumeister.m1");
		assertEquals(2, validator.validate(testPerson).size());
		populateTestPerson();

		testPerson.setGroup(null);
		assertEquals(1, validator.validate(testPerson).size());
		populateTestPerson();

	}
	
	@Test
	public void testLifeCycle()
	{
		EntityManager entityManager = this.getEntityManagerFactory().createEntityManager();

		Person testPerson = populateTestPerson();
		BaseEntity subject = new BaseEntity();

		Message testMessage = new Message(testPerson, subject, "BODY");
		testMessage.setBody("HALLO");


		try {

			//initial write test
			entityManager.getTransaction().begin();
			entityManager.persist(testPerson);
			entityManager.getTransaction().commit();
			this.getWasteBasket().add(testPerson.getIdentity());
			long personIdentity = testPerson.getIdentity();
			entityManager.clear();

			entityManager.getTransaction().begin();
			entityManager.persist(testMessage);
			entityManager.getTransaction().commit();
			this.getWasteBasket().add(testMessage.getIdentity());
			long messageId = testMessage.getIdentity();
			entityManager.clear();

			//Update Test
			entityManager.getTransaction().begin();
			testPerson = entityManager.find(Person.class, personIdentity);
			String oldMail = testPerson.getEmail();
			testPerson.setEmail("new@mail.de");
			this.getWasteBasket().add(testPerson.getIdentity());
			entityManager.flush();
			String newMail = entityManager.find(Person.class, testPerson.getIdentity()).getEmail();
			assertNotSame(oldMail, newMail);
			entityManager.clear();

			// Delete Cascade Test
			long refMessage = entityManager.find(Message.class, messageId).getAuthorReference();
			entityManager.remove(entityManager.find(Person.class, personIdentity));
			assertNull(entityManager.find(Message.class, refMessage));
			entityManager.clear();
		} catch (Exception e) {
			if (entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			throw e;
		} finally {
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			this.emptyWasteBasket();
			entityManager.close();
		}
	}

	private Person populateTestPerson() {
		testPerson.setGroup(Person.Group.USER);
		testPerson.getName().setFamily("testFamilyName");
		testPerson.getName().setGiven("testGivenName");
		testPerson.setEmail("testEmail@test.de");
		return testPerson;
	}

}
