package de.sb.messenger.rest;

import static de.sb.messenger.persistence.Person.Group.ADMIN;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import java.util.Arrays;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import de.sb.messenger.persistence.BaseEntity;
import de.sb.messenger.persistence.Message;
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
    @Path("{identity}/people")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Person queryPerson (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }

    /**
     * Creates a new person if the given template's identity is zero,
     * or otherwise updates the corresponding person with the given template data.
     * Optionally, a new password may be set using the header field “Set-Password”.
     * Returns the affected person's identity
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
    @Path("{identity}/people")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public long query (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }

    /**
     * Returns the authenticated requester, which is useful for login operations.
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
    @Path("{identity}/people/requester")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public "" queryAuthenticatedRequester (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }

    /**
     * Returns  the person matching the given identity.
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
    public Person queryPerson (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }

    /**
     * Returns the messages authored by the person matching the given identity.
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
    @Path("{identity}/people/{identity}/messagesAuthored")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Message queryMessageAuthored (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }

    /**
     * Returns the people observing the person matching the given identity.
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
    @Path("{identity}/people/{identity}/peopleObserving")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Person queryPeopleObserving (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }

    /**
     * Returns the people observed by the person matching the given identity.
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
    @Path("{identity}/people/{identity}/peopleObserved")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Person queryPeopleObserved (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
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
    public Person queryPeopleObserved (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }

    /**
     *   Returns the avatar content of the person matching the given identity
     *   (plus it's content type as part of the HTTP response header).
     *   Hint: Use @Produces(WILDCARD) to declare production of an a priori unknown media type,
     *   and return an instance of Result that contains both the document's media type and it's content.
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
    @Path("{identity}/people/{identity}/avatar")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Document queryPeopleAvatar (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
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
    public long queryPeopleAvatar (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }
}