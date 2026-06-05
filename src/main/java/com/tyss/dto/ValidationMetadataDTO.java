package com.tyss.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationMetadataDTO {

	private String description;
	private String errorConditionFormula;
	private String errorMessage;
	private Boolean active;
	private String errorDisplayField;
	
}
