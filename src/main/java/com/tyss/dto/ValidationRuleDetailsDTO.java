package com.tyss.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationRuleDetailsDTO {
     
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("ValidationName")
	private String validationName;
	
	@JsonProperty("Active")
	private Boolean active;
	
	@JsonProperty("FullName")
	private String fullName;
	
	@JsonProperty("Description")
	private String description;
	
	@JsonProperty("Metadata")
	private ValidationMetadataDTO metadata;
}
