package org.apache.cactus.internal.client.connector.http;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.util.HttpURLConnection;

import java.net.URL;

/**
 * Wrapper for the HttpURLConnection that supports the disconnect method as
 * this was not available in the original HttpClient.
 */
public class ReleasableHttpURLConnection extends HttpURLConnection {

    private final HttpMethod httpMethod;

    /**
     * create a wrapper for the HttpMethod.
     *
     * @param method connected HttpMethod
     * @param url destination URL
     */
    public ReleasableHttpURLConnection(HttpMethod method, URL url) {
        super(method, url);
        this.httpMethod = method;
    }

    @Override
    public void disconnect(){
        this.httpMethod.releaseConnection();
    }
}
