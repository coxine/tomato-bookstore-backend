package tg.cos.tomatomall.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Date;

@Component
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("aliyun.oss")
public class OSSUtil {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    public String upload(String objectName, InputStream inputStream) {
//        System.out.println("start upload");
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
//        System.out.println(putObjectRequest);
        try {
            ossClient.putObject(putObjectRequest);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        String url = ossClient.generatePresignedUrl(bucketName, objectName, new Date()).toString().split("\\?Expires")[0];
//        System.out.println(url);
        return url;
    }
}
