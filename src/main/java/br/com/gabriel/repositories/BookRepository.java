package br.com.gabriel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gabriel.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>  {

}
