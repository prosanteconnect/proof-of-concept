/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.ws.api.delegate;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DigestDocument;
import fr.asipsante.api.sign.bean.errors.ErreurSignature;
import fr.asipsante.api.sign.bean.metadata.MetaDatum;
import fr.asipsante.api.sign.bean.parameters.FSESignatureParameters;
import fr.asipsante.api.sign.bean.parameters.ProofParameters;
import fr.asipsante.api.sign.bean.parameters.SignatureParameters;
import fr.asipsante.api.sign.bean.parameters.SignatureValidationParameters;
import fr.asipsante.api.sign.bean.proof.OpenIdTokenBean;
import fr.asipsante.api.sign.bean.rapports.RapportSignature;
import fr.asipsante.api.sign.bean.rapports.RapportValidationSignature;
import fr.asipsante.api.sign.enums.MetaDataType;
import fr.asipsante.api.sign.service.ICACRLService;
import fr.asipsante.api.sign.service.IProofGenerationService;
import fr.asipsante.api.sign.service.ISignatureService;
import fr.asipsante.api.sign.service.ISignatureValidationService;
import fr.asipsante.api.sign.service.impl.utils.Version;
import fr.asipsante.api.sign.utils.AsipSignClientException;
import fr.asipsante.api.sign.utils.AsipSignException;
import fr.asipsante.api.sign.utils.AsipSignServerException;
import fr.asipsante.api.sign.ws.api.SignaturesApiDelegate;
import fr.asipsante.api.sign.ws.bean.config.IGlobalConf;
import fr.asipsante.api.sign.ws.bean.object.ProofConf;
import fr.asipsante.api.sign.ws.bean.object.SignVerifConf;
import fr.asipsante.api.sign.ws.bean.object.SignatureConf;
import fr.asipsante.api.sign.ws.model.ESignSanteSignatureReport;
import fr.asipsante.api.sign.ws.model.ESignSanteSignatureReportWithProof;
import fr.asipsante.api.sign.ws.model.Erreur;
import fr.asipsante.api.sign.ws.model.Metadata;
import fr.asipsante.api.sign.ws.model.OpenidToken;
import fr.asipsante.api.sign.ws.model.SignFSEWithProof;
import fr.asipsante.api.sign.ws.util.ESignatureType;
import fr.asipsante.api.sign.ws.util.SignWsUtils;
import fr.asipsante.api.sign.ws.util.WsVars;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class SignaturesApiDelegateImpl.
 */
@Service
public class SignaturesApiDelegateImpl extends ApiDelegate implements SignaturesApiDelegate {

	/** Default ESignSante major version. */
	private static final int MAJOR = 2;

	/** Default ESignSante version. */
	private static final Version DEFAULT_VERSION = new Version(MAJOR, 5, 0, 11);

	/**
	 * The log.
	 */
	Logger log = LoggerFactory.getLogger(SignaturesApiDelegateImpl.class);

	/** The signature service. */
	@Autowired
	private ISignatureService signatureService;

	/** The signature validation service. */
	@Autowired
	private ISignatureValidationService signatureValidationService;

	/** The proof generation service. */
	@Autowired
	private IProofGenerationService proofGenerationService;

	/** The service ca crl. */
	@Autowired
	private ICACRLService serviceCaCrl;

	/** The global configurations. */
	@Autowired
	private IGlobalConf globalConf;

	/** ESignSante Build Properties. */
	@Autowired
	private BuildProperties buildProperties;

	/** Enable/disable secret. */
	@Value("${config.secret}")
	private String secretEnabled;

	/**
	 * Digital signature with proof.
	 *
	 * @param secret          the secret
	 * @param idSignConf      the id sign conf
	 * @param doc             the doc
	 * @param idVerifSignConf the id verif sign conf
	 * @param proofParameters the proof parameters
	 * @param type            the signature type
	 * @return the response entity
	 */
	private ResponseEntity<ESignSanteSignatureReportWithProof> digitalSignatureWithProof(final String secret,
			final Long idSignConf, final MultipartFile doc, final Long idVerifSignConf,
			final ProofParameters proofParameters, final ESignatureType type, final List<String> signers, final String element) {
		final Optional<String> acceptHeader = getAcceptHeader();
		ResponseEntity<ESignSanteSignatureReportWithProof> re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		// get configurations
		final Optional<SignatureConf> signConf = globalConf.getSignatureById(idSignConf.toString());
		final Optional<SignVerifConf> verifConf = globalConf.getSignatureVerificationById(idVerifSignConf.toString());
		if (!signConf.isPresent() || !verifConf.isPresent()) {
			re = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			log.error("Configuration {}", HttpStatus.NOT_FOUND.getReasonPhrase());
		} else {
			final Optional<ProofConf> signProofConf = globalConf.getProofById(signConf.get().getIdProofConf());
			if (acceptHeader.isPresent() && acceptHeader.get().contains(WsVars.HEADER_TYPE.getVar())) {
				// this is redundant with current implementation, params are assured
				if (signParamsMissing(idSignConf, doc, idVerifSignConf) || proofParamsMissing(proofParameters)) {
					re = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				} else if (!signProofConf.isPresent()) {
					re = new ResponseEntity<>(HttpStatus.NOT_FOUND);
					log.error("Proof ID {}", HttpStatus.NOT_FOUND.getReasonPhrase());
				} else if ("enable".equalsIgnoreCase(secretEnabled) && signConf.get().noSecretMatch(secret)) {
					re = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
					log.error(HttpStatus.UNAUTHORIZED.getReasonPhrase());
				} else {
					final SignatureParameters signParams = signConf.get().getSignParams();
					if (element != null && !element.isEmpty() ) {
						signParams.setElementToSign(element);
					}
					signParams.setRoles(signers);
					final SignatureValidationParameters signVerifParams = verifConf.get().getSignVerifParams();
					final SignatureParameters signProofParams = signProofConf.get().getSignProofParams();
					re = signWithProof(doc, proofParameters, type, signParams, signVerifParams, signProofParams);
					log.info("Digital Signature With Proof Generated : {}", HttpStatus.OK.getReasonPhrase());
				}
			}
		}
		return re;
	}

	/**
	 * Sign with proof.
	 *
	 * @param doc                      the doc
	 * @param proofParameters          the proof parameters
	 * @param type                     the signature type
	 * @param signParams               the sign params
	 * @param signValidationParameters the sign validation parameters
	 * @param signProofParams          the sign proof params
	 * @return the response entity
	 */
	private ResponseEntity<ESignSanteSignatureReportWithProof> signWithProof(final MultipartFile doc,
			final ProofParameters proofParameters, final ESignatureType type, final SignatureParameters signParams,
			final SignatureValidationParameters signValidationParameters, final SignatureParameters signProofParams) {
		ResponseEntity<ESignSanteSignatureReportWithProof> re;
		try {
			// Contrôle du certificat de signature
			HttpStatus status = SignWsUtils.checkCertificate(signParams, serviceCaCrl.getCacrlWrapper());
			if (status != HttpStatus.CONTINUE) {
				re = new ResponseEntity<>(status);
			} else {
				final RapportSignature rapportSignature;
				final RapportValidationSignature rapportVerifSignature;
				// Signature du document
				if (ESignatureType.XADES.equals(type)) {
					rapportSignature = signatureService.signXADESBaselineB(doc.getBytes(), signParams);
					// Validation de la signature
					rapportVerifSignature = signatureValidationService.validateXADESBaseLineBSignature(
							rapportSignature.getDocSigne(), signValidationParameters, serviceCaCrl.getCacrlWrapper());
				} else if (ESignatureType.PADES.equals(type)) {
					rapportSignature = signatureService.signPADESBaselineB(doc.getBytes(), signParams);
					// Validation de la signature
					rapportVerifSignature = signatureValidationService.validatePADESBaseLineBSignature(
							rapportSignature.getDocSigneBytes(), signValidationParameters,
							serviceCaCrl.getCacrlWrapper());
				} else {
					rapportSignature = signatureService.signXMLDsig(doc.getBytes(), signParams);
					// Validation de la signature
					rapportVerifSignature = signatureValidationService.validateXMLDsigSignature(
							rapportSignature.getDocSigne(), signValidationParameters, serviceCaCrl.getCacrlWrapper());
				}
				// Géneration de la preuve
				final String proof = proofGenerationService.generateSignVerifProof(rapportVerifSignature,
						proofParameters, serviceCaCrl.getCacrlWrapper());
				// Contrôle du certificat de signature de la preuve
				status = SignWsUtils.checkCertificate(signProofParams, serviceCaCrl.getCacrlWrapper());
				if (status != HttpStatus.CONTINUE) {
					re = new ResponseEntity<>(status);
				} else {
					// Signature de la preuve
					final RapportSignature rapportSignaturePreuve = signatureService.signXADESBaselineB(proof,
							signProofParams);

					final ESignSanteSignatureReportWithProof rapport = populateResultSignWithProof(
							rapportVerifSignature.getListeErreurSignature(), rapportVerifSignature.getMetaData(),
							rapportVerifSignature.isValide(), rapportSignature.getDocSigneBytes(),
							rapportSignaturePreuve.getDocSigne());
					re = new ResponseEntity<>(rapport, HttpStatus.OK);
				}
			}
		} catch (final AsipSignClientException e1) {
			log.error(ExceptionUtils.getStackTrace(e1));
			re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		} catch (final AsipSignServerException e1) {
			log.error(ExceptionUtils.getStackTrace(e1));
			re = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		} catch (final IOException | AsipSignException e1) {
			log.error(ExceptionUtils.getStackTrace(e1));
			re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return re;
	}

	private ResponseEntity<ESignSanteSignatureReportWithProof> executeFSEsignatureWithProof(
			final String hash,final String idFacturationPS,final String typeFlux,
			final ProofParameters proofParameters, final ESignatureType type, final SignatureParameters signParams,
			final SignatureValidationParameters signValidationParameters, final SignatureParameters signProofParams) {
		ResponseEntity<ESignSanteSignatureReportWithProof> re;
		try {
			// Contrôle du certificat de signature
			HttpStatus status = SignWsUtils.checkCertificate(signParams, serviceCaCrl.getCacrlWrapper());
			if (status != HttpStatus.CONTINUE) {
				re = new ResponseEntity<>(status);
			} else {
				
				// Signature du document					
				final RapportSignature rapportSignature = signatureService.signFSE(hash.getBytes(), idFacturationPS, typeFlux, signParams);
					// Validation de la signature
				final String hashBase64 =  Base64.getEncoder().encodeToString(hash.getBytes());
		         DSSDocument doc = new  DigestDocument(DigestAlgorithm.SHA256, hashBase64, "FSE_"+hash);
				 
				final RapportValidationSignature  rapportVerifSignature = signatureValidationService.validateFSESignature(
							rapportSignature.getDocSigneBytes(), signValidationParameters, serviceCaCrl.getCacrlWrapper(),doc);							
				
			
				// Géneration de la preuve
				final String proof = proofGenerationService.generateSignVerifProof(rapportVerifSignature,
						proofParameters, serviceCaCrl.getCacrlWrapper());
				// Contrôle du certificat de signature de la preuve
				status = SignWsUtils.checkCertificate(signProofParams, serviceCaCrl.getCacrlWrapper());
				if (status != HttpStatus.CONTINUE) {
					re = new ResponseEntity<>(status);
				} else {
					// Signature de la preuve
				
					final RapportSignature rapportSignaturePreuve = signatureService.signXADESBaselineB(proof,
							signProofParams);
					
					final ESignSanteSignatureReportWithProof rapport = populateResultSignWithProof(
							rapportVerifSignature.getListeErreurSignature(), rapportVerifSignature.getMetaData(),
							rapportVerifSignature.isValide(), rapportSignature.getDocSigneBytes(),
							rapportSignaturePreuve.getDocSigne());
					re = new ResponseEntity<>(rapport, HttpStatus.OK);
				}
			}
		}catch(

	final AsipSignClientException e1)
	{
		log.error(ExceptionUtils.getStackTrace(e1));
		re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}catch(
	final AsipSignServerException e1)
	{
		log.error(ExceptionUtils.getStackTrace(e1));
		re = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
	}catch(final AsipSignException e1)
	{
		log.error(ExceptionUtils.getStackTrace(e1));
		re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	return re;
	}
	/**
	 * Checks if all signature params are present.
	 *
	 * @param idSignConf      the id sign conf
	 * @param doc             the doc
	 * @param idVerifSignConf the id verif sign conf
	 * @return boolean
	 */
	private boolean signParamsMissing(final Long idSignConf, final MultipartFile doc, final Long idVerifSignConf) {
		return idSignConf == null || doc == null || idVerifSignConf == null;
	}


	/**
	 * Checks if all fse signature params are present.
	 *
	 * @param idSignConf      the id sign conf
	 * @param doc             the doc
	 * @param idVerifSignConf the id verif sign conf
	 * @return boolean
	 */
	private boolean signParamsMissing(final Long idSignConf, final String doc,
			/* , final String idFacturation, */ final Long idVerifSignConf) {
		return idSignConf == null || doc == null || idVerifSignConf == null;
	}


	/**
	 * Checks if proof params are present.
	 *
	 * @param proofParameters the proof parameters
	 * @return boolean
	 */
	private boolean proofParamsMissing(final ProofParameters proofParameters) {
		return proofParameters.getRequestid() == null || proofParameters.getProofTag() == null
				|| proofParameters.getApplicantId() == null;
	}

	/**
	 * Called operation.
	 *
	 * @param opName the op name
	 * @return the string
	 */
	private String calledOperation(final String opName) {
		final Optional<NativeWebRequest> request = getRequest();
		String operation = opName;
		if (request.isPresent()) {
			// get called operation for proof generation
			operation = request.get().getContextPath() + opName;
		}
		return operation;
	}

	/**
	 * Signature XMldsig with proof.
	 *
	 * @param idSignConf      the id sign conf
	 * @param doc             the doc
	 * @param idVerifSignConf the id verif sign conf
	 * @param requestId       the request id
	 * @param proofTag        the proof tag
	 * @param applicantId     the applicant id
	 * @param secret          the secret
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteSignatureReportWithProof> signatureXMLdsigWithProof(final Long idSignConf,
			final MultipartFile doc, final Long idVerifSignConf, final String requestId, final String proofTag,
			final String applicantId, final String secret, final String element) {
		Version wsVersion = DEFAULT_VERSION;
		try {
			// get version object for proof generation
			wsVersion = new Version(buildProperties.getVersion());
		} catch (final ParseException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		final ProofParameters proofParameters = new ProofParameters("Sign", requestId, proofTag, applicantId,
				calledOperation("/signatures/xmldsigwithproof"), wsVersion);

		try {
			// Remplissage de la liste des beans OpenId
			List<OpenIdTokenBean> tokens;
			tokens = SignWsUtils.convertOpenIdTokens(parseOpenIdTokenHeader());
			if (!tokens.isEmpty()) {
				proofParameters.setOpenidTokens(tokens);
			}
			return digitalSignatureWithProof(secret, idSignConf, doc, idVerifSignConf, proofParameters,
					ESignatureType.XMLDSIG, null, element);
		} catch (AsipSignClientException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Signature xades with proof.
	 *
	 * @param idSignConf      the id sign conf
	 * @param doc             the doc
	 * @param idVerifSignConf the id verif sign conf
	 * @param requestId       the request id
	 * @param proofTag        the proof tag
	 * @param applicantId     the applicant id
	 * @param secret          the secret
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteSignatureReportWithProof> signatureXadesWithProof(final Long idSignConf,
			final MultipartFile doc, final Long idVerifSignConf, final String requestId, final String proofTag,
			final String applicantId, final String secret, final List<String> signers) {
		Version wsVersion = DEFAULT_VERSION;
		try {
			// get version object for proof generation
			wsVersion = new Version(buildProperties.getVersion());
		} catch (final ParseException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		final ProofParameters proofParameters = new ProofParameters("Sign", requestId, proofTag, applicantId,
				calledOperation("/signatures/xadesbaselinebwithproof"), wsVersion);


		try {
			// Remplissage de la liste des beans OpenId
			List<OpenIdTokenBean> tokens;
			tokens = SignWsUtils.convertOpenIdTokens(parseOpenIdTokenHeader());
			if (!tokens.isEmpty()) {	
				proofParameters.setOpenidTokens(tokens);
			}
			return digitalSignatureWithProof(secret, idSignConf, doc, idVerifSignConf, proofParameters,
					ESignatureType.XADES, signers, null);
		} catch (AsipSignClientException e) {
			// Problème lors du traitement du Header X-openidToken
			log.error(ExceptionUtils.getStackTrace(e));
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	/**
	 * Signature d'une fse avec preuve.
	 *
	 * @param idSignConf      the id sign conf
	 * @param hash            hash à signer de la FSE
	 * @param idFacturationPS id du PS
	 * @param typeFlux        type de flux (T ou R)
	 * @param idVerifSignConf the id verif sign conf
	 * @param requestId       the request id
	 * @param proofTag        the proof tag
	 * @param applicantId     the applicant id
	 * @param secret          the secret
	 * @param signers         the signers
	 * @return the response entity
	 */

	@Override
	public ResponseEntity<ESignSanteSignatureReportWithProof> signatureFSEWithProof(
			@ApiParam(value = "Secret", required=true) @RequestParam(value="secret", required=true)  String secret,
			  @ApiParam(value = "Identifiant de configuration à sélectionner parmi la liste des configurations disponibles pour la signature (appel de l'opération \\\"/configurations\\\").", required=true) @RequestParam(value="idSignConf", required=true)  Long idSignConf,
			  @ApiParam(value = "hash à signer.", required=true) @RequestParam(value="hash", required=true)  String hash,
			  @ApiParam(value = "identifiant de facturation du personnel de Sante à l'origine de la demande de signature", required=true) @RequestParam(value="idFacturationPS", required=true)  String idFacturationPS,
			  @ApiParam(value = "type de flux (valeur T ou R)", required=true) @RequestParam(value="typeFlux", required=true)  String typeFlux,
			  @ApiParam(value = "Identifiant de configuration à sélectionner parmi la liste des configurations disponibles pour la vérification de signature (appel de l'opération \\\"/configurations\\\").", required=true) @RequestParam(value="idVerifSignConf", required=true)  Long idVerifSignConf,
			  @ApiParam(value = "Liste des signataires délégataires. L'IHM swagger ne gère qu'1 seul signer - pour gérer plusieurs signers, rajouter des paramètres à la requête CURL.<br>Exemple: curl -X POST [...] -F \"signers=Dupont\" -F \"signers=Dupond\"") @RequestParam(value="signers", required=false)  List<String> signers,
			  @ApiParam(value = "Identifiant de la demande pour renseigner l'élément RequestId de la preuve.") @RequestParam(value="requestId", required=false)  String requestId,
			  @ApiParam(value = "Tag utilisé pour renseigner l'élément Tag de la preuve.") @RequestParam(value="proofTag", required=false)  String proofTag,
			  @ApiParam(value = "Identifiant xxx du demandeur utilisé pour renseigner le champ applicantId de la preuve.") @RequestParam(value="applicantId", required=false)  String applicantId )
	{
					Version wsVersion = DEFAULT_VERSION;
		try {
			// get version object for proof generation
			wsVersion = new Version(buildProperties.getVersion());
		} catch (final ParseException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		final ProofParameters proofParameters = new ProofParameters("Sign", requestId, proofTag, applicantId,
				calledOperation("/signatures/fse"), wsVersion);

		try {
			// Remplissage de la liste des beans OpenId
			List<OpenIdTokenBean> tokens;
			tokens = SignWsUtils.convertOpenIdTokens(parseOpenIdTokenHeader());
			if (!tokens.isEmpty()) {
				proofParameters.setOpenidTokens(tokens);
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		
			return signFSEWithProof(secret, idSignConf, hash, idFacturationPS, typeFlux, idVerifSignConf,
					proofParameters, ESignatureType.FSE, signers);

		} catch (AsipSignClientException e) {
			// Problème lors du traitement du Header X-openidToken
			log.error(ExceptionUtils.getStackTrace(e));
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Signature d'une fse avec preuve.
	 *
	 * @param idSignConf      the id sign conf
	 * @param hash            hash à signer de la FSE
	 * @param idFacturationPS id du PS
	 * @param typeFlux        type de flux (T ou R)
	 * @param idVerifSignConf the id verif sign conf
	 * @param requestId       the request id
	 * @param proofTag        the proof tag
	 * @param applicantId     the applicant id
	 * @param secret          the secret
	 * @param signers         reserved => paramètre ignoré dans le cas d'une FSE
	 * @return the response entity
	 */
	private ResponseEntity<ESignSanteSignatureReportWithProof> signFSEWithProof(final String secret,
			final Long idSignConf, final String hash, final String idFacturationPS, final String typeFlux,
			final Long idVerifSignConf, final ProofParameters proofParameters, final ESignatureType type,
			final List<String> signers) {
		ResponseEntity<ESignSanteSignatureReportWithProof> re = new ResponseEntity<>(HttpStatus.GONE);
		final Optional<SignatureConf> signConf = globalConf.getSignatureById(idSignConf.toString());
		final Optional<SignVerifConf> verifConf = globalConf.getSignatureVerificationById(idVerifSignConf.toString());
		if (!signConf.isPresent() || !verifConf.isPresent()) {
			re = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			log.error("Configuration {}", HttpStatus.NOT_FOUND.getReasonPhrase());
		} else {
			final Optional<ProofConf> signProofConf = globalConf.getProofById(signConf.get().getIdProofConf());
			// if (acceptHeader.isPresent() &&
			// acceptHeader.get().contains(WsVars.HEADER_TYPE.getVar())) {
			// this is redundant with current implementation, params are assured
			if (signParamsMissing(idSignConf, hash, idVerifSignConf) || proofParamsMissing(proofParameters)) {
				re = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} else if (!signProofConf.isPresent()) {
				re = new ResponseEntity<>(HttpStatus.NOT_FOUND);
				log.error("Proof ID {}", HttpStatus.NOT_FOUND.getReasonPhrase());
			} else if ("enable".equalsIgnoreCase(secretEnabled) && signConf.get().noSecretMatch(secret)) {
				re = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				log.error(HttpStatus.UNAUTHORIZED.getReasonPhrase());
			} else {
				final SignatureParameters signParams = signConf.get().getSignParams();
				signParams.setRoles(signers);
				final SignatureValidationParameters signVerifParams = verifConf.get().getSignVerifParams();
				final SignatureParameters signProofParams = signProofConf.get().getSignProofParams();
				re = executeFSEsignatureWithProof(hash, idFacturationPS, typeFlux, proofParameters, type, signParams,
						signVerifParams, signProofParams);
				log.info("Digital Signature With Proof Generated : {}", HttpStatus.OK.getReasonPhrase());
			}
			// }
		}
		return re;

	}

	/**
	 * Digital signature.
	 *
	 * @param secret     the secret
	 * @param idSignConf the id sign conf
	 * @param doc        the doc
	 * @param type       the signature type
	 * @return the response entity
	 */
	private ResponseEntity<ESignSanteSignatureReport> digitalSignature(final String secret, final Long idSignConf,
			final MultipartFile doc, final ESignatureType type, final List<String> signers, final String element) {
		final Optional<String> acceptHeader = getAcceptHeader();
		ResponseEntity<ESignSanteSignatureReport> re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		if (idSignConf != null && doc != null) {
			final Optional<SignatureConf> signConf = globalConf.getSignatureById(idSignConf.toString());
			if (acceptHeader.isPresent() && acceptHeader.get().contains(WsVars.HEADER_TYPE.getVar())) {
				if (!signConf.isPresent()) {
					re = new ResponseEntity<>(HttpStatus.NOT_FOUND);
					log.error("Configuration {}", HttpStatus.NOT_FOUND.getReasonPhrase());
				} else if ("enable".equalsIgnoreCase(secretEnabled) && signConf.get().noSecretMatch(secret)) {
					re = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
					log.error(HttpStatus.UNAUTHORIZED.getReasonPhrase());
				} else {
					final SignatureParameters signParams = signConf.get().getSignParams();
					if (element != null && !element.isEmpty()) {
						signParams.setElementToSign(element);
					}
					signParams.setRoles(signers);
					re = sign(signParams, doc, type);
					log.info("Digital Signature : {}", HttpStatus.OK.getReasonPhrase());
				}
			}
		} else {
			re = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return re;
	}

	/**
	 * Sign.
	 *
	 * @param signParams the signature parameters
	 * @param doc        the doc
	 * @param type       the signature type
	 * @return the response entity
	 */
	private ResponseEntity<ESignSanteSignatureReport> sign(final SignatureParameters signParams,
			final MultipartFile doc, final ESignatureType type) {
		ResponseEntity<ESignSanteSignatureReport> re;
		try {
			// Contrôle du certificat de signature
			final HttpStatus status = SignWsUtils.checkCertificate(signParams, serviceCaCrl.getCacrlWrapper());
			if (status != HttpStatus.CONTINUE) {
				re = new ResponseEntity<>(status);
			} else {
				// Signature
				final RapportSignature rapportSignature;
				if (ESignatureType.XADES.equals(type)) {
					rapportSignature = signatureService.signXADESBaselineB(doc.getBytes(), signParams);
				} else if (ESignatureType.PADES.equals(type)) {
					rapportSignature = signatureService.signPADESBaselineB(doc.getBytes(), signParams);
				} else {
					rapportSignature = signatureService.signXMLDsig(doc.getBytes(), signParams);
				}
				final ESignSanteSignatureReport rapport = populateResultSign(rapportSignature.getListeErreurSignature(),
						rapportSignature.getDocSigneBytes());
				re = new ResponseEntity<>(rapport, HttpStatus.OK);
			}
		} catch (final AsipSignClientException e2) {
			log.error(ExceptionUtils.getStackTrace(e2));
			re = new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		} catch (final AsipSignServerException e2) {
			log.error(ExceptionUtils.getStackTrace(e2));
			re = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		} catch (final IOException | AsipSignException e2) {
			log.error(ExceptionUtils.getStackTrace(e2));
			re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return re;
	}

	/**
	 * Signature XMLDsig.
	 *
	 * @param idSignConf the id sign conf
	 * @param doc        the doc
	 * @param secret     the secret
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteSignatureReport> signatureXMLdsig(final Long idSignConf, final MultipartFile doc,
			final String secret, final String element) {
		log.debug("appel signatureXMLdsig avec idSignConf: " + idSignConf + " element: " + element);
		return digitalSignature(secret, idSignConf, doc, ESignatureType.XMLDSIG, null, element);
	}

	/**
	 * Signature xades.
	 *
	 * @param idSignConf the id sign conf
	 * @param doc        the doc
	 * @param secret     the secret
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteSignatureReport> signatureXades(final Long idSignConf, final MultipartFile doc,
			final String secret, List<String> signers) {
		return digitalSignature(secret, idSignConf, doc, ESignatureType.XADES, signers, null);
	}

	/**
	 * Signature pades.
	 *
	 * @param idSignConf the id sign conf
	 * @param doc        the doc
	 * @param secret     the secret
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteSignatureReport> signaturePades(final Long idSignConf, final MultipartFile doc,
			final String secret, List<String> signers) {
		return digitalSignature(secret, idSignConf, doc, ESignatureType.PADES, signers, null);
	}

	/**
	 * Populate result sign.
	 *
	 * @param erreursSignature the erreurs signature
	 * @param signedDocument   the signed document
	 * @return the fr.asipsante.api.sign.ws.model. rapport signature
	 */
	private ESignSanteSignatureReport populateResultSign(final List<ErreurSignature> erreursSignature,
			final byte[] signedDocument) {
		final ESignSanteSignatureReport rapport = new ESignSanteSignatureReport();
		rapport.setDocSigne(Base64.getEncoder().encodeToString(signedDocument));
		final List<Erreur> erreurs = new ArrayList<>();
		for (final ErreurSignature erreurANS : erreursSignature) {
			final Erreur erreur = new Erreur();
			erreur.setCodeErreur(erreurANS.getCode());
			erreur.setMessage(erreurANS.getMessage());
			erreurs.add(erreur);
		}
		rapport.setErreurs(erreurs);
		return rapport;
	}

	/**
	 * Populate result sign with proof.
	 *
	 * @param erreursSignature the erreurs signature
	 * @param metadata         the metadata
	 * @param isValide         the is valide
	 * @param signedDocument   the signed document
	 * @param preuve           the preuve
	 * @return the rapport signature with proof
	 */
	private ESignSanteSignatureReportWithProof populateResultSignWithProof(final List<ErreurSignature> erreursSignature,
			final List<MetaDatum> metadata, final boolean isValide, final byte[] signedDocument, final String preuve) {
		final ESignSanteSignatureReportWithProof rapport = new ESignSanteSignatureReportWithProof();
		rapport.setValide(isValide);
		rapport.setDocSigne(Base64.getEncoder().encodeToString(signedDocument));
		rapport.setPreuve(Base64.getEncoder().encodeToString(preuve.getBytes()));

		final List<Erreur> erreurs = new ArrayList<>();
		for (final ErreurSignature erreurANS : erreursSignature) {
			final Erreur erreur = new Erreur();
			erreur.setCodeErreur(erreurANS.getCode());
			erreur.setMessage(erreurANS.getMessage());
			erreurs.add(erreur);
		}
		rapport.setErreurs(erreurs);

		final List<Metadata> metas = new ArrayList<>();
		for (final MetaDatum metadatum : metadata) {
			final Metadata meta = new Metadata();
			meta.setTypeMetadata(metadatum.getType().getName());
			if (metadatum.getType().equals(MetaDataType.RAPPORT_DIAGNOSTIQUE)
					|| metadatum.getType().equals(MetaDataType.RAPPORT_DSS)) {
				meta.setMessage(Base64.getEncoder().encodeToString(metadatum.getValue().getBytes()));
			} else {
				meta.setMessage(metadatum.getValue());
			}
			metas.add(meta);
		}
		rapport.setMetaData(metas);
		return rapport;
	}
	
		/**
	 * Signature pades with proof.
	 *
	 * @param idSignConf      the id sign conf
	 * @param doc             the doc
	 * @param idVerifSignConf the id verif sign conf
	 * @param requestId       the request id
	 * @param proofTag        the proof tag
	 * @param applicantId     the applicant id
	 * @param secret          the secret
	 * @return the response entity
	 */
	@Override
	public ResponseEntity<ESignSanteSignatureReportWithProof> signaturePadesWithProof(final Long idSignConf,
			final MultipartFile doc, final Long idVerifSignConf, final String requestId, final String proofTag,
			final String applicantId, final String secret, List<String> signers) {
		Version wsVersion = DEFAULT_VERSION;
		try {
			// get version object for proof generation
			wsVersion = new Version(buildProperties.getVersion());
		} catch (final ParseException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		final ProofParameters proofParameters = new ProofParameters("Sign", requestId, proofTag, applicantId,
				calledOperation("/signatures/padesbaselinebwithproof"), wsVersion);

		try {
			// Remplissage de la liste des beans OpenId
			List<OpenIdTokenBean> tokens;
			tokens = SignWsUtils.convertOpenIdTokens(parseOpenIdTokenHeader());
			if (!tokens.isEmpty()) {
				proofParameters.setOpenidTokens(tokens);
			}
			return digitalSignatureWithProof(secret, idSignConf, doc, idVerifSignConf, proofParameters,
					ESignatureType.PADES, signers, null);
		} catch (AsipSignClientException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}


}
