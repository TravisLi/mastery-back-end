package mastery.schooltracs.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mastery.schooltracs.model.Activity;
import mastery.schooltracs.model.CustomerMap;
import mastery.schooltracs.model.FacilityMap;
import mastery.schooltracs.model.SearchResponse;
import mastery.schooltracs.model.StaffMap;

public class SearchResponseDeserializer extends JsonDeserializer<SearchResponse> {

	private static final Logger logger = LoggerFactory.getLogger(SearchResponseDeserializer.class);
	private static final String ACT_KEY = "activities";
	private static final String STAFF_KEY = "staffMap";
	private static final String CUST_KEY = "customerMap";
	private static final String FAC_KEY = "facilityMap";
	
	@Override
	public SearchResponse deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		SearchResponse sr = new SearchResponse();

		do{
			p.nextToken();
		}while(!p.getCurrentName().equals(ACT_KEY));

		JsonNode rootNode = p.getCodec().readTree(p);
	
		sr.setActivites(processActivity(rootNode.get(ACT_KEY)));
		sr.setStaffMaps(processStaff(rootNode.get(STAFF_KEY)));
		sr.setCustomerMaps(processCustomer(rootNode.get(CUST_KEY)));
		sr.setFacilityMaps(processFacility(rootNode.get(FAC_KEY)));

		return sr;
	}

	private static List<Activity> processActivity(JsonNode actNode) throws JsonParseException, JsonMappingException, IOException{

		logger.info("Process Activities");

		List<Activity> list = new ArrayList<Activity>();

		if(actNode!=null){
			
			ObjectMapper mapper = new ObjectMapper();
			
			if(actNode.isArray()){
				for(JsonNode node: actNode){
					logger.debug("activities node" + node);				
					Activity act = mapper.readValue(node.toString(), Activity.class);
					list.add(act);
				}
			}
		}
		
		
		return list;
	}
	
	private static List<StaffMap> processStaff(JsonNode staffNode) throws JsonParseException, JsonMappingException, IOException{
		
		logger.info("Process Staff Map");
		
		List<StaffMap> list = new ArrayList<StaffMap>();
		
		if(staffNode!=null){
			
			ObjectMapper mapper = new ObjectMapper();
			
			Iterator<JsonNode>itr = staffNode.iterator();
			
			while(itr.hasNext()){
				JsonNode node = itr.next();
				if(node.isArray()){
					for(JsonNode n: node){
						logger.debug("staff node=" + n);
						StaffMap s = mapper.readValue(n.toString(), StaffMap.class);
						list.add(s);
					}
				}	
			}
		}
		
		return list;
	}
	
	private static List<CustomerMap> processCustomer(JsonNode custNode) throws JsonParseException, JsonMappingException, IOException{
		
		logger.info("Process Customer Map");

		List<CustomerMap> list = new ArrayList<CustomerMap>();
		
		if(custNode!=null){
		
			ObjectMapper mapper = new ObjectMapper();
			
			Iterator<JsonNode>itr = custNode.iterator();
			while(itr.hasNext()){
				JsonNode node = itr.next();
				if(node.isArray()){
					for(JsonNode n: node){
						logger.debug("customer node" + n);
						CustomerMap c = mapper.readValue(n.toString(), CustomerMap.class);
						list.add(c);
					}
				}	
			}
			
		}
		
		return list;
		
	}
	
	private static List<FacilityMap> processFacility(JsonNode facNode) throws JsonParseException, JsonMappingException, IOException{
		
		logger.info("Process Facility Map");

		List<FacilityMap> list = new ArrayList<FacilityMap>();
		
		if(facNode!=null){
			
			ObjectMapper mapper = new ObjectMapper();
			
			Iterator<JsonNode> itr = facNode.iterator();
			while(itr.hasNext()){
				JsonNode node = itr.next();
				if(node.isArray()){
					for(JsonNode n: node){
						//logger.debug("node" + n);
						FacilityMap f = mapper.readValue(n.toString(), FacilityMap.class);
						list.add(f);
					}
				}	
			}
		}
		
		return list;
		
	}

}
