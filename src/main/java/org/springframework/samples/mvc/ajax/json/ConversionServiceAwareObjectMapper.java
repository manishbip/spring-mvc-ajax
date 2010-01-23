package org.springframework.samples.mvc.ajax.json;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

public class ConversionServiceAwareObjectMapper extends ObjectMapper {

	@Autowired
	public ConversionServiceAwareObjectMapper(ConversionService conversionService) {
		AnnotationIntrospector introspector = AnnotationIntrospector.pair(new FormatAnnotationIntrospector(conversionService), DEFAULT_ANNOTATION_INTROSPECTOR);
		SerializationConfig sconfig = new SerializationConfig(DEFAULT_INTROSPECTOR, introspector);
		DeserializationConfig dconfig = new DeserializationConfig(DEFAULT_INTROSPECTOR, introspector);
		setSerializationConfig(sconfig);
		setDeserializationConfig(dconfig);
	}
}
