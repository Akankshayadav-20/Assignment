package com.tyss.controller;

import java.util.List;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.tyss.dto.DeployDTO;
import com.tyss.dto.UserDTO;
import com.tyss.dto.ValidationResponseDTO;
import com.tyss.dto.ValidationRuleDetailsDTO;
import com.tyss.service.SalesforceService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

	@Autowired
	private SalesforceService salesforceService;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${salesforce.client.id}")
	private String clientId;

	@Value("${salesforce.client.secret}")
	private String clientSecret;

	@Value("${salesforce.redirect.uri}")
	private String redirectUri;

	@GetMapping("/")
	public String home() {
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String loginPage() {

		return "redirect:/html/login.html";
	}

	@GetMapping("/dashboard")
	public String dashboard() {

		return "forward:/html/dashboard.html";
	}

	@GetMapping("/validation")
	public String validationPage() {

		return "forward:/html/validation.html";
	}

	@GetMapping("/salesforce-login")
	public void login(HttpServletResponse response) throws Exception {

		String encodedredirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
		String url = "https://login.salesforce.com/services/oauth2/authorize"
				+ "?response_type=code"
				+ "&client_id=" + clientId
				+ "&redirect_uri=" + encodedredirectUri;

		System.out.println("Auth URL" + url);
		System.out.println("Redierct uri from config" + redirectUri);
		response.sendRedirect(url);

	}

	@GetMapping("/callback")
	// @ResponseBody
	public String callback(@RequestParam(required=false) String code,
			               @RequestParam(required=false) String error,
			               @RequestParam(required=false) String error_description) {

		
		System.out.println(" Code = " + code);
		System.out.println("Error = " + error);
		System.out.println("Description= " + error_description);
		
		if(error != null) {
			System.out.println("Salesforce Oauth Error = " + error);
			return "redirect:/login";
		}
		
		if(code == null) {
			System.out.println("Authorization code not received ");
			return "redirect:/login";
		}
		
		System.out.println("Authorization code  " + code);

		salesforceService.getAccessToken(code);

		return "redirect:/dashboard";
	}

	@GetMapping("/user")
	@ResponseBody
	public UserDTO getUser() {
		return salesforceService.getCurrentUser();
	}

	@GetMapping("/metadata")
	@ResponseBody
	public ValidationResponseDTO getMetadata() {

		return salesforceService.getValidationRules();
	}

	@GetMapping("/validation-rules")
	@ResponseBody
	public ValidationResponseDTO getValidationRules() {
		return salesforceService.getValidationRules();
	}

	@GetMapping("/rule/{id}")
	@ResponseBody
	public ValidationRuleDetailsDTO getRule(@PathVariable String id) {
		return salesforceService.viewRule(id);
	}

	@GetMapping("/toggle/{id}/{active}")
	@ResponseBody
	public String toggleRule(@PathVariable String id, @PathVariable Boolean active) {

		return salesforceService.toggleValidationRule(id, active);

	}

	@GetMapping("/rule-details")
	public String ruleDetails() {

		return "redirect:/html/rule-details.html";
	}

	@GetMapping("/enable-all")
	@ResponseBody
	public String enableAll() {
		return salesforceService.enableAllRules(true);
	}

	@GetMapping("/disable-all")
	@ResponseBody
	public String disableAll() {
		return salesforceService.enableAllRules(false);
	}

	@PostMapping("/deploy")
	@ResponseBody
	public String deploy(@RequestBody List<DeployDTO> changes) {

		return salesforceService.deployAllRules(changes);

	}

	@GetMapping("/logout")
	@ResponseBody
	public String logout() {
		return salesforceService.logout();

	}

}
