package jcg.demo.spring.service;

import java.lang.reflect.Type;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class MessageTransformer {

	private static Gson gson = new Gson();

	public <R> R fromJson(String json, Class<R> returnType) {
		return gson.fromJson(json, returnType);
	}

	public <R> R fromJson(String json, Type returnType) {
		return gson.fromJson(json, returnType);
	}

	public <T> String toJson(T object) {
		return gson.toJson(object);
	}

}
