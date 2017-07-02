package de.sb.messenger.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.*;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.print.Doc;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import de.sb.messenger.persistence.Message;
import de.sb.messenger.persistence.Document;
import de.sb.messenger.persistence.Person;
import de.sb.toolbox.Copyright;
import de.sb.toolbox.net.RestCredentials;
import de.sb.toolbox.net.RestJpaLifecycleProvider;
import sun.security.x509.AVA;

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
    static private final String AVATAR = "select d.identity from Document as d where d.hash = :docHash";
    static private final String PEOPLE = "select p.identity from Person as p where  " +
            "(:familyName is null or p.name.family = :familyName) and " +
            "(:givenName is null or p.name.given = :givenName) and " +
            "(:groupAlias is null or p.group = :groupAlias) and " +
            "(:city is null or p.address.city = :city) and " +
            "(:postCode is null or p.address.postCode = :postCode) and " +
            "(:street is null or p.address.street = :street) and " +
            "(:email is null or p.contact.email = :email)";
    final EntityManager messengerManager = RestJpaLifecycleProvider.entityManager("messenger");


    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Person[] getPeople(
            @HeaderParam("Authorization") String authentication,
            @QueryParam("offset") @Min(0) int offset,
            @QueryParam("limit") @Min(0) int limit,
            @QueryParam("family-name") @Size(min = 1, max = 31) String familyName,
            @QueryParam("given-name") @Size(min = 1, max = 31) String givenName,
            @QueryParam("groupAlias") Person.Group groupAlias,
            @QueryParam("city") @Size(min = 1, max = 63) String city,
            @QueryParam("post-code") @Size(min = 1, max = 15) String postCode,
            @QueryParam("street") @Size(min = 1, max = 63) String street,
            @QueryParam("email") @Size(min = 1, max = 63) @Pattern(regexp ="^(.+)@(.+)$", message="{invalid.email}") String email)
    {
        try {
            Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

            TypedQuery<Long> query = messengerManager.createQuery(PEOPLE, Long.class);
            query.setParameter("familyName", familyName);
            query.setParameter("givenName", givenName);
            query.setParameter("groupAlias", groupAlias);
            query.setParameter("city", city);
            query.setParameter("postCode", postCode);
            query.setParameter("street", street);
            query.setParameter("email", email);

            if (offset > 0)
                query.setFirstResult(offset);
            if (limit > 0)
                query.setMaxResults(limit);

            List<Long> results = query.getResultList();
            Person[] peoples = new Person[results.size()];

            for (int i = 0; i < results.size(); i++) {
                peoples[i] = messengerManager.find(Person.class, results.get(i));
            }

            Arrays.sort(peoples, Comparator.comparing(Person::getIdentity));
            return peoples;

        }  finally {
            if (!messengerManager.getTransaction().isActive())
                messengerManager.getTransaction().begin();
        }
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
    public Person getPerson (
            @HeaderParam("Authorization") final String authentication,
            @PathParam("identity") final long identity) {
        try {
            Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));

            final Person entity = messengerManager.find(Person.class, identity);
            if (entity == null) throw new ClientErrorException(404);
            return entity;

        } finally {
            if (!messengerManager.getTransaction().isActive())
                messengerManager.getTransaction().begin();
        }
    }

    /**
     * Creates a new person if the given template's identity is zero,
     * or otherwise updates the corresponding person with the given template data.
     * Optionally, a new password may be set using the header field “Set-Password”. 
     * Returns the affected person's identity
     * @param authentication the HTTP Basic "Authorization" header value
     * @param personTemplate the entity
     * @return the messages caused by the matching entity (HTTP 200)
     * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
     * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
     * @throws ClientErrorException (HTTP 404) if the given message cannot be found
     * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
     * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
     *         thread is not open
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public long setPerson (
            @Valid @NotNull Person personTemplate,
            @HeaderParam("Authorization") final String authentication,
    		@HeaderParam("Set-Password") @NotNull String setPassword) {
        try {
            Person req = Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));
            if ((req.getIdentity() != personTemplate.getIdentity() && !req.getGroup().equals(Person.Group.ADMIN)))
                throw new ClientErrorException(401);
            if (!req.getGroup().equals(Person.Group.ADMIN) && personTemplate.getGroup().equals(Person.Group.ADMIN))
                throw new ClientErrorException(401);

            long identity;
            Person person = messengerManager.find(Person.class, personTemplate.getIdentity());

            if (person == null) {
                // TODO: Neue Person anlegen , new Person ist protected?
                identity = personTemplate.getIdentity();
            } else {
                person.setEmail(personTemplate.getEmail());
                person.setGroup(personTemplate.getGroup());
                // TODO Felder setzen, frage: Class Person enthaelt keine getter und setter fuer die Felder?
                identity = person.getIdentity();
                messengerManager.getTransaction().commit();
            }

            return identity;
        } finally {
            if (!messengerManager.getTransaction().isActive())
                messengerManager.getTransaction().begin();
        }
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
        try {
            return (Authenticator) Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));
        } catch (NotAuthorizedException e) {
            throw new ClientErrorException(401);
        } finally {
            if (!messengerManager.getTransaction().isActive())
                messengerManager.getTransaction().begin();
        }

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
    public Message[] getMessageAuthored (
            @HeaderParam("Authorization") final String authentication,
            @PathParam("identity") final long identity) {
        try {
            final Person requester = Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));
            Person person = messengerManager.find(Person.class, identity);

            if (person == null)
                throw new ClientErrorException(404);

            Set<Message> matchingMessages = new HashSet<>();

            for (Message message : person.getMessageAuthored()) {
                if (requester.getIdentity() == identity)
                    matchingMessages.add(message);
            }

            Message[] messages = matchingMessages.toArray(new Message[0]);
            Arrays.sort(messages, Comparator.comparing(Message::getCreationTimestamp));
            return  messages;
        } finally {
            if (!messengerManager.getTransaction().isActive())
                messengerManager.getTransaction().begin();
        }
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
    @Path("{identity}/people/peopleObserved")
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
    @Produces({MediaType.WILDCARD})
    public Response getPersonAvatar (
            @HeaderParam("Authorization") final String authentication,
            @PathParam("identity") final long identity) {
        try {
            Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));
            Person person = messengerManager.find(Person.class, identity);

            if (person == null)
                throw new ClientErrorException(404);

            Document avatar = person.getAvatar();

            if (avatar == null) {
                return Response.noContent().build();
            }

            return Response.ok(avatar.getContent(), avatar.getContentType()).build();

        } finally {
            if (!messengerManager.getTransaction().isActive())
                messengerManager.getTransaction().begin();
        }
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
    @Consumes({MediaType.WILDCARD})
    public long setPersonAvatar (
            @HeaderParam("Authorization") final String authentication,
            @PathParam("identity") final long identity,
            @NotNull byte[] documentContent,
            @NotNull @HeaderParam("Content-Type") String contentType) {
        Person person = null;
        try {
            final Person requester = Authenticator.authenticate(RestCredentials.newBasicInstance(authentication));
            person = messengerManager.find(Person.class, identity);

            if (person == null)
                throw new ClientErrorException(404);

            if (requester.getIdentity() != identity && !requester.getGroup().equals(Person.Group.ADMIN))
                throw new ClientErrorException(403);

            byte[] documentHash = Document.mediaHash(documentContent);
            TypedQuery query = messengerManager.createQuery(AVATAR, Document.class);
            query.setParameter("docHash", documentHash);
            List<Long> result = query.getResultList();
            Document doc;

            if (result.size() == 1) {
                doc = messengerManager.find(Document.class, result.get(0));
                doc.setContentType(contentType);
                messengerManager.flush();
            } else {
                doc = new Document(documentContent, contentType);
                messengerManager.persist(doc);
            }

            messengerManager.getTransaction().commit();
            messengerManager.getTransaction().begin();
            person.setAvatar(doc);
            messengerManager.getTransaction().commit();

            // Person Identity? Or Http 200? (Response.ok().build() ? )
            return person.getIdentity();
        } finally {
            if (!messengerManager.getTransaction().isActive())
                messengerManager.getTransaction().begin();
            if (person != null) {
                Cache cache = messengerManager.getEntityManagerFactory().getCache();
                cache.evict(person.getClass(), person.getIdentity());
            }
        }
    }
}