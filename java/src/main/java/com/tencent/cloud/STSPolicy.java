package com.tencent.cloud;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class STSPolicy {
	
	private List<Scope> scopes = new ArrayList<Scope>();
	
	public STSPolicy() {
		
	}
	
	public void addScope(List<Scope> scopes) {
		if(scopes != null) {
			for(Scope scope : scopes) {
				this.scopes.add(scope);
			}
		}
	}
	
	public void addScope(Scope scope) {
		this.scopes.add(scope);
	}
	
	private JSONObject createElement(Scope scope) {
		JSONObject element = new JSONObject();
		
		JSONArray actions = new JSONArray();
		for(String action : scope.getAction()) {
			actions.put(action);
		}
		element.put("action", actions);
		
		element.put("effect", "allow");
		
		JSONObject principal = new JSONObject();
		JSONArray qcs = new JSONArray();
		qcs.put("*");
		principal.put("qcs", qcs);
		element.put("principal", principal);
		
		JSONArray resources = new JSONArray();
		String region = scope.getRegion();
		String bucket = scope.getBucket();
		int index = bucket.lastIndexOf('-');
		String appid = bucket.substring(index + 1).trim();
		String bucketName = bucket.substring(0, index).trim();
		for(String prefix : scope.getResourcefix()) {
			if(!prefix.startsWith("/")) {
				prefix = '/' + prefix;
			}
			StringBuilder resource = new StringBuilder();
			resource.append("qcs::cos")
			.append(':')
			.append(region)
			.append(':')
			.append("uid/").append(appid)
			.append(':')
			.append("prefix//").append(appid).append('/').append(bucketName)
			.append(prefix);
			
			resources.put(resource);
		}
		element.put("resource", resources);
		
		return element;
	}
	
	@Override
	public String toString() {
		JSONObject policy = new JSONObject();
    	policy.put("version", "2.0");
    	if(scopes.size() > 0) {
    		JSONArray statement = new JSONArray();
    		for(Scope scope : scopes) {
    			statement.put(createElement(scope));
    		}
    		policy.put("statement", statement);
    	}
    	return policy.toString();
	}
}
