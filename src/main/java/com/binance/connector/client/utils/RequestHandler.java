package com.binance.connector.client.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.connector.client.enums.HttpMethod;
import com.binance.connector.client.exceptions.BinanceConnectorException;
import com.binance.connector.client.utils.signaturegenerator.Ed25519SignatureGenerator;
import com.binance.connector.client.utils.signaturegenerator.HmacSignatureGenerator;
import com.binance.connector.client.utils.signaturegenerator.RsaSignatureGenerator;
import com.binance.connector.client.utils.signaturegenerator.SignatureGenerator;

public class RequestHandler {
    private final String apiKey;
    private final SignatureGenerator signatureGenerator;
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final ProxyAuth proxy;

    public RequestHandler(final String apiKey, final ProxyAuth proxy) {
        this(apiKey, null, proxy);
    }

    public RequestHandler(final String apiKey, final SignatureGenerator signatureGenerator, final ProxyAuth proxy) {
        this.apiKey = apiKey;
        this.signatureGenerator = signatureGenerator;
        this.proxy = proxy;
    }

    public String sendPublicRequest(final String baseUrl, final String urlPath, final Map<String, Object> parameters, final HttpMethod httpMethod, final boolean showLimitUsage) {
        final String fullUrl = UrlBuilder.buildFullUrl(baseUrl, urlPath, parameters);
        logger.debug("{} {}", httpMethod, fullUrl);

        return ResponseHandler.handleResponse(RequestBuilder.buildPublicRequest(fullUrl, httpMethod), showLimitUsage, proxy);
    }

    public String sendApiRequest(final String baseUrl, final String urlPath, final Map<String, Object> parameters, final HttpMethod httpMethod, final boolean showLimitUsage) {
        if (null == apiKey || apiKey.isEmpty()) {
            throw new BinanceConnectorException("[RequestHandler] API key cannot be null or empty!");
        }

        final String fullUrl = UrlBuilder.buildFullUrl(baseUrl, urlPath, parameters);
        logger.debug("{} {}", httpMethod, fullUrl);

        return ResponseHandler.handleResponse(RequestBuilder.buildApiKeyRequest(fullUrl, httpMethod, apiKey), showLimitUsage, proxy);
    }

    public String sendSignedRequest(final String baseUrl, final String urlPath, Map<String, Object> parameters, final HttpMethod httpMethod, final boolean showLimitUsage) {                          
        if (signatureGenerator.getClass() == HmacSignatureGenerator.class && (null == apiKey || apiKey.isEmpty())) {
            throw new BinanceConnectorException("[RequestHandler] Secret key/API key cannot be null or empty!");
        }
        if ((signatureGenerator.getClass() == RsaSignatureGenerator.class || signatureGenerator.getClass() == Ed25519SignatureGenerator.class) && (null == apiKey || apiKey.isEmpty())) {
            throw new BinanceConnectorException("[RequestHandler] Private key/API key cannot be null or empty!");
        }

        parameters = (parameters == null) ? new HashMap<>() : parameters;
        parameters.putIfAbsent("timestamp", UrlBuilder.buildTimestamp());
        parameters.put("signature", this.signatureGenerator.getSignature(UrlBuilder.joinQueryParameters(parameters)));

        final String fullUrl = UrlBuilder.buildFullUrl(baseUrl, urlPath, parameters);
        logger.info("{} {}", httpMethod, fullUrl);

        return ResponseHandler.handleResponse(RequestBuilder.buildApiKeyRequest(fullUrl, httpMethod, apiKey), showLimitUsage, proxy);
    }
}
