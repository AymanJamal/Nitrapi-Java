package net.nitrado.api.common.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.nitrado.api.common.exceptions.NitrapiErrorException;
import net.nitrado.api.common.exceptions.NitrapiHttpException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * HttpClient that actually connects to the internet and gets the data.
 */
public class ProductionHttpClient implements HttpClient {

    private int rateLimit;
    private int rateLimitRemaining;
    private long rateLimitReset;
    private String locale = "en";

    public JsonObject dataGet(String url, String accessToken, Parameter[] parameters) {

        // create the full url string with parameters
        boolean first = true;
        StringBuilder fullUrl = new StringBuilder();
        fullUrl.append(url);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                fullUrl.append(first ? "?" : "&");
                fullUrl.append(parameter.getKey());
                fullUrl.append("=");
                try {
                    fullUrl.append(URLEncoder.encode(parameter.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // everyone should support utf-8 so this should not happen
                    e.printStackTrace();
                }
                first = false;
            }
        }
        fullUrl.append(first ? "?" : "&");
        fullUrl.append("locale");
        fullUrl.append("=");
        fullUrl.append(locale);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fullUrl.toString()).openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            BufferedReader reader;
            if (connection.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            StringBuffer response = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            if (response.length() == 0) {
                throw new NitrapiHttpException(new NitrapiErrorException("We got an empty result.", connection.getResponseCode()));
            }

            // get the results

            JsonObject result = (JsonObject) new JsonParser().parse(response.toString());

            if (connection.getHeaderField("X-Rate-Limit") != null) {
                rateLimit = Integer.parseInt(connection.getHeaderField("X-RateLimit-Limit"));
                rateLimitRemaining = Integer.parseInt(connection.getHeaderField("X-RateLimit-Remaining"));
                rateLimitReset = Long.parseLong(connection.getHeaderField("X-RateLimit-Reset"));
            }

            if (!result.get("status").getAsString().equals("success")) {
                throw new NitrapiErrorException(result.get("message").getAsString(), connection.getResponseCode());
            }

            // return the interesting subobject
            if (result.get("data") != null) {
                return result.get("data").getAsJsonObject();
            }

            return result;

        } catch (IOException e) {
            throw new NitrapiHttpException(e);
        }
    }


    public JsonObject dataPost(String url, String accessToken, Parameter[] parameters) {

        // create POST parameter string
        boolean first = false;
        StringBuilder params = new StringBuilder();
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                params.append(first ? "?" : "&");
                params.append(parameter.getKey());
                params.append("=");
                try {
                    params.append(URLEncoder.encode(parameter.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // everyone should support utf-8 so this should not happen
                    e.printStackTrace();
                }
            }
        }

        url += "?locale="+locale;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            // write post parameters
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(params.toString());
            writer.flush();
            writer.close();


            BufferedReader reader;
            if (connection.getResponseCode() == 200 || connection.getResponseCode() == 201) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            StringBuffer response = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            if (response.length() == 0) {
                throw new NitrapiHttpException(new NitrapiErrorException("We got an empty result.  ("+connection.getResponseCode()+")", connection.getResponseCode()));
            }

            JsonParser parser = new JsonParser();
            JsonObject result = (JsonObject) parser.parse(response.toString());

            if (connection.getHeaderField("X-Rate-Limit") != null) {
                rateLimit = Integer.parseInt(connection.getHeaderField("X-RateLimit-Limit"));
                rateLimitRemaining = Integer.parseInt(connection.getHeaderField("X-RateLimit-Remaining"));
                rateLimitReset = Long.parseLong(connection.getHeaderField("X-RateLimit-Reset"));
            }

            if (!result.get("status").getAsString().equals("success")) {
                throw new NitrapiErrorException(result.get("message").getAsString(), connection.getResponseCode());
            }


            // return the interesting subobject
            if (result.get("data") != null) {
                return result.get("data").getAsJsonObject();
            }

            return result;
        } catch (IOException e) {
            throw new NitrapiHttpException(e);
        }
    }

    public JsonObject dataDelete(String url, String accessToken, Parameter[] parameters) {

        // create DELETE parameter string
        boolean first = false;
        StringBuilder params = new StringBuilder();
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                params.append(first ? "?" : "&");
                params.append(parameter.getKey());
                params.append("=");
                try {
                    params.append(URLEncoder.encode(parameter.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // everyone should support utf-8 so this should not happen
                    e.printStackTrace();
                }
            }
        }

        url += "?locale="+locale;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            // write post parameters
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(params.toString());
            writer.flush();
            writer.close();


            BufferedReader reader;
            if (connection.getResponseCode() == 200 || connection.getResponseCode() == 201) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            StringBuffer response = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            if (response.length() == 0) {
                throw new NitrapiHttpException(new NitrapiErrorException("We got an empty result.", connection.getResponseCode()));
            }

            JsonParser parser = new JsonParser();
            JsonObject result = (JsonObject) parser.parse(response.toString());

            if (connection.getHeaderField("X-Rate-Limit") != null) {
                rateLimit = Integer.parseInt(connection.getHeaderField("X-RateLimit-Limit"));
                rateLimitRemaining = Integer.parseInt(connection.getHeaderField("X-RateLimit-Remaining"));
                rateLimitReset = Long.parseLong(connection.getHeaderField("X-RateLimit-Reset"));
            }

            if (!result.get("status").getAsString().equals("success")) {
                throw new NitrapiErrorException(result.get("message").getAsString(), connection.getResponseCode());
            }


            // return the interesting subobject
            if (result.get("data") != null) {
                return result.get("data").getAsJsonObject();
            }

            return result;
        } catch (IOException e) {
            throw new NitrapiHttpException(e);
        }
    }

    public InputStream rawGet(String url) {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            return connection.getInputStream();
        } catch (IOException e) {
            throw new NitrapiHttpException(e);
        }

    }

    public void rawPost(String url, String token, byte[] body) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Token", token);
            connection.setRequestProperty("Content-Type", "application/binary");

            // write post parameters
            connection.getOutputStream().write(body);
            connection.getOutputStream().close();


            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            if (response.length() == 0) {
                throw new NitrapiHttpException(new NitrapiErrorException("We got an empty result.", connection.getResponseCode()));
            }

            JsonParser parser = new JsonParser();
            JsonObject result = (JsonObject) parser.parse(response.toString());

            if (!result.get("status").getAsString().equals("success")) {
                throw new NitrapiErrorException(result.get("message").getAsString(), connection.getResponseCode());
            }

        } catch (IOException e) {
            throw new NitrapiHttpException(e);
        }
    }

    public int getRateLimit() {
        return rateLimit;
    }

    public int getRateLimitRemaining() {
        return rateLimitRemaining;
    }

    public long getRateLimitReset() {
        return rateLimitReset;
    }

    public void setLanguage(String lang) {
        locale = lang;
    }

    public String getLanguage() {
        return locale;
    }
}
