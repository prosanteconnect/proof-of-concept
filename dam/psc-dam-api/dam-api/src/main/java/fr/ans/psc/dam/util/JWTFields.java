package fr.ans.psc.dam.util;

public enum JWTFields {
	
	    //header
	    KEY_IDENTIFIER("kid"),
	    ALGO("alg"),
	    
	    //body
	    SUB("sub"),
	    USERINFO("userInfo"), 
	    ISS("iss"),
	    DATE_EXPIRATION("exp"),
	    DATE_CREATION("iat"),
	    JTI("jti");
	
	private String field;
	 
	JWTFields(String field) {
        this.field = field;
    }
	
	public String getFieldName() {
        return field;
    }	
}
