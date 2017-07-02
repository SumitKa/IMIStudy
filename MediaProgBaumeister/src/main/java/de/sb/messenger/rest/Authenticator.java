package de.sb.messenger.rest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotAuthorizedException;

import de.sb.messenger.persistence.Document;
import de.sb.messenger.persistence.Person;
import de.sb.toolbox.net.HttpCredentials;
import de.sb.toolbox.net.RestJpaLifecycleProvider;

import java.util.Arrays;
import java.util.List;


/**
 * Facade interface for HTTP authentication purposes.
 */
public interface Authenticator {

    String PERSON_BY_EMAIL = "select p from Person as p where p.email = :email";

	/**
	 * Returns the authenticated requester (a person) for the given HTTP Basic authentication credentials.
	 * A SHA-256 hash-code is calculated for the contained password, and uses it in conjunction with the
	 * user email to query and return a suitable Person entity from the database.
	 * @param credentials the HTTP Basic authentication credentials
	 * @return the authenticated requestor
	 * @throws NotAuthorizedException (HTTP 401) if the given credentials are invalid
	 * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
	 * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
	 *         thread is not open
	 * @throws NullPointerException (HTTP 500) if the given credentials are {@code null}
	 */
	static Person authenticate (final HttpCredentials.Basic credentials) throws NotAuthorizedException, PersistenceException, IllegalStateException, NullPointerException {
		final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");

		if(credentials == null) throw new NullPointerException();

		final String username = credentials.getUsername();
		final String password = credentials.getPassword();

		// Add JPA authentication by calculating the password hash from the given password,
		// creating a query using the constant above, and returning the person if it matches the
		// password hash. If there is none, or if it fails the password hash check, then throw
		// NotAuthorizedException("Basic"). Note that this exception type is a specialized Subclass
		// of ClientErrorException that is capable of storing an authentication challenge.

        TypedQuery<Person> query = messengerManager.createQuery(PERSON_BY_EMAIL, Person.class);
        List<Person> results = query.setParameter("email", username).getResultList();
        Person person = null;
        if (results.size() > 0)
            person = results.get(0);
        if (person == null)
            throw new NullPointerException();
        else {
            if (Arrays.toString(Document.mediaHash(password.getBytes())).equals(Arrays.toString(Person.passwordHash(password))))
                return person;
            else
                throw new NotAuthorizedException("Basic", 401);
        }


	}
}
