package de.sb.messenger.rest;

/**
 * Created by kapoor on 12.07.17.
 */

import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MessageServiceTest extends ServiceTest {

    @Test
    public void testCriteriaQueries() {
        WebTarget webTarget = newWebTarget("ines", "").path("messages");
        assertEquals(webTarget.request().get().getStatus(), Response.Status.UNAUTHORIZED);

        webTarget = newWebTarget("ines", "ines").path("messages");
        assertEquals(Response.Status.BAD_REQUEST, webTarget.queryParam("offset", -1).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST, webTarget.queryParam("limit", -1).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST, webTarget.queryParam("subject", "").request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST, webTarget.queryParam("subject", new String(new char[256])).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST, webTarget.queryParam("body", "").request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST, webTarget.queryParam("body", new String(new char[8190])).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST, webTarget.queryParam("author", 0).request().get().getStatus());
        assertEquals(Response.Status.BAD_REQUEST, webTarget.queryParam("author", 0).request().get().getStatus());

        assertEquals(Response.Status.OK.getStatusCode(), webTarget.request(MediaType.APPLICATION_JSON).get().getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), webTarget.request(MediaType.APPLICATION_XML).get().getStatus());

        String messages = webTarget.request(MediaType.APPLICATION_JSON).get().readEntity(String.class);
        assertFalse(messages.length() != 0);
    }

    @Test
    public void testIdentityQueries() {

    }

}
