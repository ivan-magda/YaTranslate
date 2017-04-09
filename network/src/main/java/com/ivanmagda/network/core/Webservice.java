/**
 * Copyright (c) 2017 Ivan Magda
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ivanmagda.network.core;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Provide an abstraction over the webservice.
 * Does loading data over the network.
 */
public final class Webservice {

    /* Log tag for debug statements. */
    private static String LOG_TAG = Webservice.class.getSimpleName();

    /* HttpURLConnection configuration constants */
    private static final int READ_TIME_OUT = 10000;
    private static final int CONNECTION_TIME_OUT = 15000;

    private Webservice() {
    }

    /**
     * @param resource The resource to be loaded using HttpUrlConnection.
     * @param <A>      The generic return parameter, that will return resource.parseBlock.parse function.
     * @return Result after parsing.
     */
    public static <A> A load(Resource<A> resource) {
        // Getting a connection to the resource referred to by this URL
        // and trying to connect.
        HttpURLConnection connection = null;
        URL url = resource.url;

        if (url == null) {
            return null;
        }

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(READ_TIME_OUT);
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setRequestMethod(resource.httpMethodName);
            connection.setDoInput(true);
            connection.connect();

            String response = processResponse(connection);

            return resource.parseBlock.parse(response);
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Failed to download raw data", exception);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    private static String processResponse(HttpURLConnection connection) {
        try {
            // Did we receive a successful 2XX status code.
            int responseCode = connection.getResponseCode();
            if (responseCode < HttpURLConnection.HTTP_OK || responseCode > 299) {
                Log.w(LOG_TAG, "Received status code other then 2XX, status code: " + responseCode);
                return null;
            }
            Log.d(LOG_TAG, "Response status code: " + responseCode
                    + " for URL: " + connection.getURL());

            return readInput(connection.getInputStream());
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Failed to process on http response", exception);
        }

        return null;
    }

    private static String readInput(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader((new InputStreamReader(inputStream)));

        // Reading each line of the data.
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

        reader.close();
        inputStream.close();

        return stringBuilder.toString();
    }
}
