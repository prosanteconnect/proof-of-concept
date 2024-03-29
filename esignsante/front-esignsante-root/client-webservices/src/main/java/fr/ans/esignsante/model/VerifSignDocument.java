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
import java.io.File;
import java.io.Serializable;
/**
 * VerifSignDocument
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-07-04T13:37:15.229Z[GMT]")
public class VerifSignDocument implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("idVerifSignConf")
  private Long idVerifSignConf = null;

  @JsonProperty("file")
  private File file = null;

  public VerifSignDocument idVerifSignConf(Long idVerifSignConf) {
    this.idVerifSignConf = idVerifSignConf;
    return this;
  }

   /**
   * Identifiant de configuration à sélectionner parmi la liste des configurations disponibles pour la vérification de signature (appel de l&#x27;opération \&quot;/configurations\&quot;).
   * @return idVerifSignConf
  **/
  @Schema(required = true, description = "Identifiant de configuration à sélectionner parmi la liste des configurations disponibles pour la vérification de signature (appel de l'opération \"/configurations\").")
  public Long getIdVerifSignConf() {
    return idVerifSignConf;
  }

  public void setIdVerifSignConf(Long idVerifSignConf) {
    this.idVerifSignConf = idVerifSignConf;
  }

  public VerifSignDocument file(File file) {
    this.file = file;
    return this;
  }

   /**
   * Document à vérifier.
   * @return file
  **/
  @Schema(required = true, description = "Document à vérifier.")
  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VerifSignDocument verifSignDocument = (VerifSignDocument) o;
    return Objects.equals(this.idVerifSignConf, verifSignDocument.idVerifSignConf) &&
        Objects.equals(this.file, verifSignDocument.file);
  }

  @Override
  public int hashCode() {
    return Objects.hash(idVerifSignConf, Objects.hashCode(file));
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VerifSignDocument {\n");
    
    sb.append("    idVerifSignConf: ").append(toIndentedString(idVerifSignConf)).append("\n");
    sb.append("    file: ").append(toIndentedString(file)).append("\n");
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
