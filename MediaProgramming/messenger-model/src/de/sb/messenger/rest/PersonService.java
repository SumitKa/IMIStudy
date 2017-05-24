package de.sb.messenger.rest;

import static de.sb.messenger.persistence.Person.Group.ADMIN;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.persistence.eis.interactions.QueryStringInteraction;

import de.sb.messenger.persistence.Adress;
import de.sb.messenger.persistence.BaseEntity;
import de.sb.messenger.persistence.Message;
import de.sb.messenger.persistence.Name;
import de.sb.messenger.persistence.Person;
import de.sb.toolbox.Copyright;
import de.sb.toolbox.net.RestCredentials;
import de.sb.toolbox.net.RestJpaLifecycleProvider;

/**
 * JAX-RS based REST service implementation for polymorphic entity resources. The following path and
 * method combinations are supported:
 * <ul>
 * <li>GET entities/{identity}: Returns the entity matching the given identity.</li>
 * <li>DELETE entities/{identity}: Deletes the entity matching the given identity.</li>
 * <li>GET entities/{identity}/messagesCaused: Returns the messages caused by the entity matching
 * the given identity.</li>
 * </ul>
 */
@Path("entities")
@Copyright(year=2017, holders="")
public class PersonService {

    /**
     * Returns the entity with the given identity.
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity the entity identity
     * @return the matching entity (HTTP 200)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 404) if the given entity cannot be found
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @GET
    @Path("{identity}")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Person queryIdentity (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity);
        if (entity == null) throw new NotFoundException();
        return entity;
    }


    /**
     * Deletes the entity matching the given identity, or does nothing if no such entity exists.
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity the identity
     * @return void (HTTP 204)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 403) if authentication is successful, but the requester is
     *         not an administrator
     * @throws ClientErrorException (HTTP 404) if the given entity cannot be found
     * @throws ClientErrorException (HTTP 409) if there is a database constraint violation (like
     *         conflicting locks)
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @DELETE
    @Path("{identity}")
    public void deleteEntity (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        final Person requester = Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        if (requester.getGroup() != ADMIN) throw new ClientErrorException(FORBIDDEN);
        messengerManager.getEntityManagerFactory().getCache().evict(BaseEntity.class, identity);

        final Person entity = messengerManager.find(Person.class, identity);
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
        messengerManager.remove(entity);

        try {
            messengerManager.getTransaction().commit();
        } catch (final RollbackException exception) {
            throw new ClientErrorException(CONFLICT);
        } finally {
            messengerManager.getTransaction().begin();
        }
    }

    /**
     * Returns the person matching the given criteria, with missing parameters identifying omitted criteria.
     * @param authentication the HTTP Basic "Authorization" header value
     * @param group the person group
     * @param name the person name
     * @param adress the person adress
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 404) if the given message cannot be found
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @GET
    @Path("{identity}/people")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public List<Person> getPersons (@HeaderParam("Authorization") final String authentication, 
    		@PathParam("group") final Person.Group group, 
    		@PathParam("name") final Name name, 
    		@PathParam("adress") final Adress adress) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        
        //Query query = messengerManager.createQuery();
        //return query.getResultList();
        return null;
    }

    /**
     * Creates a new person if the given template's identity is zero,
     * or otherwise updates the corresponding person with the given template data.
     * Optionally, a new password may be set using the header field “Set-Password”. 
     * Returns the affected person's identity
     * @param authentication the HTTP Basic "Authorization" header value
     * @param person the entity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 404) if the given message cannot be found
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @PUT
    @Path("{identity}/people")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public long putPerson (@HeaderParam("Authorization") final String authentication, 
    		@PathParam("person")final Person entity, 
    		@HeaderParam("Set-Password") final String setPassword) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        
        if(setPassword != null)
        {
        	// TODO
        }
        
        if(entity.getIdentity() == 0)
        {
        	messengerManager.persist(entity);
        }
        else
        {
        	messengerManager.refresh(entity);
        }
        
        return entity.getIdentity();
    }

    /**
     * Returns the authenticated requester, which is useful for login operations.
     * @param authentication the HTTP Basic "Authorization" header value
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 404) if the given message cannot be found
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @GET
    @Path("{identity}/people/requester")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Authenticator getAuthenticatedRequester (@HeaderParam("Authorization") final String authentication) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        //TODO
        return null;
    }

    /**
     * Returns the person matching the given identity.
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity the entity identity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 404) if the given message cannot be found
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @GET
    @Path("{identity}/people/{identity}")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Person getPerson (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity);
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
        
        return entity;
    }

    /**
     * Returns the messages authored by the person matching the given identity.
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity the person identity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 404) if the given message cannot be found
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @GET
    @Path("{identity}/people/{identity}/messagesAuthored")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public List<Message> getMessageAuthored (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        // TODO
        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Message entity = messengerManager.find(Message.class, identity);
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
        
        //entity.getAuthor().getIdentity() == identity
        return null;
    }

    /**
     * Returns the people observing the person matching the given identity.
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity the person identity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 404) if the given message cannot be found
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @GET
    @Path("{identity}/people/{identity}/peopleObserving")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public List<Person> getPeopleObserving (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        //TODO
        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity);
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
        
        return null;
    }

    /**
     * Returns the people observed by the person matching the given identity.
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity the person identity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 404) if the given message cannot be found
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @GET
    @Path("{identity}/people/{identity}/peopleObserved")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public List<Person> getPeopleObserved (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        // TODO
        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity);
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
        
        return null;
    }

    /**
     *  Updates the person matching the given identity to monitor the people matching the form-supplied collection of person identities.
     *  Hint: Make sure all people whose Mirror-Relations change due to this operation are evicted from the 2nd-level cache!
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity the entity identity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 404) if the given message cannot be found
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @PUT
    @Path("{identity}/people/{identity}/peopleObserved")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public void putPeopleObserved (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity);
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }

    /**
     *   Returns the avatar content of the person matching the given identity
     *   (plus it's content type as part of the HTTP response header).
     *   Hint: Use @Produces(WILDCARD) to declare production of an a priori unknown media type,
     *   and return an instance of Result that contains both the document's media type and it's content.
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity the person identity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 404) if the given message cannot be found
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @GET
    @Path("{identity}/people/{identity}/avatar")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public byte[] getPersonAvatar (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        // TODO 
        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        //final Document entity = messengerManager.find(Person.class, identity);
        //if (entity == null) throw new ClientErrorException(NOT_FOUND);
        
        return null;
    }

    /**
     *    Updates the person's avatar (owner only), with the document content being passed as the HTTP request body,
     *    and the media type passed as Header-Field “Content-type”. 
     *    If the given content is empty, the person's avatar is set to the default document (identity=1).
     *    Otherwise, if a document matching the media hash of the given content already exists,
     *    then this document becomes the person's avatar. Otherwise,
     *    the given content and content-type is used to create a new document,
     *    and it is registered as the person's avatar.
     *    Hint: Use @Consumes(WILDCARD) to declare consumption of an a priori unknown media type.
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity the entity identity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 404) if the given message cannot be found
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @PUT
    @Path("{identity}/people/{identity}/avatar")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public void updatePersonAvatar (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity);
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }
}