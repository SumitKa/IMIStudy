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
import javax.ws.rs.PUT;
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
public class MessageService {

    /**
     * Returns the entity with the given identity.
     *
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity       the entity identity
     * @return the matching entity (HTTP 200)
     * @throws ClientErrorException  (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException  (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException  (HTTP 404) if the given entity cannot be found
     * @throws PersistenceException  (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *                               thread is not open
     */
    @GET
    @Path("{identity}")
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public Message queryIdentity(@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Message entity = messengerManager.find(Message.class, identity);
        if (entity == null) throw new NotFoundException();
        return entity;
    }


    /**
     * Deletes the entity matching the given identity, or does nothing if no such entity exists.
     *
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity       the identity
     * @return void (HTTP 204)
     * @throws ClientErrorException  (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException  (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException  (HTTP 403) if authentication is successful, but the requester is
     *                               not an administrator
     * @throws ClientErrorException  (HTTP 404) if the given entity cannot be found
     * @throws ClientErrorException  (HTTP 409) if there is a database constraint violation (like
     *                               conflicting locks)
     * @throws PersistenceException  (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *                               thread is not open
     */
    @DELETE
    @Path("{identity}")
    public void deleteEntity(@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        final Person requester = Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        if (requester.getGroup() != ADMIN) throw new ClientErrorException(FORBIDDEN);
        messengerManager.getEntityManagerFactory().getCache().evict(BaseEntity.class, identity);

        final Message entity = messengerManager.find(Message.class, identity);
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
     * Creates a new message with the requesting person as author, using the given form fields “content” and “subjectReference”,
     * and returning the message's identity.
     * Hint: Make sure that the author and the (polymorphic) subject are evicted from 2nd-level cache once the message is stored,
     * because their mirror relations have to change.
     *
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity       the entity identity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException  (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException  (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException  (HTTP 404) if the given message cannot be found
     * @throws PersistenceException  (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *                               thread is not open
     */
    @PUT
    @Path("{identity}/message")
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public long queryMessage(@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }

    /**
     * Returns the message matching the given identity.
     *
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity       the entity identity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException  (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException  (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException  (HTTP 404) if the given message cannot be found
     * @throws PersistenceException  (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *                               thread is not open
     */
    @GET
    @Path("{identity}/message/{identity}")
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public Message queryMessage(@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }

    /**
     * Returns the author of the message matching the given identity.
     *
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity       the entity identity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException  (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException  (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException  (HTTP 404) if the given message cannot be found
     * @throws PersistenceException  (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *                               thread is not open
     */
    @GET
    @Path("{identity}/message/{identity}/author")
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public Person queryMessageAuthor(@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }

    /**
     * Returns the (polymorphic) subject of the message matching the given identity.
     *
     * @param authentication the HTTP Basic "Authorization" header value
     * @param identity       the entity identity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException  (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException  (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException  (HTTP 404) if the given message cannot be found
     * @throws PersistenceException  (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *                               thread is not open
     */
    @GET
    @Path("{identity}/message/{identity}/subject")
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public long queryMessageSubject(@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity, );
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
    }
}