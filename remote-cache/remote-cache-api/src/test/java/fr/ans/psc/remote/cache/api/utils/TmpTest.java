package fr.ans.psc.remote.cache.api.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;


/*
 * Check validity of JSON-SCHEMA
 */
@Slf4j
public class TmpTest {

    @Test
    @DisplayName("hash ")
    public void calculDuHash() {
    	String hash = org.apache.commons.codec.digest.DigestUtils.sha256Hex("Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKRlRBM1llVVdQbERCTEJfeU5qWUs0bWZJcTdhYXBBS21ieVdyczRPZ0RnIn0.eyJleHAiOjE2Nzc1MTA1MjYsImlhdCI6MTY3NzUxMDQwNiwiYXV0aF90aW1lIjoxNjc3NTA5OTI4LCJqdGkiOiJjNzIwOTJkMC0yYzA1LTRjM2QtOWY5My03MmE5NmIyODY3YTQiLCJpc3MiOiJodHRwczovL2F1dGguYmFzLnBzYy5lc2FudGUuZ291di5mci9hdXRoL3JlYWxtcy9lc2FudGUtd2FsbGV0Iiwic3ViIjoiZjo1NTBkYzFjOC1kOTdiLTRiMWUtYWM4Yy04ZWI0NDcxY2Y5ZGQ6ODk5NzAwNDI3ODg1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYW5zLXBvYy1lc2lnbnNhbnRlLWJhcyIsIm5vbmNlIjoiIiwic2Vzc2lvbl9zdGF0ZSI6ImY3NWZlOTczLWQ4OGMtNDQ4Ny1hOWY5LTYwOTk3MzczNDRjMiIsImFjciI6ImVpZGFzMyIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgc2NvcGVfYWxsIGlkZW50aXR5Iiwic2lkIjoiZjc1ZmU5NzMtZDg4Yy00NDg3LWE5ZjktNjA5OTczNzM0NGMyIiwiYXV0aE1vZGUiOiJDQVJEIiwib3RoZXJJZHMiOlt7ImlkZW50aWZpYW50IjoiODk5NzAwNDI3ODg1Iiwib3JpZ2luZSI6IlJQUFMiLCJxdWFsaXRlIjoxfV0sInByZWZlcnJlZF91c2VybmFtZSI6Ijg5OTcwMDQyNzg4NSJ9.PO1vO1JaCdOu0-3OwftOAIXutChAV45HEOOv38NSf001M4_eOMsr-DJKkFKZj8sk-XVIVUu1O_Cv1Dp3DpikW6R8VCzTS1IX2XA3_OV4OkbimL5QcGLIGrXjyC1VlJ-vEEgcjUPpt6YqFMp6JJ5mPd8VF2g3byQfZwA9J_hKo9MVMBDTJSEH9mROkp3tJ5rQxCG0q2lKO5njn-c9KSaFoInLvYJA4Sb2sFHArxZRh24NzGn_nSaJjHJJB3kKP82rlf9o3aStinUHhS9U5ztAWctlzqUQvChOwM1Vv6I56Y1-vSVrNkQlyVCsE0qAha3Bs--QYJFr237jjmBC0ktjcQ");
    	System.out.println(hash);
    	assertEquals(hash, "3139e5e375d06392df0692251db317237888f57537d9fdfcf1dc7fa7446f4092");
    	
    hash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(
    		"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKRlRBM1llVVdQbERCTEJfeU5qWUs0bWZJcTdhYXBBS21ieVdyczRPZ0RnIn0.eyJleHAiOjE2Nzc1NzQzNTEsImlhdCI6MTY3NzU3NDIzMSwiYXV0aF90aW1lIjoxNjc3NTcwNzYyLCJqdGkiOiJjOTQwZDg3YS1iMGMyLTQ4NmQtODZmZi1hN2U1ZWQ4ZGMxYmYiLCJpc3MiOiJodHRwczovL2F1dGguYmFzLnBzYy5lc2FudGUuZ291di5mci9hdXRoL3JlYWxtcy9lc2FudGUtd2FsbGV0Iiwic3ViIjoiZjo1NTBkYzFjOC1kOTdiLTRiMWUtYWM4Yy04ZWI0NDcxY2Y5ZGQ6ODk5NzAwNDI3ODg1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYW5zLXBvYy1lc2lnbnNhbnRlLWJhcyIsIm5vbmNlIjoiIiwic2Vzc2lvbl9zdGF0ZSI6IjI5ZmRlZmYxLTNjYmItNGMwZi05NzkwLTY5MWNhZWJjZDdmOCIsImFjciI6ImVpZGFzMyIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgc2NvcGVfYWxsIGlkZW50aXR5Iiwic2lkIjoiMjlmZGVmZjEtM2NiYi00YzBmLTk3OTAtNjkxY2FlYmNkN2Y4IiwiYXV0aE1vZGUiOiJDQVJEIiwib3RoZXJJZHMiOlt7ImlkZW50aWZpYW50IjoiODk5NzAwNDI3ODg1Iiwib3JpZ2luZSI6IlJQUFMiLCJxdWFsaXRlIjoxfV0sInByZWZlcnJlZF91c2VybmFtZSI6Ijg5OTcwMDQyNzg4NSJ9.utNkwAL0xQm6T3I2pSeWa4gLOo2UHCPGozjTLwEXbDxILsderQ4nru3DRRyThGKt50zc7iNRjI9u8AwTbUYntTM5PP0Q5QwjV7Na4yHGM8bsB6OcJcJGNpfmunOJG7xPtrB9wRr4jgvrkVP3QbACt8w610c7LIHziDvLfY1NJ4cLoW0GiSm6kXcM718kENUfUk8x3Xyfwh223ch70CofmGEp2GkpZLVxZkJelqWDKDCGJVIo59SGmcXwie9TakUbXmBdOWBlb2Anf0CEXeuOyjBtfaH_GlcNcD-kthmv4OrC3pMMEsANuT2ihxJlPDd7hwFyJl5l98pRKKJZ_bNLRQ");
    System.out.println(hash);
	assertEquals(hash, "b76c4537eb383f2164a7561039238c38378637b860ccdf06bee7d9994f5b2ab3");
	
    }
   
  //groovy Gravitee:   context.attributes.'json' = '{"PscAccessToken" : "' + request.headers.'Authorization'[0] + '", "IntrospectionResponse": ' + context.attributes.'oauth.payload' + ', "UserInfo": ' + context.attributes.'openid.userinfo.payload' + '}'
 //
    
    
    @Test
    @DisplayName("parsing psc Data ")
    public void parse() {
    	//{"PscAccessToken" : "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKRlRBM1llVVdQbERCTEJfeU5qWUs0bWZJcTdhYXBBS21ieVdyczRPZ0RnIn0.eyJleHAiOjE2Nzc1MTgzNjgsImlhdCI6MTY3NzUxODI0OCwiYXV0aF90aW1lIjoxNjc3NTEyODUzLCJqdGkiOiJhZTEwNTc0Zi0zMzFmLTRiOWMtOGZkYS00ZWQ5MGYwOTBmNWIiLCJpc3MiOiJodHRwczovL2F1dGguYmFzLnBzYy5lc2FudGUuZ291di5mci9hdXRoL3JlYWxtcy9lc2FudGUtd2FsbGV0Iiwic3ViIjoiZjo1NTBkYzFjOC1kOTdiLTRiMWUtYWM4Yy04ZWI0NDcxY2Y5ZGQ6ODk5NzAwNDI3ODg1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYW5zLXBvYy1lc2lnbnNhbnRlLWJhcyIsIm5vbmNlIjoiIiwic2Vzc2lvbl9zdGF0ZSI6IjI4NTZjZTQyLTAzY2YtNGM5OC05MjEyLTNmNjc2OTE5ZTczMCIsImFjciI6ImVpZGFzMyIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgc2NvcGVfYWxsIGlkZW50aXR5Iiwic2lkIjoiMjg1NmNlNDItMDNjZi00Yzk4LTkyMTItM2Y2NzY5MTllNzMwIiwiYXV0aE1vZGUiOiJDQVJEIiwib3RoZXJJZHMiOlt7ImlkZW50aWZpYW50IjoiODk5NzAwNDI3ODg1Iiwib3JpZ2luZSI6IlJQUFMiLCJxdWFsaXRlIjoxfV0sInByZWZlcnJlZF91c2VybmFtZSI6Ijg5OTcwMDQyNzg4NSJ9.IjgS92V9wlYRiLAYK9SRXRx69WwAHTlHhkTFUyWtI0zzv_3ydMiu32v0cFd2s2Qo6OrEhFCCHy4mS6txmnbCuy6cA0h7CkzSy7qVucCfKC5YWE13sL_fhFsiQ6LeW3D18lFb8LaA0qw_r48YlWBsDHuxsFSDxm5bfQSG2PW58R9tWtYnOamQQtEU-G0tkbolyFedA4kxWQIWYIPK4AFLVWesTEYi5sY4tfboAGK8Z92Ia9eEb8Ose0A9Di6RG9lMXutwzfFZL43ktHA1tV2C3vXj6sS8XFZ4pptlL5ubpwNLOVo298JePixreXvOMtG-yKBmYPM3p3-Dd1g6pNUZiA", "IntrospectionResponse": {"exp":1677518368,"iat":1677518248,"auth_time":1677512853,"jti":"ae10574f-331f-4b9c-8fda-4ed90f090f5b","iss":"https://auth.bas.psc.esante.gouv.fr/auth/realms/esante-wallet","sub":"f:550dc1c8-d97b-4b1e-ac8c-8eb4471cf9dd:899700427885","typ":"Bearer","azp":"ans-poc-esignsante-bas","nonce":"","session_state":"2856ce42-03cf-4c98-9212-3f676919e730","preferred_username":"899700427885","acr":"eidas3","scope":"openid profile scope_all identity","sid":"2856ce42-03cf-4c98-9212-3f676919e730","authMode":"CARD","otherIds":[{"identifiant":"899700427885","origine":"RPPS","qualite":1}],"client_id":"ans-poc-esignsante-bas","username":"899700427885","active":true}, "UserInfo": {"Secteur_Activite":"SA07^1.2.250.1.71.4.2.4","sub":"f:550dc1c8-d97b-4b1e-ac8c-8eb4471cf9dd:899700427885","SubjectOrganization":"CABINET M DOC0042788","Mode_Acces_Raison":"","preferred_username":"899700427885","given_name":"KIT","Acces_Regulation_Medicale":"FAUX","UITVersion":"1.0","authMode":"CARD","Palier_Authentification":"APPPRIP3^1.2.250.1.213.1.5.1.1.1","SubjectRefPro":{"codeCivilite":"M","exercices":[{"codeProfession":"10","codeCategorieProfessionnelle":"C","codeCiviliteDexercice":"DR","nomDexercice":"DOC0042788","prenomDexercice":"KIT","codeTypeSavoirFaire":"S","codeSavoirFaire":"SM26","activities":[{"codeModeExercice":"L","codeSecteurDactivite":"SA07","codeSectionPharmacien":"","codeRole":"","codeGenreActivite":"","numeroSiretSite":"","numeroSirenSite":"","numeroFinessSite":"","numeroFinessetablissementJuridique":"","identifiantTechniqueDeLaStructure":"R102671","raisonSocialeSite":"CABINET M DOC0042788","enseigneCommercialeSite":"","complementDestinataire":"CABINET M DOC","complementPointGeographique":"","numeroVoie":"1","indiceRepetitionVoie":"","codeTypeDeVoie":"R","libelleVoie":"NOIR","mentionDistribution":"","bureauCedex":"75009 PARIS","codePostal":"75009","codeCommune":"","codePays":"99000","telephone":"","telephone2":"","telecopie":"","adresseEMail":"","codeDepartement":"","ancienIdentifiantDeLaStructure":"499700427885006","autoriteDenregistrement":"CNOM/CNOM/CNOM"},{"codeModeExercice":"S","codeSecteurDactivite":"SA01","codeSectionPharmacien":"","codeRole":"","codeGenreActivite":"","numeroSiretSite":"","numeroSirenSite":"","numeroFinessSite":"0B0193488","numeroFinessetablissementJuridique":"1B0064166","identifiantTechniqueDeLaStructure":"F0B0193488","raisonSocialeSite":"HOPITAL GENERIQUE FIN VARI","enseigneCommercialeSite":"","complementDestinataire":"","complementPointGeographique":"","numeroVoie":"10","indiceRepetitionVoie":"","codeTypeDeVoie":"R","libelleVoie":"DE PARIS","mentionDistribution":"","bureauCedex":"PARIS","codePostal":"75009","codeCommune":"","codePays":"","telephone":"","telephone2":"","telecopie":"","adresseEMail":"","codeDepartement":"","ancienIdentifiantDeLaStructure":"10B0193488","autoriteDenregistrement":"CNOM/CNOM/ARS"}]}]},"SubjectOrganizationID":"R102671","SubjectRole":["10^1.2.250.1.213.1.1.5.5"],"PSI_Locale":"1.2.250.1.213.1.3.1.1","otherIds":[{"identifiant":"899700427885","origine":"RPPS","qualite":1}],"SubjectNameID":"899700427885","family_name":"DOC0042788"}}
    } 
}
