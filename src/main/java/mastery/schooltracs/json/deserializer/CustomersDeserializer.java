package mastery.schooltracs.json.deserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mastery.schooltracs.model.Customer;

public class CustomersDeserializer extends JsonDeserializer<List<Customer>> {

	private static final Logger logger = LoggerFactory.getLogger(CustomersDeserializer.class);

	@Override
	public List<Customer> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		List<Customer> list = new ArrayList<Customer>();

		do{
			p.nextToken();
		}while(!p.getCurrentName().equals("data"));

		JsonNode rootNode = p.getCodec().readTree(p);

		if(rootNode!=null){

			ObjectMapper mapper = new ObjectMapper();

			Iterator<JsonNode>itr = rootNode.iterator();

			while(itr.hasNext()){
				JsonNode node = itr.next();
				if(node.isArray()){
					for(JsonNode n: node){
						logger.debug("customer node=" + n);				
						Customer c = mapper.readValue(n.toString(), Customer.class);
						list.add(c);
					}
				}	
			}

		}

		return list;
	}

}
