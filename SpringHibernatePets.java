package com.amex.ea.example.spring.codility;

import java.util.Set;
import javax.persistence.*;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

/*
Implement a Spring + Hibernate Service with a one-to-many relation.

You are given a Spring Boot version 2.0.5 application with database access already configured.

The database contains two tables: person and pet. A person can have many pets, and every pet has an owner.

Database schema
CREATE TABLE person (
  id bigint NOT NULL,
  first_name varchar(50) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE pet (
id bigint NOT NULL,
name varchar(50) NOT NULL,
owner_id bigint NOT NULL,


PRIMARY KEY (id)
);


ALTER TABLE pet
ADD FOREIGN KEY (owner_id)
REFERENCES person(id);

Requirements
1. Register the PersonService class as Spring Bean.

2. Model the relationship between Pet and Person by creating class fields. Add appropriate hibernate/JPA annotations.

3. Implement the Person.getPets method. It should return all the pets of a given person.

4. Implement the Pet.getOwnerName method. It should return the pet owner's first name.

5. Implement the PersonService.addPet method. It should add a pet of the given name to a person of the given id. If a person with the given id does not exist, you should throw a PersonNotFoundException.

Assessment/Tools
1. Names of existing classes and methods cannot be changed.

2. Database access is fully configured.

3. Use EntityManager for database access.

Libraries
You can use following libraries and Spring Boot application (version 2.0.5):

1. org.springframework.*

2. org.springframework.stereotype.*

3. org.springframework.transaction.annotation.*

4. javax.persistence.*

5. java.util.*
 */
public class SpringHibernatePets {

  @Entity
  class Person {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    @OneToMany(cascade = CascadeType.ALL,
        fetch = FetchType.EAGER,
        mappedBy = "owner")
    private Set<Pet> pets;

    public String getFirstName() {
      return firstName;
    }

    public Set<Pet> getPets() {
      //FIXME
      return pets;
    }

  }

  @Entity
  class Pet {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Person owner;

    private String name;

    public void setName(String name) {
      this.name = name;
    }

    public void setOwner(Person owner) {
      this.owner = owner;
    }

    public String getOwnerName() {
      return owner.getFirstName();
    }

    public String getName() {
      return name;
    }
  }

  class PersonNotFoundException extends RuntimeException {

  }

  @Service
  class PersonService {


    private final EntityManager entityManager;

    @Autowired
    public PersonService(EntityManager entityManager) {
      this.entityManager = entityManager;
    }

    @Transactional
    public void addPet(Long personId, String petName) {
      Person person = entityManager.find(Person.class, personId);
      if(person == null) {
        throw new PersonNotFoundException();
      }
      Pet pet = new Pet();
      pet.setName(petName);
      pet.setOwner(person);
      person.getPets().add(pet);
      entityManager.merge(person);
    }
  }
}
