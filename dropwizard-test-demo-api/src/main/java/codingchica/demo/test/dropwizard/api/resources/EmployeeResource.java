package codingchica.demo.test.dropwizard.api.resources;

import codingchica.demo.test.dropwizard.core.model.external.Employee;
import com.codahale.metrics.annotation.Timed;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.hibernate.validator.constraints.Length;

/** The Web service entry point into the application for CRUD operations involving people. */
@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
public class EmployeeResource {

  /**
   * Retrieve an existing employee by GUID.
   *
   * @param guid The global unique ID of the existing employee to retrieve.
   * @return The existing employee.
   */
  @GET
  @Timed
  @Path("/{guid}")
  public Employee getEmployee(
      @Length(max = 50, message = "length must be less than or equal to 50") @NotBlank
          @Pattern(
              regexp = "^[A-Za-z0-9]*$",
              message = "Must contain only alphanumeric characters.")
          @PathParam("guid")
          String guid) {
    // TODO call service and return retrieved info.
    return Employee.builder()
        .guid(guid)
        .firstName("John")
        .lastName("Doe")
        .nickName("Johnny")
        .build();
  }
}
