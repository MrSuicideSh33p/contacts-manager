package com.vichu.japantrip.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.vichu.japantrip.R;
import com.vichu.japantrip.models.ContactData;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AwsS3Helper {

    private static final String TAG = "AwsS3Helper";
    private static final String BUCKET_NAME = "the-japan-trip-bucket";

    private AmazonS3 s3Client;

    public AwsS3Helper(Context context) {
        initializeS3Client(context);
    }

    private void initializeS3Client(Context context) {
        try {
            // Load credentials from raw/aws_credentials.properties
            Properties properties = new Properties();
            InputStream credentialsStream = context.getResources().openRawResource(R.raw.aws_credentials);
            properties.load(credentialsStream);

            String accessKey = properties.getProperty("AWS_ACCESS_KEY");
            String secretKey = properties.getProperty("AWS_SECRET_KEY");

            if (accessKey == null || secretKey == null) {
                throw new IllegalStateException("AWS credentials not found in properties file.");
            }

            s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
            s3Client.setRegion(Region.getRegion(Regions.US_EAST_1));

            Log.d(TAG, "Amazon S3 client initialized successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing AWS credentials: " + e.getMessage(), e);
        }
    }

    public void fetchContactList(S3ContactFetchListener listener) {
        new Thread(() -> {
            try {
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(BUCKET_NAME).withPrefix("contacts/");
                ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
                List<String> names = new ArrayList<>();
                List<String> files = new ArrayList<>();

                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    String fileName = objectSummary.getKey();
                    files.add(fileName);
                    names.add(fileName.substring(fileName.lastIndexOf('/') + 1));
                }
                listener.onSuccess(names, files);
            } catch (Exception e) {
                listener.onError(e.getMessage());
            }
        }).start();
    }

    public void fetchContactDetails(String fileName, ContactDataListener listener) {
        new Thread(() -> {
            try {
                S3Object s3Object = s3Client.getObject(BUCKET_NAME, fileName);
                InputStream inputStream = s3Object.getObjectContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                ContactData contactData = new ContactData(content.toString());
                listener.onResult(contactData, null);
            } catch (Exception e) {
                listener.onResult(null, e.getMessage());
            }
        }).start();
    }

    public void uploadContact(@Nullable String existingFileName, String fileContent, UploadListener listener) {
        new Thread(() -> {
            try {
                // Determine filename (reuse existing if provided, otherwise create new)
                String fileName = (existingFileName != null) ? existingFileName : "contacts/" + UUID.randomUUID().toString() + ".txt";

                // Convert content to input stream
                InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));

                // Prepare metadata
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(fileContent.length());

                // Upload to S3 (overwrites if file already exists)
                s3Client.putObject(BUCKET_NAME, fileName, inputStream, metadata);

                Log.d(TAG, "File uploaded successfully: " + fileName);
                listener.onSuccess(true, fileName);
            } catch (Exception e) {
                Log.e(TAG, "File upload failed: " + e.getMessage());
                listener.onSuccess(false, null);
            }
        }).start();
    }

    public void deleteContact(String fileName, DeleteListener listener) {
        new Thread(() -> {
            try {
                s3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, fileName));
                listener.onSuccess(true);
            } catch (Exception e) {
                listener.onSuccess(false);
            }
        }).start();
    }

    public interface S3ContactFetchListener {
        void onSuccess(List<String> names, List<String> files);
        void onError(String error);
    }

    public interface UploadListener {
        void onSuccess(boolean success, String fileName);
    }

    @FunctionalInterface
    public interface ContactDataListener {
        void onResult(ContactData contactData, String errorMessage);
    }

    public interface DeleteListener {
        void onSuccess(boolean success);
    }
}
