package com.bm.rank.secret;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SecretExtAttribute {
	private String secretId;//秘境id
	public SecretExtAttribute() {
		
		// TODO Auto-generated constructor stub
	}
	public String getSecretId() {
		return secretId;
	}

	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}

}
