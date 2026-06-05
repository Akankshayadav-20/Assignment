package com.tyss.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationRuleDTO {
 
	private String id;
	private String validationName;
	private boolean active;
	private String objectName;
}
