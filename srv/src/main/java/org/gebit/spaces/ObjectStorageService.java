package org.gebit.spaces;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sap.cds.services.ServiceException;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

@Component
public class ObjectStorageService {
	
	private S3Client s3Client;
	private String endpoint;
	private String region;
	private String bucket;

	public ObjectStorageService(
			@Value("${aws.endpoint}")String endpoint, 
			@Value("${aws.region}")String region, 
			@Value("${aws.bucket}")String bucket,
			@Value("${aws.accessKeyId}")String accessKey, 
			@Value("${aws.secretAccessKey}")String secretKey) {
		this.endpoint = endpoint;
		this.region = region;
		this.bucket = bucket;
		
	     AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
	    		 accessKey,
	    		 secretKey
	        );
	     
		s3Client = (S3Client) S3Client.builder()
	             .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .endpointOverride(this.buildEndpointConfiguration())
                .region(Region.of(this.region))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // Required for DigitalOcean Spaces
                        .build())
                .build();
	}
	
	
	public String save(byte[] bytesArray, String filePath, String contentType) throws IOException {
		
		s3Client.putObject(request -> 
		  request
		    .bucket(this.bucket)
		    .key(filePath)
		    .contentType(contentType)
		    .acl(ObjectCannedACL.PUBLIC_READ)
		    .build(),  RequestBody.fromBytes(bytesArray));
		
			try {
				S3Utilities utils = S3Utilities.builder().region(Region.of(this.region)).endpoint(new URI(this.endpoint)) .build();
				return utils.getUrl(GetUrlRequest.builder().bucket(this.bucket).key(filePath).build()) .toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return "";
	}
	
	
	public void delete(String filePath) {
		s3Client.deleteObject(req -> req.bucket(this.bucket).key(filePath));
	}
	
	private URI buildEndpointConfiguration() {
		try {
			return new URI(this.endpoint);
		} catch (URISyntaxException e) {
			throw new ServiceException("AWS endpoint is not configured");
		}
	}
}
