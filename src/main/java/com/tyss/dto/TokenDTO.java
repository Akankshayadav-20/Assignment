package com.tyss.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDTO {

	private String access_token;
	private String instance_url;
	private String token_type;
	private String id;
}
