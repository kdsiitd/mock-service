package com.kds.mock.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Register custom serializers/deserializers
        SimpleModule module = new SimpleModule();
        module.addSerializer(HttpMethod.class, new HttpMethodSerializer());
        module.addDeserializer(HttpMethod.class, new HttpMethodDeserializer());
        objectMapper.registerModule(module);
        
        return objectMapper;
    }
    
    /**
     * Custom serializer for HttpMethod
     */
    public static class HttpMethodSerializer extends JsonSerializer<HttpMethod> {
        @Override
        public void serialize(HttpMethod value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.name());
        }
    }
    
    /**
     * Custom deserializer for HttpMethod
     */
    public static class HttpMethodDeserializer extends JsonDeserializer<HttpMethod> {
        @Override
        public HttpMethod deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            return HttpMethod.valueOf(value);
        }
    }
} 