package voz;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Base64;

import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import ru.sberbank.edo.oep.edo_oep_document.Package;

@Component
public class PackageRoute extends RouteBuilder {

	@Autowired
	private Marshaller marshaller;
	
	@Value("${folder.in}")
	private String folderIn;
	
	@Value("${folder.out}")
	private String folderOut;
	
	@Override
	public void configure() throws Exception {
		from("file:"+folderIn+"?noop=true")
		.process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				String body=exchange.getIn().getBody(String.class);
				String typeDoc=getRootElem(body);
				body="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+body;
				String document = Base64.getEncoder().encodeToString(body.getBytes());
				Package pkg=new Package();
				pkg.setTypeDocument(typeDoc);
				pkg.setDocument(document);
				pkg.setSignature("SIGN");
				StringWriter sw = new StringWriter();
				marshaller.marshal(pkg, sw);
				body=sw.toString();				
				exchange.getOut().setBody(body);
				String inputFile=exchange.getIn().getHeader(Exchange.FILE_NAME).toString();
				exchange.getOut().setHeader(Exchange.FILE_NAME, inputFile+"_pkg");
			}
			
		})
		.to("file:"+folderOut);		
	}

	static String getRootElem(String xmlRecords) throws Exception {
		InputSource is = new InputSource();
	    is.setCharacterStream(new StringReader(xmlRecords));
	    
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    
	    Document doc = db.parse(is);
	    
	    Element root = doc.getDocumentElement();
	    return root.getNodeName();		
	}
}
