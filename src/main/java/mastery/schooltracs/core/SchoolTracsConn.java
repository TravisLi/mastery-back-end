package mastery.schooltracs.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import mastery.model.Lesson;
import mastery.schooltracs.model.NewMakeupRequest;
import mastery.schooltracs.model.SearchRequest;
import mastery.schooltracs.util.NewMakeupRequestSerializer;
import mastery.schooltracs.util.SchoolTracsConst;
import mastery.schooltracs.util.SearchRequestSerializer;

public class SchoolTracsConn {

	private static final Logger logger = LoggerFactory.getLogger(SchoolTracsConn.class);
	
	private HttpClientContext localContext;

	private CookieStore cookieStore;

	private final HttpClient httpClient;
	
	private int reqSeq = 1;

	public SchoolTracsConn() {
		
		/*HttpHost proxy = new HttpHost("judpocproxy.poc.et", 8080);
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		
		httpClient = HttpClientBuilder.create().setRoutePlanner(routePlanner).build();*/
		
		httpClient = HttpClientBuilder.create().build();
		localContext = HttpClientContext.create();
		cookieStore = new BasicCookieStore();
		localContext.setCookieStore(cookieStore);
	}

	public void login(String username, String password) throws IOException{
		
		logger.info("Login to SchoolTracs");
		
		HttpPost post = new HttpPost(SchoolTracsConst.LOGIN_URL);
		
		List<NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("username", username));
		nvps.add(new BasicNameValuePair("password", password));
		post.setEntity(new UrlEncodedFormEntity(nvps));
		HttpResponse r = httpClient.execute(post, localContext);

		logger.info("response status code=" + r.getStatusLine().getStatusCode());
		
		//HttpEntity entity2 = r.getEntity();
		/*List<Cookie> cookies = cookieStore.getCookies();
		for (int i = 0; i < cookies.size(); i++) {
			logger.info("Local cookie: " + cookies.get(i));
		}*/
		
		// do something useful with the response body
		// and ensure it is fully consumed
		//EntityUtils.consume(entity2);

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
	
	public String sendSchReq(String searchStr, Date fromDate, Date toDate, String displayMode, SearchRequest.ContentOpt contentOpt) throws ClientProtocolException, IOException{
		
		SearchRequest req = new SearchRequest(reqSeq,SchoolTracsConst.Task.SEARCH_ACTIVITY.code(),displayMode,searchStr,fromDate,toDate,"",contentOpt);
				
		HttpResponse request = this.excuteClient(prepareHttpJsonPost(SchoolTracsConst.TASK_REQ_URL, buildSchReqJson(req)));

		return EntityUtils.toString(request.getEntity());
				
	}
	
	public String sendNewMkupReq(Lesson l, String stdId) throws ClientProtocolException, UnsupportedEncodingException, JsonProcessingException, IOException{
		
		NewMakeupRequest req = new NewMakeupRequest(l);
		req.setCustomerId(stdId);
		
		HttpResponse request = this.excuteClient(prepareHttpJsonPost(SchoolTracsConst.TASK_REQ_URL, buildNewMakeupReqJson(req)));

		return EntityUtils.toString(request.getEntity());
		
	}
	
	private HttpResponse excuteClient(HttpPost post) throws ClientProtocolException, IOException{
		reqSeq++;
		return httpClient.execute(post, localContext);
	}
	
	private static HttpPost prepareHttpJsonPost(String requestUrl, String payload) throws UnsupportedEncodingException{
		HttpPost post = prepareHttpPost(requestUrl);
		post.setHeader(HttpHeaders.CONTENT_TYPE, SchoolTracsConst.CONTENT_TYPE_JSON);
		//post.setHeader(HttpHeaders.CONTENT_ENCODING, Consts.UTF_8.toString());
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
	
	private static String buildSchReqJson(SearchRequest req) throws JsonProcessingException{
		
		ObjectMapper om = new ObjectMapper();
		//Set pretty printing of json
		om.enable(SerializationFeature.INDENT_OUTPUT);

		SimpleModule module = new SimpleModule();
		module.addSerializer(SearchRequest.class, new SearchRequestSerializer());
		om.registerModule(module);

		String json = om.writeValueAsString(req);
		
		logger.debug(json);
		
		return json;
	}
	
	private static String buildNewMakeupReqJson(NewMakeupRequest req) throws JsonProcessingException{
		
		ObjectMapper om = new ObjectMapper();
		//Set pretty printing of json
		om.enable(SerializationFeature.INDENT_OUTPUT);

		SimpleModule module = new SimpleModule();
		module.addSerializer(NewMakeupRequest.class, new NewMakeupRequestSerializer());
		om.registerModule(module);

		String json = om.writeValueAsString(req);
		
		logger.debug(json);
		
		return json;
	}
		
}
