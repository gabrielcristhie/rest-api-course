package br.com.gabriel.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.gabriel.controllers.BookController;
import br.com.gabriel.data.vo.v1.BookVO;
import br.com.gabriel.exceptions.RequiredObjectIsNullException;
import br.com.gabriel.exceptions.ResourceNotFoundException;
import br.com.gabriel.mapper.DozerMapper;
import br.com.gabriel.model.Book;
import br.com.gabriel.repositories.BookRepository;

@Service
public class BookServices {

	@Autowired
	private BookRepository repository;
	
	@Autowired
	PagedResourcesAssembler<BookVO> assembler;
	
	private Logger logger = Logger.getLogger(BookServices.class.getName());
	
	public BookVO findById(Long id) {
		
		logger.info("Finding one book!");
		
		// Busca no banco de dados por id ou retorna erro customizado
		Book entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No books found for this ID!"));
		
		// Convertendo Person para PersonVO
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		
		// Adicionando link Hateoas
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		
		return vo;
	}
	
	public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable){
		logger.info("Finding all books!");
		
		var booksPage = repository.findAll(pageable);
		
		var booksVOs = booksPage.map(p -> DozerMapper.parseObject(p, BookVO.class));
		
		booksVOs.map(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));
		
		Link findAllLink = linkTo(
				methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
				.withSelfRel();
		
		return assembler.toModel(booksVOs, findAllLink);
	}
	
	public BookVO create(BookVO bookVO) {
		
		// Jogando uma exception dependendo da regra de negocio
		if(bookVO == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one book!");
		
		// Convertendo de BooksVO para Books para salvar no BD
		var entity = DozerMapper.parseObject(bookVO, Book.class);
		
		// Salva no BD e depois devolve um BooksVO
		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
	public BookVO update(BookVO bookVO) {
		
		// Jogando uma exception dependendo da regra de negocio
		if(bookVO == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one book!");
		
		var entity = repository.findById(bookVO.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
	
		entity.setAuthor(bookVO.getAuthor());
		entity.setLaunchDate(bookVO.getLaunchDate());
		entity.setPrice(bookVO.getPrice());
		entity.setTitle(bookVO.getTitle());
		
		// Salva no BD e depois devolve um BooksVO
		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one book!");
		
		var entity = repository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("No records found for this ID!"));
		
		repository.delete(entity);
	}

}
