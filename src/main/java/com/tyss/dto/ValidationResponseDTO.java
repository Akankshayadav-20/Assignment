package com.tyss.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationResponseDTO {
 
	private List<ValidationRuleRecordDTO> records;
}
