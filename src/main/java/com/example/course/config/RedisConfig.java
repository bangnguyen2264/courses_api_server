package com.example.course.config;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {
//    @Bean
//        public LettuceConnectionFactory redisConnectionFactory() {
//            // Dùng DefaultAzureCredential để lấy token
//            DefaultAzureCredential credential = new DefaultAzureCredentialBuilder().build();
//
//            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
//            config.setHostName("courses-cache-sever.redis.cache.windows.net");
//            config.setPort(6380);
//            config.setPassword(getRedisToken(credential));
//
//            LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                    .useSsl()
//                    .build();
//
//            return new LettuceConnectionFactory(config, clientConfig);
//        }
//
//        private String getRedisToken(DefaultAzureCredential credential) {
//            TokenRequestContext context = new TokenRequestContext()
//                    .addScopes("https://redis.azure.com/.default");
//            return credential.getToken(context).block().getToken();
//    }
    @Bean
    public LettuceClientConfiguration lettuceClientConfiguration() {
        ClientOptions clientOptions = ClientOptions.builder()
                .protocolVersion(ProtocolVersion.RESP2)
                .build();

        return LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .build();
    }
}
