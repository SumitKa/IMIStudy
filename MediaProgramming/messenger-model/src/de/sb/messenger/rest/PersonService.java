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
import javax.xml.ws.Response;

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
@Path("people")
@Copyright(year=2017, holders="")
public class PersonService {

    //TODO GET. methode -> Liste von Person (sortiert),
    // query definieren = filter definieren nummerisch ober untergrenze felder mit istgleich
    // offset und länge als sonderkriterien
    // alle anderen als parameter
    // identitäten zurückliefern nicht die obkekte selber
    // über person durch identitäten durchiterieren
    @GET
    public Person get(@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity)
    select x from X as x where
    (:lowerNumber is null or x.number >= :lowerNumber) and
    (:upperNumber is null or x.number <= :upperNumber) and
    (:text is null or x.text = :text)
    his.name = new Name();
		this.adress = new Adress();
		this.group = Group.USER;
		this.messageAuthored = Collections.emptyList();
		this.peopleOberserving = new Person();
		this.peopleOberserved = new Person();
		this.passHash = new byte[0];
		this.email = email;
		this.avatar = avatar;
    {

    }

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
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public long putPerson (@HeaderParam("Authorization") final String authentication, 
    		@PathParam("person")final Person template,
    		@HeaderParam("Set-Password") final String setPassword) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        
        if(setPassword != null)
        {
            setPassword = template.passwordHash(setPassword);
            messengerManager.persist(setPassword); // ???
        }

        final Person entity;
        if(template.getIdentity() == 0)
        {
            entity = new Person();
            entity.setEmail(template.getEmail());
            entity.setGroup(template.getGroup());
            // TODO felder setzen ???
        }
        else
            // TODO try finaly
            entity = messengerManager.find(template.getIdentity())

        if(template.getIdentity() == 0)
            messengerManager.persist(entity);
        else
            messengerManager.flush(entity);

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
    @Path("{identity}/requester")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Authenticator getAuthenticatedRequester (@HeaderParam("Authorization") final String authentication) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        //TODO
        return null;
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
    @Path("{identity}/messagesAuthored")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Message[] getMessageAuthored (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity);
        if (entity == null) throw new ClientErrorException(NOT_FOUND);

        // TODO in einem array sortieren (einfacher) oder in einem treeset sortieren
        // Array zurückgeben
        Message[] messeges = entity.getMessagesAuthored().toArray();

        return messages;
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
    @Path("{identity}/peopleObserving")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public List<Person> getPeopleObserving (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity);
        if (entity == null) throw new ClientErrorException(NOT_FOUND);
        
        return entity.getPeopleObserving();

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
    @Path("{identity}/peopleObserved")
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
    @Path("{identity}/avatar")
    @Produces({ *.* })
    // TODO Response importieren (breite und höhe)
    public Response getPersonAvatar (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        // TODO 
        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");

        Response response;
        //final Document entity = messengerManager.find(Person.class, identity);
        //if (entity == null) throw new ClientErrorException(NOT_FOUND);
        // Inhalt und mimetype
        // es sollte immer einen geben!!! wenn avatar null dann default
        // niemals null zurückgeben!!
        // größe anpassen?!

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
    @Path("{identity}/avatar")
    @Produces({ *.* }) // oder IMAGE/*
    // TODO byte[] und mimetype übernehmen
    public long updatePersonAvatar (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
        Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

        final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");
        final Person entity = messengerManager.find(Person.class, identity);
        if (entity == null) throw new ClientErrorException(NOT_FOUND);

        // hashwert bilder
        // gibt es schon einen eintrag
        // wenn ja nicht speichern nur laden
        // wenn nciht dann in der datenbank speichern
        messengerManager.find(Person.class, getPersonAvatar(authentication, identity));

        return // ID
    }
}