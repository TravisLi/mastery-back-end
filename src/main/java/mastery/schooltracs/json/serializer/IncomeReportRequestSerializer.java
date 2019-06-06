package mastery.schooltracs.json.serializer;

import java.io.IOException;
import java.lang.reflect.Field;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import mastery.schooltracs.model.IncomeReportRequest;

public class IncomeReportRequestSerializer extends JsonSerializer<IncomeReportRequest> {

	@Override
	public void serialize(IncomeReportRequest req, JsonGenerator jg, SerializerProvider sp)
			throws IOException, JsonProcessingException {

		/*
		 * {"14 ":["Income_Report.getData",{"centerId":3,"start":"2019-05-23 00:00:00","end":"2019-05-24 23:59:00"}],
		 * "15 ":["Income_Report.getData",{"centerId":3,"start":"2019-05-23 00:00:00","end":"2019-05-24 23:59:00"}]}
		 */

		jg.writeStartObject();                                                                                                                                                                                       

		jg.writeFieldName(Integer.toString(req.getReqSeq()) + " ");

		jg.writeStartArray();
		jg.writeString(req.getTask());

		jg.writeStartObject();

		for(Field f: IncomeReportRequest.IncomeRptOpt.class.getDeclaredFields()){
			try {
				f.setAccessible(true);
				if(f.getType()==Integer.class){
					jg.writeNumberField(f.getName(), (Integer) f.get(req.getIncomeRptOpt()));
				}else if(f.getType()==String.class){
					jg.writeStringField(f.getName(), (String) f.get(req.getIncomeRptOpt()));
				}

			} catch (IllegalArgumentException | IllegalAccessException  e) {
				e.printStackTrace();
			} 
		}

		jg.writeEndObject();
		jg.writeEndArray();



		jg.writeEndObject();

	}

}
