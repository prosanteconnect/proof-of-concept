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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
/**
 * Données complémentaires et paramétrables retournées par le service.
 */
@Schema(description = "Données complémentaires et paramétrables retournées par le service.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-07-04T13:37:15.229Z[GMT]")
public class Metadata implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("typeMetadata")
  private String typeMetadata = null;

  @JsonProperty("message")
  private String message = null;

  public Metadata typeMetadata(String typeMetadata) {
    this.typeMetadata = typeMetadata;
    return this;
  }

   /**
   * Get typeMetadata
   * @return typeMetadata
  **/
  @Schema(required = true, description = "")
  public String getTypeMetadata() {
    return typeMetadata;
  }

  public void setTypeMetadata(String typeMetadata) {
    this.typeMetadata = typeMetadata;
  }

  public Metadata message(String message) {
    this.message = message;
    return this;
  }

   /**
   * Get message
   * @return message
  **/
  @Schema(required = true, description = "")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Metadata metadata = (Metadata) o;
    return Objects.equals(this.typeMetadata, metadata.typeMetadata) &&
        Objects.equals(this.message, metadata.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeMetadata, message);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Metadata {\n");
    
    sb.append("    typeMetadata: ").append(toIndentedString(typeMetadata)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("}");
    return sb.toString();
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
