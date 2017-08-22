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

import mastery.schooltracs.model.StaffWorkHour;

public class StaffWorkHoursDeserializer extends JsonDeserializer<List<StaffWorkHour>> {

	private static final Logger logger = LoggerFactory.getLogger(StaffWorkHoursDeserializer.class);
	
	@Override
	public List<StaffWorkHour> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		List<StaffWorkHour> list = new ArrayList<StaffWorkHour>();
		
		JsonNode rootNode = p.getCodec().readTree(p);
	
		if(rootNode!=null){
			
			ObjectMapper mapper = new ObjectMapper();
			
			Iterator<JsonNode>itr = rootNode.iterator();
			
			while(itr.hasNext()){
				JsonNode node = itr.next();
				if(node.isArray()){
					for(JsonNode n: node){
						logger.debug("staff work hour node=" + n);
						StaffWorkHour s = mapper.readValue(n.toString(), StaffWorkHour.class);
						list.add(s);
					}
				}	
			}
			
		}
		
		return list;
	}

}
