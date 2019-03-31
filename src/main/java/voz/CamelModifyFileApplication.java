package voz;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class CamelModifyFileApplication {

	public static void main(String[] args) {
		SpringApplication.run(CamelModifyFileApplication.class, args);
	}

	@Bean
	public Marshaller getMarshaller() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(ru.sberbank.edo.oep.edo_oep_document.Package.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		return jaxbMarshaller;
	}
	
}
