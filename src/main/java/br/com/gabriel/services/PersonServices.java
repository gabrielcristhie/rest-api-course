package br.com.gabriel.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import br.com.gabriel.controllers.PersonController;
import br.com.gabriel.data.vo.v1.PersonVO;
import br.com.gabriel.exceptions.RequiredObjectIsNullException;
import br.com.gabriel.exceptions.ResourceNotFoundException;
import br.com.gabriel.mapper.DozerMapper;
import br.com.gabriel.model.Person;
import br.com.gabriel.repositories.PersonRepository;

@Service
public class PersonServices {

	private Logger logger = Logger.getLogger(PersonServices.class.getName());

	@Autowired
	private PersonRepository repository;

	public PersonVO findById(Long id) throws Exception {

		logger.info("Finding one person!");

		// Procura um Person por id no Banco de dados ou retorna o erro customizado
		var entity =  repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		
		// Converte Person em PersonVO
		var vo = DozerMapper.parseObject(entity, PersonVO.class);
		
		// Adiciona o link de referência hateoas
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		
		return vo;
	}

	public List<PersonVO> findAll() {

		var persons =  DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
		persons
			.stream()
			.forEach(p -> {
				try {
					p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		return persons;
	}

	public PersonVO create(PersonVO personVO) throws Exception {
		
		if(personVO == null) throw new RequiredObjectIsNullException();

		logger.info("Creating one person!");
		
		//Converte o personVO em Person
		var entity = DozerMapper.parseObject(personVO, Person.class);
		
		//Salva o person e depois devolve um personVO para retorno
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}

	public PersonVO update(PersonVO personVO) throws Exception {
		
		if(personVO == null) throw new RequiredObjectIsNullException();

		logger.info("Updating one person!");

		var entity = repository.findById(personVO.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		entity.setFirstName(personVO.getFirstName());
		entity.setLastName(personVO.getLastName());
		entity.setAddress(personVO.getAddress());
		entity.setGender(personVO.getGender());

		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}

	public void delete(Long id) {

		logger.info("Deleting one person!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		repository.delete(entity);
	}

}
