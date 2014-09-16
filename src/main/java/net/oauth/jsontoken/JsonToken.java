package net.oauth.jsontoken;

import net.oauth.jsontoken.crypto.AsciiStringSigner;
import net.oauth.jsontoken.crypto.SignatureAlgorithm;
import net.oauth.jsontoken.crypto.Signer;
import org.apache.commons.codec.binary.Base64;

import java.security.SignatureException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by steve on 12/09/14.
 */
public class JsonToken {
    // header names
    public final static String ALGORITHM_HEADER = "alg";
    public final static String KEY_ID_HEADER = "kid";
    public final static String TYPE_HEADER = "typ";

    // standard claim names (payload parameters)
    public final static String ISSUER = "iss";
    public final static String ISSUED_AT = "iat";
    public final static String EXPIRATION = "exp";
    public final static String AUDIENCE = "aud";

    // default encoding for all Json token
    public final static String BASE64URL_ENCODING = "base64url";

    public final static int DEFAULT_LIFETIME_IN_MINS = 2;


    private Map<String, Object> header;
    private SignatureAlgorithm sigAlg;

    protected final Clock clock;
    private final Map<String, Object> payload;
    private final String tokenString;

    // The following fields are only valid when signing the token.
    private final Signer signer;
    private String signature;
    private String baseString;


    /**
     * Public constructor, use empty data type.
     * @param signer the signer that will sign the token.
     */
    public JsonToken(Signer signer) {
        this(signer, new SystemClock());
    }

    /**
     * Public constructor.
     * @param signer the signer that will sign the token
     * @param clock a clock whose notion of current time will determine the not-before timestamp
     *   of the token, if not explicitly set.
     */
    public JsonToken(Signer signer, Clock clock) {
        JsonTokenUtil.checkNotNull(signer);
        JsonTokenUtil.checkNotNull(clock);

        this.payload = new LinkedHashMap<String, Object>();
        this.signer = signer;
        this.clock = clock;
        this.sigAlg = signer.getSignatureAlgorithm();
        this.signature = null;
        this.baseString = null;
        this.tokenString = null;
        String issuer = signer.getIssuer();
        if (issuer != null) {
            setParam(JsonToken.ISSUER, issuer);
        }
    }

    /**
     * Public constructor used when parsing a JsonToken {@link JsonToken}
     * (as opposed to create a token). This constructor takes Json payload
     * and clock as parameters, set all other signing related parameters to null.
     *
     * @param payload A payload JSON object.
     * @param clock a clock whose notion of current time will determine the not-before timestamp
     *   of the token, if not explicitly set.
     * @param tokenString The original token string we parsed to get this payload.
     */
    public JsonToken(Map<String, Object> header, Map<String, Object> payload, Clock clock,
                     String tokenString) {
        this.payload = payload;
        this.clock = clock;
        this.baseString = null;
        this.signature = null;
        this.sigAlg = null;
        this.signer = null;
        this.header = header;
        this.tokenString = tokenString;
    }

    /**
     * Public constructor used when parsing a JsonToken {@link JsonToken}
     * (as opposed to create a token). This constructor takes Json payload
     * as parameter, set all other signing related parameters to null.
     *
     * @param payload A payload JSON object.
     */
    public JsonToken(Map<String, Object> payload) {
        this.payload = payload;
        this.baseString = null;
        this.tokenString = null;
        this.signature = null;
        this.sigAlg = null;
        this.signer = null;
        this.clock = null;
    }

    /**
     * Public constructor used when parsing a JsonToken {@link JsonToken}
     * (as opposed to create a token). This constructor takes Json payload
     * and clock as parameters, set all other signing related parameters to null.
     *
     * @param payload A payload JSON object.
     * @param clock a clock whose notion of current time will determine the not-before timestamp
     *   of the token, if not explicitly set.
     */
    public JsonToken(Map<String, Object> payload, Clock clock) {
        this.payload = payload;
        this.clock = clock;
        this.baseString = null;
        this.tokenString = null;
        this.signature = null;
        this.sigAlg = null;
        this.signer = null;
    }

    /**
     * Returns the serialized representation of this token, i.e.,
     * keyId.sig.base64(payload).base64(data_type).base64(encoding).base64(alg)
     *
     * This is what a client (token issuer) would send to a token verifier over the
     * wire.
     * @throws java.security.SignatureException if the token can't be signed.
     */
    public String serializeAndSign() throws SignatureException {
        String baseString = computeSignatureBaseString();
        String sig = getSignature();
        return JsonTokenUtil.toDotFormat(baseString, sig);
    }

    /**
     * Returns a human-readable version of the token.
     */
    @Override
    public String toString() {
        return JsonTokenUtil.toJson(payload);
    }

    public String getIssuer() {
        return getParamAsString(ISSUER);
    }

    public Instant getIssuedAt() {
        Long issuedAt = getParamAsLong(ISSUED_AT);
        if (issuedAt == null) {
            return null;
        }
        // JWT represents time in seconds
        return Instant.ofEpochSecond(issuedAt);
    }

    public void setIssuedAt(Instant instant) {
        setParam(JsonToken.ISSUED_AT, instant.getEpochSecond());
    }

    public Instant getExpiration() {
        Long expiration = getParamAsLong(EXPIRATION);
        if (expiration == null) {
            return null;
        }
        // JWT represents time in seconds
        return Instant.ofEpochSecond(expiration);
    }

    public void setExpiration(Instant instant) {
        setParam(JsonToken.EXPIRATION, instant.getEpochSecond());
    }

    public String getAudience() {
        return getParamAsString(AUDIENCE);
    }

    public void setAudience(String audience) {
        setParam(AUDIENCE, audience);
    }

    public void setParam(String name, String value) {
        payload.put(name, value);
    }

    public void setParam(String name, Number value) {
        payload.put(name, value);
    }


    public Map<String, Object> getPayload() {
        return payload;
    }

    public String getKeyId() {
        return signer.getKeyId();
    }

    public SignatureAlgorithm getSignatureAlgorithm() {
        if (sigAlg == null) {
            if (header == null) {
                throw new IllegalStateException("JWT has no algorithm or header");
            }
            String algorithmName = (String)header.get(JsonToken.ALGORITHM_HEADER);
            if (algorithmName == null) {
                throw new IllegalStateException("JWT header is missing the required '" +
                        JsonToken.ALGORITHM_HEADER + "' parameter");
            }
            sigAlg = SignatureAlgorithm.getFromJsonName(algorithmName);
        }
        return sigAlg;
    }

    public String getTokenString() {
        return tokenString;
    }

    public Map<String, Object> getHeader() {
        if (header == null) {
            createHeader();
        }
        return header;
    }

    public String getParamAsString(String param) {
        return (String)payload.get(param);
    }

    public Long getParamAsLong(String param) {
        Number number = (Number)payload.get(param);
        if(number == null) {
            return null;
        } else {
            return Long.valueOf(number.longValue());
        }
    }

    protected String computeSignatureBaseString() {
        if (baseString != null && !baseString.isEmpty()) {
            return baseString;
        }
        baseString = JsonTokenUtil.toDotFormat(
                JsonTokenUtil.toBase64(getHeader()),
                JsonTokenUtil.toBase64(payload)
        );
        return baseString;
    }

    private Map<String, Object> createHeader() {
        header = new LinkedHashMap<String, Object>();
        header.put(ALGORITHM_HEADER, getSignatureAlgorithm().getNameForJson());
        String keyId = getKeyId();
        if (keyId != null) {
            header.put(KEY_ID_HEADER, keyId);
        }
        return header;
    }

    private String getSignature() throws SignatureException {
        if (signature != null && !signature.isEmpty()) {
            return signature;
        }

        if (signer == null) {
            throw new SignatureException("can't sign JsonToken with signer.");
        }
        String signature;
        // now, generate the signature
        AsciiStringSigner asciiSigner = new AsciiStringSigner(signer);
        signature = Base64.encodeBase64URLSafeString(asciiSigner.sign(baseString));

        return signature;
    }

}
