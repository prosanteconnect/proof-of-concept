/*
 * eSignSante
 * API du composant eSignSante. <br>Ce composant dit de \"signature\" mutualise et homogénéise la mise en oeuvre des besoins autour de la signature. <br>Il permet de signer des documents ainsi que de vérifier la validité d'une signature ou d'un certificat.    <br>
 *
 * OpenAPI spec version: 2.5.0.11
 * Contact: esignsante@asipsante.fr
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package fr.ans.esignsante.model;

import java.util.Objects;
import java.util.Arrays;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
/**
 * contient les informations du token et sa validation
 */
@Schema(description = "contient les informations du token et sa validation")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-07-04T13:37:15.229Z[GMT]")
public class OpenidToken implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("accessToken")
  private String accessToken = null;

  @JsonProperty("introspectionResponse")
  private String introspectionResponse = null;

  @JsonProperty("userInfo")
  private String userInfo = null;
  
  @JsonProperty("apiToken")
  private String apiToken = null;

  public OpenidToken accessToken(String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

   /**
   * Get accessToken
   * @return accessToken
  **/
  @Schema(required = true, description = "")
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public OpenidToken apiToken(String apiToken) {
	    this.apiToken = apiToken;
	    return this;
	  }

	   /**
	   * Get accessToken
	   * @return accessToken
	  **/
	  @Schema(required = true, description = "")
	  public String getApiToken() {
	    return apiToken;
	  }

	  public void setApiToken(String apiToken) {
	    this.apiToken = apiToken;
	  }
  
  
  public OpenidToken introspectionResponse(String introspectionResponse) {
    this.introspectionResponse = introspectionResponse;
    return this;
  }

   /**
   * Get introspectionResponse
   * @return introspectionResponse
  **/
  @Schema(required = true, description = "")
  public String getIntrospectionResponse() {
    return introspectionResponse;
  }

  public void setIntrospectionResponse(String introspectionResponse) {
    this.introspectionResponse = introspectionResponse;
  }

  public OpenidToken userInfo(String userInfo) {
    this.userInfo = userInfo;
    return this;
  }

   /**
   * Get userInfo
   * @return userInfo
  **/
  @Schema(required = true, description = "")
  public String getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(String userInfo) {
    this.userInfo = userInfo;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpenidToken openidToken = (OpenidToken) o;
    return Objects.equals(this.accessToken, openidToken.accessToken) &&
    		Objects.equals(this.apiToken, openidToken.apiToken) &&
        Objects.equals(this.introspectionResponse, openidToken.introspectionResponse) &&
        Objects.equals(this.userInfo, openidToken.userInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessToken,apiToken, introspectionResponse, userInfo);
  }


  @Override
  public String toString() {
	  ObjectMapper mapper = new ObjectMapper();
	  String json = null; 
	  try {
		json = mapper.writeValueAsString(this);
	} catch (JsonProcessingException e) {
		throw new RuntimeException(e);
	}
    return Base64.getEncoder().encodeToString(json.getBytes());
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
