package br.com.gabriel.integrationtests.controller.withyaml.mapper;

import com.fasterxml.jackson.databind.type.TypeFactory;

import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;

public class YMLMapper implements ObjectMapper {

	private com.fasterxml.jackson.databind.ObjectMapper objectMapper;
	protected TypeFactory typeFactory;
	
	
	@Override
	public Object deserialize(ObjectMapperDeserializationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object serialize(ObjectMapperSerializationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

}
