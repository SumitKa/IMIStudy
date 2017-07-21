package de.sb.messenger.rest;

/**
 * Created by kapoor on 12.07.17.
 */

import de.sb.messenger.persistence.Person;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;


public class PersonServiceTest extends ServiceTest {

    @Test
    public void testCriteriaQueries() {
        // authentication
        WebTarget webTarget = newWebTarget("sascha", "").path("people");
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), webTarget.request().get().getStatus());

        webTarget = newWebTarget("sascha", "sascha").path("people");
        // illegal arguments
        final Response response = webTarget.queryParam("offset", -1).request().get();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        Person entity = response.readEntity(Person.class);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("limit", -1).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("name", "").request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("name", new String(new char[0])).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("name", new String(new char[32])).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST, webTarget.queryParam("group", null).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("city", "").request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("city", new String(new char[0])).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("city", new String(new char[64])).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("street", "").request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("street", new String(new char[0])).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("street", new String(new char[64])).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("email", "").request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("email", new String(new char[0])).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), webTarget.queryParam("email", new String(new char[64])).request().get().getStatus());

        assertEquals(Response.Status.OK.getStatusCode(), webTarget.request(MediaType.APPLICATION_JSON).get().getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), webTarget.request(MediaType.APPLICATION_XML).get().getStatus());
    }

    @Test
    public void testIdentityQueries() {

    }

}
