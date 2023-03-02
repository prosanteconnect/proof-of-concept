package fr.ans.psc.dam.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.ans.psc.dam.model.UsefulUserInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Helper {

	// champs du UserInfo
		public static final  String GIVEN_NAME = "given_name";
		public static final String PREFERRED_USERNAME = "preferred_username";
		public static final String SUBJECT_ORGANIZATION = "SubjectOrganization";
		public static final String FAMILY_NAME = "family_name";
	
	private Helper() {
	}
	public static String encodeBase64(String stringToEncode) throws UnsupportedEncodingException {
		return Base64.getEncoder().encodeToString(stringToEncode.getBytes("UTF-8"));
	}

	public static String decodeBase64toString(String stringToDecode) throws UnsupportedEncodingException, IllegalArgumentException{
		byte[] decodedBytes = Base64.getDecoder().decode(stringToDecode);
		return new String(decodedBytes, "UTF-8");
	}
	
	public static String safeUrlDecodeBase64toString(String stringToDecode) throws UnsupportedEncodingException, IllegalArgumentException{
		byte[] decodedBytes = Base64.getUrlDecoder().decode(stringToDecode);
		return new String(decodedBytes, "UTF-8");
	}
	
//	public static byte[] decodeBase64toByteArray(String stringToDecode) throws UnsupportedEncodingException, IllegalArgumentException {
//		return Base64.getDecoder().decode(stringToDecode);
//	}
	
	public static UsefulUserInfo jsonStringToUserInfo(String sUserInfo)
			throws JsonMappingException, JsonProcessingException, UnsupportedEncodingException {
		return new ObjectMapper().readValue(sUserInfo, UsefulUserInfo.class);
	}
	
	public static void parseUserInfo(String userInfo) {
		log.debug("parseUserInfo IN with {}", userInfo);
		ObjectNode node;
//		try {
//			node = new ObjectMapper().readValue(userInfo, ObjectNode.class);
//
//			if (node.has(TOKEN_ACTIVE_FIELD)) {
//				String token_active_field = node.get(TOKEN_ACTIVE_FIELD).asText();
//				i
//
//		} catch (Exception e) {
//			log.error("Erreur technique durant le parse de la reponse d'intropection PSC: champ {} ", e.getMessage());
//			log.debug(e.toString());
//			throwExceptionRequestError("Erreur lors du parse de..", HttpStatus.INTERNAL_SERVER_ERROR);
//		}
		
//		if (status.compareTo(HttpStatus.OK)!=0)  {
//			throwExceptionRequestError(msg,status);
//		}
	}
}
