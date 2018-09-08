package com.lyl.study.cloud.base.config;

import com.alibaba.boot.dubbo.autoconfigure.DubboAutoConfiguration;
import com.alibaba.boot.dubbo.autoconfigure.SingleDubboConfigBindingProperties;
import com.alibaba.dubbo.config.AbstractConfig;
import com.alibaba.dubbo.config.spring.ServiceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

import static com.alibaba.boot.dubbo.util.DubboUtils.DUBBO_PREFIX;
import static com.alibaba.boot.dubbo.util.DubboUtils.MULTIPLE_CONFIG_PROPERTY_NAME;

@Configuration
@ConditionalOnProperty(prefix = DUBBO_PREFIX, name = "enabled", matchIfMissing = true, havingValue = "true")
@ConditionalOnClass(AbstractConfig.class)
@AutoConfigureAfter(DubboAutoConfiguration.class)
public class DubboCommonConfig {
    @Bean
    @ConditionalOnProperty(name = MULTIPLE_CONFIG_PROPERTY_NAME, havingValue = "false", matchIfMissing = true)
    public SingleDubboConfigServiceBeanAware singleDubboConfigServiceBeanAware() {
        return new SingleDubboConfigServiceBeanAware();
    }


    /**
     * 处理单Dubbo注册源的一些坑
     */
    public static class SingleDubboConfigServiceBeanAware implements BeanPostProcessor {
        protected final Logger logger = LoggerFactory.getLogger(this.getClass());

        private SingleDubboConfigBindingProperties singleDubboConfig;

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            // 某些配置赋值在SingleDubboConfigBindingProperties中，但没有配置到ServiceBean上
            if (bean instanceof ServiceBean) {
                BeanUtils.copyProperties(singleDubboConfig, bean);
            } else if (bean instanceof SingleDubboConfigBindingProperties) {
                singleDubboConfig = (SingleDubboConfigBindingProperties) bean;
                if (singleDubboConfig.getProtocol() != null && singleDubboConfig.getProtocol().getHost() != null) {
                    String ip = resolveProtocolHost(singleDubboConfig.getProtocol().getHost());
                    logger.info("选取作为服务提供者的注册IP: {}", ip);
                    singleDubboConfig.getProtocol().setHost(ip);
                }
            }
            return bean;
        }

        /**
         * 注册Host选取
         *
         * @param pattern IP地址、IP前缀、正则表达式或主机名
         * @return 符合Pattern规则Host
         */
        protected String resolveProtocolHost(String pattern) {
            try {
                Enumeration<NetworkInterface> interfaces;
                interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface ni = interfaces.nextElement();


                    Enumeration<InetAddress> addresss = ni.getInetAddresses();
                    while (addresss.hasMoreElements()) {
                        InetAddress nextElement = addresss.nextElement();
                        String hostAddress = nextElement.getHostAddress();
                        if (logger.isDebugEnabled()) {
                            logger.debug("检测网卡[{}]的IP: {}", ni.getName(), hostAddress);
                        }

                        if (pattern.equals(hostAddress) || hostAddress.startsWith(pattern)
                                || Pattern.matches(pattern, hostAddress)) {
                            return hostAddress;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 所有IP都没配对上，则直接用回Pattern作为注册的Host（这种算法兼容主机名注册的方式）
            return pattern;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }
}
