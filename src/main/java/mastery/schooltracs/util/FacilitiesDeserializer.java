package mastery.schooltracs.util;

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

import mastery.schooltracs.model.Facility;

public class FacilitiesDeserializer extends JsonDeserializer<List<Facility>> {

	private static final Logger logger = LoggerFactory.getLogger(FacilitiesDeserializer.class);
	
	@Override
	public List<Facility> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		List<Facility> list = new ArrayList<Facility>();
		
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
						logger.debug("facility node=" + n);				
						Facility fac = mapper.readValue(n.toString(), Facility.class);
						list.add(fac);
					}
				}	
			}
			
		}

		return list;
	}

}
