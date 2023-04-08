package codingchica.demo.test.dropwizard.service;

import codingchica.demo.test.dropwizard.core.model.internal.Person;
import codingchica.demo.test.dropwizard.core.model.mappers.PersonMapper;
import codingchica.demo.test.dropwizard.core.model.mappers.PersonMapperImpl;

/** The internal business layer logic for handling of a person. */
public class PersonService {
  /** A mapper to translate between different layer's representations of a person. */
  private PersonMapper personMapper = new PersonMapperImpl();

  /**
   * Retrieve an existing person by ID.
   *
   * @param id The numeric identifier of an existing person.
   * @return The existing person that can be returned to the client, if present.
   */
  public codingchica.demo.test.dropwizard.core.model.external.Person getPersonById(int id) {
    // TODO retrieve this person from the DB
    Person person = Person.builder().id(id).first("John").last("Doe").nickName("Johnny").build();

    return personMapper.internalToExternalPersonMapping(person);
  }
}
