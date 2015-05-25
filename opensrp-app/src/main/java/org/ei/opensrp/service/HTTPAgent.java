package org.ei.opensrp.service;

import android.content.Context;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.ei.opensrp.DristhiConfiguration;
import org.ei.opensrp.R;
import org.ei.opensrp.client.GZipEncodingHttpClient;
import org.ei.opensrp.domain.DownloadStatus;
import org.ei.opensrp.domain.LoginResponse;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.domain.ResponseStatus;
import org.ei.opensrp.repository.AllSettings;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.util.DownloadForm;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

import static org.ei.opensrp.AllConstants.REALM;
import static org.ei.opensrp.domain.LoginResponse.*;
import static org.ei.opensrp.util.HttpResponseUtil.getResponseBody;
import static org.ei.opensrp.util.Log.logError;
import static org.ei.opensrp.util.Log.logWarn;

public class HTTPAgent {
    private final GZipEncodingHttpClient httpClient;
    private Context context;
    private AllSettings settings;
    private AllSharedPreferences allSharedPreferences;
    private DristhiConfiguration configuration;


    public HTTPAgent(Context context, AllSettings settings, AllSharedPreferences allSharedPreferences, DristhiConfiguration configuration) {
        this.context = context;
        this.settings = settings;
        this.allSharedPreferences = allSharedPreferences;
        this.configuration = configuration;

        BasicHttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, 30000);
        HttpConnectionParams.setSoTimeout(basicHttpParams, 60000);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sslSocketFactoryWithopensrpCertificate(), 443));

        SingleClientConnManager connectionManager = new SingleClientConnManager(basicHttpParams, registry);
        httpClient = new GZipEncodingHttpClient(new DefaultHttpClient(connectionManager, basicHttpParams));
    }

    public Response<String> fetch(String requestURLPath) {
        try {
            setCredentials(allSharedPreferences.fetchRegisteredANM(), settings.fetchANMPassword());
            String responseContent = IOUtils.toString(httpClient.fetchContent(new HttpGet(requestURLPath)));
            return new Response<String>(ResponseStatus.success, responseContent);
        } catch (Exception e) {
            logWarn(e.toString());
            return new Response<String>(ResponseStatus.failure, null);
        }
    }

    public Response<String> post(String postURLPath, String jsonPayload) {
        try {
            setCredentials(allSharedPreferences.fetchRegisteredANM(), settings.fetchANMPassword());
            HttpPost httpPost = new HttpPost(postURLPath);
            StringEntity entity = new StringEntity(jsonPayload, HTTP.UTF_8);
            entity.setContentType("application/json; charset=utf-8");
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.postContent(httpPost);

            ResponseStatus responseStatus = response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED ? ResponseStatus.success : ResponseStatus.failure;
            response.getEntity().consumeContent();
            return new Response<String>(responseStatus, null);
        } catch (Exception e) {
            logWarn(e.toString());
            return new Response<String>(ResponseStatus.failure, null);
        }
    }

    public LoginResponse urlCanBeAccessWithGivenCredentials(String requestURL, String userName, String password) {
        setCredentials(userName, password);
        try {
            HttpResponse response = httpClient.execute(new HttpGet(requestURL));
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                return SUCCESS.withPayload(getResponseBody(response));
            } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                logError("Invalid credentials for: " + userName + " using " + requestURL);
                return UNAUTHORIZED;
            } else {
                logError("Bad response from Dristhi. Status code:  " + statusCode + " username: " + userName + " using " + requestURL);
                return UNKNOWN_RESPONSE;
            }
        } catch (IOException e) {
            logError("Failed to check credentials of: " + userName + " using " + requestURL + ". Error: " + e.toString());
            return NO_INTERNET_CONNECTIVITY;
        }
    }

    public DownloadStatus downloadFromUrl(String url, String filename) {
        setCredentials(allSharedPreferences.fetchRegisteredANM(), settings.fetchANMPassword());
        Response<DownloadStatus> status = DownloadForm.DownloadFromURL(url, filename, httpClient);
        return status.payload();
    }

    private void setCredentials(String userName, String password) {
        httpClient.getCredentialsProvider().setCredentials(new AuthScope(configuration.host(), configuration.port(), REALM),
                new UsernamePasswordCredentials(userName, password));
    }

    private SocketFactory sslSocketFactoryWithopensrpCertificate() {
        try {
            KeyStore trustedKeystore = KeyStore.getInstance("BKS");
            InputStream inputStream = context.getResources().openRawResource(R.raw.dristhi_client);
            try {
                trustedKeystore.load(inputStream, "phone red pen".toCharArray());
            } finally {
                inputStream.close();
            }
            SSLSocketFactory socketFactory = new SSLSocketFactory(trustedKeystore);
            final X509HostnameVerifier oldVerifier = socketFactory.getHostnameVerifier();
            socketFactory.setHostnameVerifier(new AbstractVerifier() {
                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                    for (String cn : cns) {
                        if (!configuration.shouldVerifyCertificate() || host.equals(cn)) {
                            return;
                        }
                    }
                    oldVerifier.verify(host, cns, subjectAlts);
                }
            });
            return socketFactory;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
