package mastery.schooltracs.util;

import java.io.IOException;
import java.lang.reflect.Field;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import mastery.schooltracs.model.SearchRequest;

public class SearchRequestSerializer extends JsonSerializer<SearchRequest> {
	
	@Override
	public void serialize(SearchRequest req, JsonGenerator jg, SerializerProvider sp)
			throws IOException, JsonProcessingException {
				
		/*{"6 ":
		 * ["Activity.searchActivities",
		 * "2",
		 * {"customer":true,"facility":true,"staff":true,"product":true},
		 * "s",
		 * ["K2�Ӯa"],
		 * "2017-05-25 00:00:00",
		 * "2017-05-25 00:00:00",
		 * "",
		 * 4,
		 * 50,
		 * "2",
		 * {}]}
		 * */
				
		jg.writeStartObject();
		jg.writeFieldName(Integer.toString(req.getReqSeq()) + " ");
		
		jg.writeStartArray();
		jg.writeString(req.getTask());

		jg.writeString(req.getUnknownStr1());
		
		jg.writeStartObject();
		
		for(Field f: SearchRequest.ContentOpt.class.getDeclaredFields()){
			try {
				f.setAccessible(true);
				jg.writeBooleanField(f.getName(), (Boolean) f.get(req.getContentOpt()));
			} catch (IllegalArgumentException | IllegalAccessException  e) {
				e.printStackTrace();
			} 
		}
	
		jg.writeEndObject();
		
		jg.writeString(req.getDisplayMode());
		
		jg.writeStartArray();
		jg.writeString(req.getSearchStr());
		jg.writeEndArray();
		
		jg.writeString(SchoolTracsConst.SDF.format(req.getFromDate()));
		jg.writeString(SchoolTracsConst.SDF.format(req.getToDate()));
		jg.writeString(req.getTimeslot());
		jg.writeNumber(req.getUnknownInt1());
		jg.writeNumber(req.getUnknownInt2());
		jg.writeString(req.getUnknownStr3());
		
		jg.writeStartObject();
		jg.writeEndObject();
		
		jg.writeEndArray();
		jg.writeEndObject();
		
		
	}
	


}
