package com.vichu.japantrip.utils;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.vichu.japantrip.R;

import java.io.File;
import java.io.InputStream;
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
            // Load credentials from assets/aws_credentials.properties
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

    public void uploadFile(File file) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String fileName = "contacts/" + UUID.randomUUID().toString() + ".txt";
                s3Client.putObject(new PutObjectRequest(BUCKET_NAME, fileName, file));
                Log.d(TAG, "File uploaded successfully to S3: " + fileName);
            } catch (Exception e) {
                Log.e(TAG, "File upload failed: " + e.getMessage());
            }
        });
    }

    public File downloadFile(String s3FileName, Context context) {
        if (s3Client == null) {
            Log.e(TAG, "Amazon S3 client is NULL! Cannot download file.");
            return null;
        }
        try {
            File localFile = new File(context.getCacheDir(), "downloaded_contacts.txt");
            s3Client.getObject(new GetObjectRequest(BUCKET_NAME, s3FileName), localFile);
            Log.d(TAG, "File downloaded successfully from S3: " + s3FileName);
            return localFile;
        } catch (Exception e) {
            Log.e(TAG, "File download failed: " + e.getMessage(), e);
            return null;
        }
    }
}
