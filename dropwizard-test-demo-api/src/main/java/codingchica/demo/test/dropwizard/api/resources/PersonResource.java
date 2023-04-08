package codingchica.demo.test.dropwizard.api.resources;

import codingchica.demo.test.dropwizard.core.model.external.Person;
import com.codahale.metrics.annotation.Timed;
import javax.validation.constraints.Positive;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/** The Web service entry point into the application for CRUD operations involving people. */
@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {

  /**
   * Retrieve an existing person by ID.
   *
   * @param id The ID of the existing person to retrieve.
   * @return The existing person.
   */
  @GET
  @Timed
  @Path("/{id}")
  public Person getPerson(@Positive @PathParam("id") int id) {
    // TODO call service and return retrieved info.
    return Person.builder().id(1).firstName("John").lastName("Doe").nickName("Johnny").build();
  }
}