package mastery.schooltracs.json.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import mastery.schooltracs.model.StaffWorkHourRequest;

public class StaffWorkHourRequestSerializer extends JsonSerializer<StaffWorkHourRequest> {
	
	@Override
	public void serialize(StaffWorkHourRequest req, JsonGenerator jg, SerializerProvider sp)
			throws IOException, JsonProcessingException {
				
		/*{
		 * "43 ":
		 * [
		 * "Staff.getWorkingHour",
		 * 102
		 * ]
		 * }*/
				
		jg.writeStartObject();
		jg.writeFieldName(Integer.toString(req.getReqSeq()) + " ");
		
		jg.writeStartArray();
		jg.writeString(req.getTask());
		jg.writeNumber(req.getStaffId());

		jg.writeEndArray();
		jg.writeEndObject();
		
	}
	


}
