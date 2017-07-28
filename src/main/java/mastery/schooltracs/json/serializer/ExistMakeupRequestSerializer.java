package mastery.schooltracs.json.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import mastery.schooltracs.model.ExistMakeupRequest;

public class ExistMakeupRequestSerializer extends JsonSerializer<ExistMakeupRequest> {
	
	@Override
	public void serialize(ExistMakeupRequest req, JsonGenerator jg, SerializerProvider sp)
			throws IOException, JsonProcessingException {
				
		/* {
		 * "15 ":
		 * ["ActivityCustomer.makeUp",
		 * "401005",
		 * [482327],
		 * 0,
		 * null]}*/
				
		jg.writeStartObject();
		jg.writeFieldName(Integer.toString(req.getReqSeq()) + " ");
		
		jg.writeStartArray();
		jg.writeString(req.getTask());
		
		jg.writeString(req.getToLsonId());
		
		jg.writeStartArray();
		jg.writeNumber(req.getStdLsonId());
		jg.writeEndArray();
		
		jg.writeNumber(0);
		jg.writeNull();
		
		jg.writeEndArray();
		jg.writeEndObject();
		
	}
	


}
