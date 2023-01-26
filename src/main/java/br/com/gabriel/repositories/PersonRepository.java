package br.com.gabriel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gabriel.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {}
