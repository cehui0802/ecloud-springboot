package com.telecom.ecloudframework.sys.autoconfigure.uploader;


import com.telecom.ecloudframework.sys.oss.MinioOss;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass({MinioClient.class})
@EnableConfigurationProperties({MinioConfigProperties.class})
@Configuration
public class UploaderAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(UploaderAutoConfiguration.class);
    @Autowired
    MinioConfigProperties minioConfigProperties = null;

    public UploaderAutoConfiguration() {
    }

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = null;

        try {
            minioClient = new MinioClient(this.minioConfigProperties.getEndpoint(), this.minioConfigProperties.getAccessKey(), this.minioConfigProperties.getSecretKey());
        } catch (Exception var3) {
            log.error("minioClient初始化失败:" + var3.getMessage());
        }

        return minioClient;
    }

    @Bean
    public MinioOss minioOssService() {
        MinioOss minioOss = new MinioOss();
        minioOss.setBucketName(this.minioConfigProperties.getBucket());
        minioOss.setPath(this.minioConfigProperties.getPath());
        return minioOss;
    }
}

