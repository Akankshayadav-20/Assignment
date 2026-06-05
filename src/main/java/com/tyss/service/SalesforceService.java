package com.tyss.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tyss.dto.DeployDTO;
import com.tyss.dto.TokenDTO;
import com.tyss.dto.UserDTO;
import com.tyss.dto.ValidationResponseDTO;
import com.tyss.dto.ValidationRuleDetailsDTO;
import com.tyss.dto.ValidationRuleRecordDTO;

@Service
public class SalesforceService {


	@Autowired
	private RestTemplate restTemplate;
	
	private TokenDTO currentToken;
	
	private UserDTO currentUser;
	
	@Value("${salesforce.client.id}")
	private String clientId;
	
	@Value("${salesforce.client.secret}")
	private String clientSecret;
	
	@Value("${salesforce.redirect.uri}")
	private String redirectUri;
	
	public UserDTO getCurrentUser() {
		return currentUser;
	}
	
	private HttpHeaders getHeaders() {
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.setBearerAuth(currentToken.getAccess_token());
		
		return headers;
	}
	
	private <T> T getRequest(String url, Class<T> responseType) {
		
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		
		return restTemplate.exchange(url,
				            HttpMethod.GET,
	                        request,
	                        responseType).getBody();
	}
	
	private void patchRequest(String url, String body) {
		
		HttpHeaders headers = getHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept" , "application/json");
		
		System.out.println("Headers" + headers);
		
		HttpEntity<String> request = new HttpEntity<>(body, headers);
		
		restTemplate.exchange(url,
	            HttpMethod.PATCH,
                request,
                String.class);
	}
	public String getAccessToken(String code) {
		
		try {
			
			String tokenUrl="https://login.salesforce.com/services/oauth2/token";
		    
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			
			MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
			
			body.add("grant_type", "authorization_code");
			body.add("client_id", clientId);
			body.add("client_secret", clientSecret);
			body.add("redirect_uri", redirectUri);
			body.add("code", code);
//		    System.out.println("Authorization code " + code);
//		
//		    System.out.println("Client Id" + clientId);
			
	  		HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(body ,headers);
		    
			TokenDTO token = restTemplate.postForObject(
					         tokenUrl,
					         request,
					         TokenDTO.class);
			
			this.currentToken = token;
			
			System.out.println("Access Token " +token.getAccess_token());
			System.out.println("Instance URL" + token.getInstance_url());
			System.out.println("Identity URL"+ token.getId());
			
			HttpHeaders userHeaders = new HttpHeaders();
			userHeaders.setBearerAuth(token.getAccess_token());
			
			HttpEntity<String> userRequest= new HttpEntity<>(userHeaders);
			
			ResponseEntity<UserDTO> userResponse = restTemplate.exchange(token.getId(),
					                                                      HttpMethod.GET,
					                                                      userRequest,
					                                                      UserDTO.class);
			UserDTO user = userResponse.getBody();
			this.currentUser = user;
			
			System.out.println("Username: " + user.getUsername());
			System.out.println("Organization : " + user.getOrganization_id());
			System.out.println("User Id :" + user.getUser_id());
			
		   return "Token Generated Successfully";
		
		} catch(Exception e) {
			e.printStackTrace();
			
			return "Unable to login to Salesforce" + e.getMessage();
		}
	}
	
	public ValidationResponseDTO getValidationRules() {
		
		try {
			
			System.out.println("CurrentToken =" + currentToken);
			
			String query = "SELECT Id, ValidationName, Active FROM ValidationRule";
			
			String url = currentToken.getInstance_url()
					     + "/services/data/v63.0/tooling/query?q="
					     + query.replace(" ", "+");

			ValidationResponseDTO response = getRequest(url, ValidationResponseDTO.class);
			
			System.out.println(response);
			
			return response;
		} catch(Exception e) {
			 throw new RuntimeException("Unable to fetch validation rules" , e);
		}
	}
	
	public String toggleValidationRule(String id, Boolean active) {
		
		try {
			String url = currentToken.getInstance_url()
					     + "/services/data/v63.0/tooling/sobjects/ValidationRule/" + id;
			
			boolean newStatus = !active;
			
			
			ValidationRuleDetailsDTO existingRule = getRequest(url,ValidationRuleDetailsDTO.class);
			
			System.out.println("Rule = " + existingRule);
			System.out.println("Metadata = " + existingRule.getMetadata());
			System.out.println("FullName = " + existingRule.getFullName());
			System.out.println("Description = " + existingRule.getMetadata().getDescription());
			System.out.println("Formula = " + existingRule.getMetadata().getErrorConditionFormula());
			System.out.println("Error Message = " + existingRule.getMetadata().getErrorMessage());
			
			
			ObjectMapper mapper = new ObjectMapper();
			
			ObjectNode metadataNode = mapper.createObjectNode();
			
			metadataNode.put("active" , newStatus);
			
			metadataNode.put("errorConditionFormula", existingRule.getMetadata().getErrorConditionFormula());
			metadataNode.put("description",existingRule.getMetadata().getDescription());
			metadataNode.put("errorMessage",existingRule.getMetadata().getErrorMessage());
			metadataNode.put("errorDisplayField", existingRule.getMetadata().getErrorDisplayField());
//			metadataNode.put("errorLocation" , "TopOfPage");
		
			ObjectNode bodyNode = mapper.createObjectNode();
			bodyNode.put("FullName" , existingRule.getFullName());
			bodyNode.set("Metadata", metadataNode);
			
			String body = mapper.writeValueAsString(bodyNode);
			
			System.out.println("Body" + body);
			
			patchRequest(url, body);
			
			return newStatus ? "Validation Rule Enabled Successfully"
					           :"validation Rules Disable Successfully";
		}catch(Exception e) {
			e.printStackTrace();
			return "Unable to disable Validation Rule : " + e.getMessage();
		}
		
	}
	  
	public ValidationRuleDetailsDTO viewRule(String id) {
		
		try {
			
			String url = currentToken.getInstance_url()
					 + "/services/data/v63.0/tooling/sobjects/ValidationRule/" + id;
			
			
			ValidationRuleDetailsDTO rule = getRequest(url,ValidationRuleDetailsDTO.class);
			
			
			System.out.println("Metadata :" + rule.getMetadata());
			System.out.println(rule.getMetadata().getErrorConditionFormula());
			System.out.println("Description : " + rule.getMetadata().getDescription());
			System.out.println("RuleName" + rule.getValidationName());
			System.out.println("Active :" + rule.getActive());
		
			
			return rule;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String enableAllRules(boolean status) {
		
		ValidationResponseDTO response = getValidationRules();
		
		for(ValidationRuleRecordDTO rule : response.getRecords()) {
			
			if(rule.getActive() != status) {
				
				 toggleValidationRule(rule.getId(), !status);
			}
			
		}
		
		return status ? "All Rules Enable Successfully"
				       :"All Rules Disable Successfully";
	}
	
	public String deployAllRules(List<DeployDTO> changes) {
	     
		for(DeployDTO rule : changes) {
			
			toggleValidationRule(rule.getId(), rule.getActive());
		}
		return "All changes deployed to salesforce successfully";
	}
	
	public String logout() {
		 this.currentToken = null;
		 this.currentUser = null;
		 return "Logged out successfully";
	}
}
