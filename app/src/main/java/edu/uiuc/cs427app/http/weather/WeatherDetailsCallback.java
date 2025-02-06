package edu.uiuc.cs427app.http.weather;

import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WeatherDetailsCallback extends UrlRequest.Callback {
    private static final String TAG = "WeatherDetailsCallback";

    public final TextView textDate;
    public final TextView textTime;
    public final TextView textTemperature;
    public final TextView textWeather;
    public final TextView textHumidity;
    public final TextView textWindCondition;

    /**
     * Processes any request redirection
     * @param request Request being redirected.
     * @param info Response information.
     * @param newLocationUrl Location where request is redirected.
     */
    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
        Log.i(TAG, "onRedirectReceived method called.");
        request.followRedirect();
    }

    /**
     * Processes the response start
     * @param request Request that started to get response.
     * @param info Response information.
     */
    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
        Log.i(TAG, "onResponseStarted method called.");
        request.read(ByteBuffer.allocateDirect(102400));
    }

    /**
     * Processes the completed read
     * @param request Request that received data.
     * @param info Response information.
     * @param byteBuffer The buffer that was passed in to
     *         {@link UrlRequest#read UrlRequest.read()}, now containing the
     *         received data. The buffer's position is updated to the end of
     *         the received data. The buffer's limit is not changed.
     */
    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
        // https://stackoverflow.com/questions/29190684/convert-bytebuffer-to-string-in-java
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        String responseData = new String(bytes);

        JsonArray convertedObject = new Gson().fromJson(responseData, JsonArray.class);
        Log.d("CronetJSON", "Response: " + convertedObject.toString());


        textDate.post(() -> textDate.setText("Date: " + LocalDate.now()));
        textTime.post(() -> textTime.setText("Time: " + LocalTime.now()));
        textWeather.post(() -> textWeather.setText(
                "Current Weather: " + convertedObject.get(0).getAsJsonObject().get("WeatherText")));
        textWindCondition.post(() -> textWindCondition.setText(
                "Current Wind Conditions: " + getWindInfo(convertedObject.get(0).getAsJsonObject())));
        textHumidity.post(() -> textHumidity.setText("Current Relative Humidity: " +
                convertedObject.get(0).getAsJsonObject().get("RelativeHumidity")));
        textTemperature.post(() -> textTemperature.setText("Current Temperature: " +
                convertedObject.get(0).getAsJsonObject().getAsJsonObject("Temperature")
                        .getAsJsonObject("Imperial").get("Value") + " Fahrenheit"));

        byteBuffer.clear();
        request.read(byteBuffer);
    }

    /**
     * Log when request succeeds
     * @param request Request that succeeded.
     * @param info Response information.
     */
    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        Log.i(TAG, "onSucceeded method called.");
    }

    /**
     * Logs when request fails
     * @param request Request that failed.
     * @param info Response information. May be {@code null} if no response was
     *         received.
     * @param error information about error.
     */
    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called." + error.getMessage());
    }

    /**
     * Gets information about the wind
     * @param obj the json object to parse
     * @return the wind info
     */
    private String getWindInfo(JsonObject obj) {
        JsonObject wind = obj.getAsJsonObject("Wind");
        String direction = String.valueOf(wind.getAsJsonObject("Direction").get("English"));
        String speed = String.valueOf(wind.getAsJsonObject("Speed")
                .getAsJsonObject("Imperial").get("Value"));
        return direction + " at " + speed + " miles per hour";
    }

}