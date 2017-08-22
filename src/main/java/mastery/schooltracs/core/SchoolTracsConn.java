package mastery.schooltracs.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import mastery.model.Lesson;
import mastery.schooltracs.json.serializer.ExistMakeupRequestSerializer;
import mastery.schooltracs.json.serializer.NewMakeupRequestSerializer;
import mastery.schooltracs.json.serializer.SearchRequestSerializer;
import mastery.schooltracs.json.serializer.StaffWorkHourRequestSerializer;
import mastery.schooltracs.model.Customer;
import mastery.schooltracs.model.CustomerUpdateRequest;
import mastery.schooltracs.model.ExistMakeupRequest;
import mastery.schooltracs.model.NewMakeupRequest;
import mastery.schooltracs.model.SearchActivityRequest;
import mastery.schooltracs.model.StaffWorkHourRequest;
import mastery.schooltracs.util.SchoolTracsConst;
import mastery.schooltracs.util.SchoolTracsUtil;

@Service
public class SchoolTracsConn {

	private static final Logger logger = LoggerFactory.getLogger(SchoolTracsConn.class);

	private HttpClientContext localContext;

	private CookieStore cookieStore;

	private HttpClient httpClient;

	@Value("${schooltracs.sys.uname}")
	private String uname;

	@Value("${schooltracs.sys.pwd}")
	private String pwd;
	
	private ActionObserver obsver = new ActionObserver();
	
	private int reqSeq = 1;

	public SchoolTracsConn() {
		/*httpClient = HttpClientBuilder.create().build();
		localContext = HttpClientContext.create();
		cookieStore = new BasicCookieStore();
		localContext.setCookieStore(cookieStore);*/
		Thread t = new Thread(obsver);
		t.start();
	}

	@PostConstruct
	public void login() throws IOException{

		logger.info("Login to SchoolTracs");

		httpClient = HttpClientBuilder.create().build();
		localContext = HttpClientContext.create();
		cookieStore = new BasicCookieStore();
		localContext.setCookieStore(cookieStore);
		
		HttpPost post = new HttpPost(SchoolTracsConst.LOGIN_URL);

		List<NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("username", uname));
		nvps.add(new BasicNameValuePair("password", pwd));
		post.setEntity(new UrlEncodedFormEntity(nvps));
		HttpResponse r = this.excuteClient(post);
		reqSeq = 1;
		logger.info("response status code=" + r.getStatusLine().getStatusCode());

	}
	
	public String sendStfReq() throws ClientProtocolException, UnsupportedEncodingException, IOException{
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("start", "0"));
		//nvps.add(new BasicNameValuePair("limit", "50"));
		nvps.add(new BasicNameValuePair("centerId", "2"));
		nvps.add(new BasicNameValuePair("deleted", "0"));
		
		return this.sendNvpReq(nvps, SchoolTracsConst.STAFF_REQ_URL);
	}
	
	public String sendActFacReq(String actId) throws ClientProtocolException, UnsupportedEncodingException, IOException{
		logger.info("Send Activities Facility Request");

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("activityId", actId));
		nvps.add(new BasicNameValuePair("deleted", "0"));
		
		return this.sendNvpReq(nvps, SchoolTracsConst.ACT_FAC_REQ_URL);
	}
	
	public String sendActStfReq(String actId) throws ClientProtocolException, UnsupportedEncodingException, IOException{
		logger.info("Send Activities Staff Request");

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("activityId", actId));
		nvps.add(new BasicNameValuePair("deleted", "0"));
		
		return this.sendNvpReq(nvps, SchoolTracsConst.ACT_STAFF_REQ_URL);
	}
	
	public String sendActReq(String actId) throws ClientProtocolException, UnsupportedEncodingException, IOException{
		logger.info("Send Activity Request");

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("id", actId));
		nvps.add(new BasicNameValuePair("deleted", "0"));
		
		return this.sendNvpReq(nvps, SchoolTracsConst.ACT_REQ_URL);
	}
	
	public String sendStfReq(List<NameValuePair> nvps) throws ClientProtocolException, UnsupportedEncodingException, IOException{
		logger.info("Send Staff Request");

		return this.sendNvpReq(nvps, SchoolTracsConst.STAFF_REQ_URL);

	}
	
	public String sendCustReq(List<NameValuePair> nvps) throws ClientProtocolException, UnsupportedEncodingException, IOException{
		logger.info("Send Customer Request");

		return this.sendNvpReq(nvps, SchoolTracsConst.CUST_REQ_URL);

	}
	
	public String sendFacReq() throws ClientProtocolException, IOException{

		logger.info("Send Facility Request");

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("start", "0"));
		nvps.add(new BasicNameValuePair("limit", "50"));
		nvps.add(new BasicNameValuePair("centerId", "2"));
		nvps.add(new BasicNameValuePair("deleted", "0"));

		HttpResponse request = this.excuteClient(prepareHttpFormPost(SchoolTracsConst.FAC_REQ_URL, nvps));

		return EntityUtils.toString(request.getEntity());

	}

	public String sendSchReq(String searchStr, Date fromDate, Date toDate, String displayMode, SearchActivityRequest.ContentOpt contentOpt) throws ClientProtocolException, IOException{

		SearchActivityRequest req = new SearchActivityRequest(reqSeq,SchoolTracsConst.Task.SEARCH_ACTIVITY.code(),displayMode,searchStr,fromDate,toDate,"",contentOpt);

		HttpResponse request = this.excuteClient(prepareHttpJsonPost(SchoolTracsConst.TASK_REQ_URL, buildSchReqJson(req)));

		return EntityUtils.toString(request.getEntity());

	}

	public String sendNewMkupReq(Lesson l, String stdId, Boolean ignResrc) throws ClientProtocolException, UnsupportedEncodingException, JsonProcessingException, IOException{

		NewMakeupRequest req = new NewMakeupRequest(reqSeq,l,stdId);
		if(ignResrc){
			req.setIgnResrc(true);
		}

		HttpResponse request = this.excuteClient(prepareHttpJsonPost(SchoolTracsConst.TASK_REQ_URL, buildNewMkupReqJson(req)));

		return EntityUtils.toString(request.getEntity());

	}
		
	public String sendExtMkupReq(String toLsonId, String stdLsonId) throws ClientProtocolException, UnsupportedEncodingException, JsonProcessingException, IOException{

		ExistMakeupRequest req = new ExistMakeupRequest(reqSeq,toLsonId,stdLsonId);

		HttpResponse request = this.excuteClient(prepareHttpJsonPost(SchoolTracsConst.TASK_REQ_URL, buildExtMkupReqJson(req)));

		return EntityUtils.toString(request.getEntity());

	}
	
	public String sendStfWkhrReq(String stfId) throws ClientProtocolException, UnsupportedEncodingException, JsonProcessingException, IOException{

		StaffWorkHourRequest req = new StaffWorkHourRequest(reqSeq,stfId);

		HttpResponse request = this.excuteClient(prepareHttpJsonPost(SchoolTracsConst.TASK_REQ_URL, buildStfWkhrReqJson(req)));

		return EntityUtils.toString(request.getEntity());

	}
	
	public String sendCustUpdReq(Customer cust) throws ClientProtocolException, UnsupportedEncodingException, JsonProcessingException, IOException{
		
		CustomerUpdateRequest req = new CustomerUpdateRequest(cust);
		
		HttpResponse request = this.excuteClient(prepareHttpJsonPost(SchoolTracsConst.CUST_UPD_URL, buildReqJson(req)));
		
		return EntityUtils.toString(request.getEntity());
	}
	
	private String sendNvpReq(List<NameValuePair> nvps, String url) throws ClientProtocolException, UnsupportedEncodingException, IOException{
		
		HttpResponse request = this.excuteClient(prepareHttpFormPost(url, nvps));

		return EntityUtils.toString(request.getEntity());
		
	}

	private HttpResponse excuteClient(HttpPost post) throws ClientProtocolException, IOException{
		reqSeq++;
		this.obsver.actionPerformed();
		return httpClient.execute(post, localContext);
	}
	
	private class ActionObserver implements Runnable {

		private Date lastActTime = new Date();
		
		@Override
		public void run() {
			while(true){
				Date runTime = new Date();
				Integer min = SchoolTracsUtil.minDiffBtwDates(runTime, lastActTime);
				logger.info("lastActTime=" + lastActTime);
				logger.info("runTime=" + runTime);
				logger.info("min diff=" + min);
				try {
					if(min>=60){
						login();
					}else{
						if(StringUtils.isEmpty(sendFacReq())){
							logger.info("No response from schooltracs - relogin");
							login();
						}
					}
				} catch (IOException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
				try {
					//sleep for 10 mins
					Thread.sleep(1000*60*10);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		public void actionPerformed(){
			this.lastActTime = new Date();
		}

	}

	private static HttpPost prepareHttpJsonPost(String requestUrl, String payload) throws UnsupportedEncodingException{
		HttpPost post = prepareHttpPost(requestUrl);
		post.setHeader(HttpHeaders.CONTENT_TYPE, SchoolTracsConst.CONTENT_TYPE_JSON);
		post.setEntity(new StringEntity(payload, Consts.UTF_8));
		return post;
	}

	private static HttpPost prepareHttpFormPost(String requestUrl, List <NameValuePair> nvps) throws UnsupportedEncodingException{
		HttpPost post = prepareHttpPost(requestUrl);
		post.setHeader(HttpHeaders.CONTENT_TYPE, SchoolTracsConst.CONTENT_TYPE_FORM);
		post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		return post;
	}

	private static HttpPost prepareHttpPost(String requestUrl){
		HttpPost post = new HttpPost(requestUrl);
		post.setHeader(HttpHeaders.HOST, SchoolTracsConst.HOST);
		post.setHeader(HttpHeaders.USER_AGENT, SchoolTracsConst.USER_AGENT);
		post.setHeader(HttpHeaders.ACCEPT_ENCODING, SchoolTracsConst.ACCEPT_ENCODING);
		post.setHeader(HttpHeaders.CONNECTION, SchoolTracsConst.CONNECTION);
		return post;
	}

	private static String buildSchReqJson(SearchActivityRequest req) throws JsonProcessingException{

		SimpleModule module = new SimpleModule();
		module.addSerializer(SearchActivityRequest.class, new SearchRequestSerializer());

		return buildReqJson(module,req);
	}

	private static String buildNewMkupReqJson(NewMakeupRequest req) throws JsonProcessingException{

		SimpleModule module = new SimpleModule();
		module.addSerializer(NewMakeupRequest.class, new NewMakeupRequestSerializer());

		return buildReqJson(module,req);
	}

	private static String buildExtMkupReqJson(ExistMakeupRequest req) throws JsonProcessingException{

		SimpleModule module = new SimpleModule();
		module.addSerializer(ExistMakeupRequest.class, new ExistMakeupRequestSerializer());

		return buildReqJson(module,req);
	}
	
	private static String buildStfWkhrReqJson(StaffWorkHourRequest req) throws JsonProcessingException{

		SimpleModule module = new SimpleModule();
		module.addSerializer(StaffWorkHourRequest.class, new StaffWorkHourRequestSerializer());

		return buildReqJson(module,req);
	}
	
	private static String buildReqJson(SimpleModule module, Object req) throws JsonProcessingException{

		ObjectMapper om = SchoolTracsUtil.getObjMapper();
		om.registerModule(module);

		String json = om.writeValueAsString(req);

		logger.debug(json);

		return json;
	}
	
	private static String buildReqJson(Object req) throws JsonProcessingException{

		ObjectMapper om = SchoolTracsUtil.getObjMapper();

		String json = om.writeValueAsString(req);

		logger.debug(json);

		return json;
	}
	
}
