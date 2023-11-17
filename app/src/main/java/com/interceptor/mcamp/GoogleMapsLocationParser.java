package com.interceptor.mcamp;
//Done

import android.net.Uri;
import android.util.Log;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleMapsLocationParser {

    public double[] getLatitudeLongitude(String googleMapsLink) {

        double[] coordinates = extractCoordinatesFromGoogleMapsLink(googleMapsLink);

        if (coordinates.length == 2) {
            double latitude = coordinates[0];
            double longitude = coordinates[1];

            Log.d("OutputLatitude", String.valueOf(latitude));
            Log.d("OutputLongitude", String.valueOf(longitude));
            return new double[]{latitude,longitude};
        } else {
            Log.d("Output", "Unable to extract coordinates.");
            return new double[]{};
        }
    }

    public double getLatitude(String googleMapsLink) {
        double[] coordinates = extractCoordinatesFromGoogleMapsLink(googleMapsLink);

        if (coordinates.length == 2) {
            double latitude = coordinates[0];

            Log.d("OutputLatitude", String.valueOf(latitude));
            return latitude;
        } else {
            Log.d("Output", "Unable to extract coordinates.");
            return 500.00;
        }
    }

    public double getLongitude(String googleMapsLink) {
        double[] coordinates = extractCoordinatesFromGoogleMapsLink(googleMapsLink);

        if (coordinates.length == 2) {
            double longitude = coordinates[1];

            Log.d("OutputLongitude", String.valueOf(longitude));
            return longitude;
        } else {
            Log.d("Output", "Unable to extract coordinates.");
            return 500.0;
        }
    }

    private double[] extractCoordinatesFromGoogleMapsLink(String googleMapsLink) {
        // Use a regular expression to extract coordinates
        Pattern pattern = Pattern.compile("@([\\d.]+),([\\d.]+)");
        Matcher matcher = pattern.matcher(googleMapsLink);

        if (matcher.find()) {
            // Extract latitude and longitude
            String latitude = matcher.group(1);
            String longitude = matcher.group(2);

            // Print the results
            assert latitude != null;
            assert longitude != null;
            return new double[]{Double.parseDouble(latitude), Double.parseDouble(longitude)};
        } else {
            return extractCoordinatesFromGoogleMapsLink1(googleMapsLink);
        }
    }

    private double[] extractCoordinatesFromGoogleMapsLink1(String googleMapsLink) {
        // Use a regular expression to extract coordinates
        Pattern pattern = Pattern.compile("([\\d.]+),([\\d.]+)");
        Matcher matcher = pattern.matcher(googleMapsLink);

        if (matcher.find()) {
            // Extract latitude and longitude
            String latitude = matcher.group(1);
            String longitude = matcher.group(2);

            // Print the results
            assert latitude != null;
            assert longitude != null;
            return new double[]{Double.parseDouble(latitude), Double.parseDouble(longitude)};
        } else {
            return extractCoordinatesFromGoogleMapsLink2(googleMapsLink);
        }
    }

    private double[] extractCoordinatesFromGoogleMapsLink2(String googleMapsLink) {
        Uri uri = Uri.parse(googleMapsLink);
        String query = uri.getQuery();
        if (query != null) {
            String[] queryParams = query.split("&");
            double latitude = 500.0, longitude = 500.0;

            for (String param : queryParams) {
                if (param.startsWith("!1d")) {
                    latitude = Double.parseDouble(param.substring(3));
                } else if (param.startsWith("!2d")) {
                    longitude = Double.parseDouble(param.substring(3));
                }
            }

            if(latitude != 500.0)
                return new double[]{latitude, longitude};
            else
                return extractCoordinatesFromGoogleMapsLink4(googleMapsLink);
        } else {
            return extractCoordinatesFromGoogleMapsLink4(googleMapsLink);
        }
    }

    private double[] extractCoordinatesFromGoogleMapsLink4(String googleMapsLink) {
        Pattern pattern = Pattern.compile("/@([\\d.]+),([\\d.]+)");
        Matcher matcher = pattern.matcher(googleMapsLink);

        if (matcher.find()) {
            double latitude = Double.parseDouble(Objects.requireNonNull(matcher.group(1)));
            double longitude = Double.parseDouble(Objects.requireNonNull(matcher.group(2)));

            return new double[]{latitude, longitude};
        } else {
            return extractCoordinatesFromGoogleMapsLink5(googleMapsLink);
        }
    }

    private double[] extractCoordinatesFromGoogleMapsLink5(String googleMapsLink) {
        Uri uri = Uri.parse(googleMapsLink);
        String data = uri.getQueryParameter("data");

        if (data != null) {
            String[] parts = data.split("!");
            double latitude = Double.parseDouble(parts[1].substring(3));
            double longitude = Double.parseDouble(parts[2].substring(3));

            return new double[]{latitude, longitude};
        } else {
            return new double[]{};
        }
    }
}

