package mastery.schooltracs.json.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import mastery.schooltracs.model.NewMakeupRequest;
import mastery.schooltracs.util.SchoolTracsConst;

public class NewMakeupRequestSerializer extends JsonSerializer<NewMakeupRequest> {
	
	@Override
	public void serialize(NewMakeupRequest req, JsonGenerator jg, SerializerProvider sp)
			throws IOException, JsonProcessingException {
				
		/*{
		 * "80 ":[
		 * "ActivityCustomer.newMakeUp",{
		 * "date":"2017-08-07",
		 * "startTime":"10:15",
		 * "endTime":"11:15",
		 * "staffId":"22",
		 * "facilityId":"3",
		 * "productId":"",
		 * "id":412610,
		 * "centerId":"2",
		 * "customerId":[813]}]} */
				
		jg.writeStartObject();
		jg.writeFieldName(Integer.toString(req.getReqSeq()) + " ");
		
		jg.writeStartArray();
		jg.writeString(req.getTask());

		jg.writeStartObject();
		
		jg.writeStringField("date", SchoolTracsConst.SDF_DATE.format(req.getStartTime()));
		jg.writeStringField("startTime", SchoolTracsConst.SDF_TIME.format(req.getStartTime()));
		jg.writeStringField("endTime", SchoolTracsConst.SDF_TIME.format(req.getEndTime()));
		jg.writeStringField("staffId", req.getStaffId());
		jg.writeStringField("facilityId", req.getFacilityId());
		jg.writeStringField("productId", req.getProductId());
		jg.writeNumberField("id", req.getId()!=null?Integer.parseInt(req.getId()):0);
		jg.writeStringField("centerId", req.getCenterId());
		jg.writeArrayFieldStart("customerId");
		jg.writeNumber(req.getCustomerId()!=null?Integer.parseInt(req.getCustomerId()):0);
		jg.writeEndArray();
		jg.writeEndObject();
		jg.writeEndArray();
		jg.writeEndObject();
		
	}
	


}
