package com.lyl.study.cloud.base.config;

import com.netflix.discovery.EurekaClientConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.noop.NoopDiscoveryClientAutoConfiguration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
@EnableConfigurationProperties
@ConditionalOnClass(EurekaClientConfig.class)
@ConditionalOnProperty(value = "eureka.client.enabled", matchIfMissing = true)
@AutoConfigureBefore({NoopDiscoveryClientAutoConfiguration.class,
        CommonsClientAutoConfiguration.class, ServiceRegistryAutoConfiguration.class, EurekaClientAutoConfiguration.class})
@AutoConfigureAfter(name = "org.springframework.cloud.autoconfigure.RefreshAutoConfiguration")
public class EurekaCommonConfig {
    private ConfigurableEnvironment env;
    private RelaxedPropertyResolver propertyResolver;

    public EurekaCommonConfig(ConfigurableEnvironment env) {
        this.env = env;
        this.propertyResolver = new RelaxedPropertyResolver(env);
    }

    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils) throws MalformedURLException {
        PropertyResolver eurekaPropertyResolver = new RelaxedPropertyResolver(this.env, "eureka.instance.");
        String hostname = eurekaPropertyResolver.getProperty("hostname");

        boolean preferIpAddress = Boolean.parseBoolean(eurekaPropertyResolver.getProperty("preferIpAddress"));
        int nonSecurePort = Integer.valueOf(propertyResolver.getProperty("server.port", propertyResolver.getProperty("port", "8080")));
        int managementPort = Integer.valueOf(propertyResolver.getProperty("management.port", String.valueOf(nonSecurePort)));
        String managementContextPath = propertyResolver.getProperty("management.contextPath", propertyResolver.getProperty("server.contextPath", "/"));
        EurekaInstanceConfigBean instance = new EurekaInstanceConfigBean(inetUtils);
        instance.setNonSecurePort(nonSecurePort);
        /*
            这里做了修改
            修复Eureka原来发布的InstanceId中的IP地址与实际发布的IP地址不一致问题
         */
        instance.setInstanceId(getDefaultInstanceId(instance.getIpAddress(), propertyResolver));

        instance.setPreferIpAddress(preferIpAddress);
        if (managementPort != nonSecurePort && managementPort != 0) {
            if (StringUtils.hasText(hostname)) {
                instance.setHostname(hostname);
            }
            String statusPageUrlPath = eurekaPropertyResolver.getProperty("statusPageUrlPath");
            String healthCheckUrlPath = eurekaPropertyResolver.getProperty("healthCheckUrlPath");
            if (!managementContextPath.endsWith("/")) {
                managementContextPath = managementContextPath + "/";
            }
            if (StringUtils.hasText(statusPageUrlPath)) {
                instance.setStatusPageUrlPath(statusPageUrlPath);
            }
            if (StringUtils.hasText(healthCheckUrlPath)) {
                instance.setHealthCheckUrlPath(healthCheckUrlPath);
            }
            String scheme = instance.getSecurePortEnabled() ? "https" : "http";
            URL base = new URL(scheme, instance.getHostname(), managementPort, managementContextPath);
            instance.setStatusPageUrl(new URL(base, StringUtils.trimLeadingCharacter(instance.getStatusPageUrlPath(), '/')).toString());
            instance.setHealthCheckUrl(new URL(base, StringUtils.trimLeadingCharacter(instance.getHealthCheckUrlPath(), '/')).toString());
        }
        return instance;
    }

    /****************************** START 新增部分 ***************************/

    private static final String SEPARATOR = ":";

    public static String getDefaultInstanceId(String hostname, PropertyResolver resolver) {
        RelaxedPropertyResolver relaxed = new RelaxedPropertyResolver(resolver);
        String vcapInstanceId = relaxed.getProperty("vcap.application.instance_id");
        if (StringUtils.hasText(vcapInstanceId)) {
            return vcapInstanceId;
        }

        if (hostname == null) {
            hostname = relaxed.getProperty("spring.cloud.client.hostname");
        }
        String appName = relaxed.getProperty("spring.application.name");

        String namePart = combineParts(hostname, SEPARATOR, appName);

        String indexPart = relaxed.getProperty("spring.application.instance_id",
                relaxed.getProperty("server.port"));

        return combineParts(namePart, SEPARATOR, indexPart);
    }

    public static String combineParts(String firstPart, String separator, String secondPart) {
        String combined = null;
        if (firstPart != null && secondPart != null) {
            combined = firstPart + separator + secondPart;
        } else if (firstPart != null) {
            combined = firstPart;
        } else if (secondPart != null) {
            combined = secondPart;
        }
        return combined;
    }

    /****************************** END 新增部分 ***************************/
}
