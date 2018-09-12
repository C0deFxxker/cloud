package com.lyl.study.cloud.base.config;

import com.alibaba.boot.dubbo.autoconfigure.DubboAutoConfiguration;
import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.config.spring.ServiceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.regex.Pattern;

import static com.alibaba.boot.dubbo.util.DubboUtils.DUBBO_PREFIX;
import static com.alibaba.boot.dubbo.util.DubboUtils.MULTIPLE_CONFIG_PROPERTY_NAME;

/**
 * @author liyilin
 */
@Configuration
@ConditionalOnProperty(prefix = DUBBO_PREFIX, name = "enabled", matchIfMissing = true, havingValue = "true")
@ConditionalOnClass(AbstractConfig.class)
@AutoConfigureBefore(DubboAutoConfiguration.class)
public class DubboCommonConfig {

    /**
     * 处理单Dubbo注册源的一些坑
     */
    @ConditionalOnProperty(name = MULTIPLE_CONFIG_PROPERTY_NAME, havingValue = "false", matchIfMissing = true)
    @EnableConfigurationProperties(SingleDubboConfigBindingProperties.class)
    static class SingleDubboConfig {
        @Bean
        public SingleDubboConfigServiceBeanAware singleDubboConfigServiceBeanAware() {
            return new SingleDubboConfigServiceBeanAware();
        }
    }

    /**
     * 让Dubbo框架配置更简单，并且加入了服务注册IP的正则表达式匹配功能
     */
    public static class SingleDubboConfigServiceBeanAware implements BeanPostProcessor, InitializingBean {
        protected final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Autowired
        private SingleDubboConfigBindingProperties singleDubboConfig;

        private Set<String> networkInterfaceAddressSet = new HashSet<>();

        public SingleDubboConfigServiceBeanAware() {
            initNetworkInterfaceAddressSet();
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            // 程序运行参数具有最大优先级
            String registryIp = System.getProperty(Constants.DUBBO_IP_TO_REGISTRY);

            // 然后是配置文件
            if (registryIp == null
                    && singleDubboConfig.getProtocol() != null
                    && singleDubboConfig.getProtocol().getHost() != null) {
                registryIp = resolveProtocolHost(singleDubboConfig.getProtocol().getHost());
            }

            // 最后是环境变量
            if (registryIp == null) {
                registryIp = System.getenv(Constants.DUBBO_IP_TO_REGISTRY);
            }

            // 如果上述都没有确定注册IP，则随便使用一个本机IP
            if (registryIp == null) {
                registryIp = NetUtils.getLocalHost();
            }

            if (registryIp != null) {
                logger.info("选取作为服务提供者的默认注册IP: {}", registryIp);
                System.setProperty(Constants.DUBBO_IP_TO_REGISTRY, registryIp);
                if (singleDubboConfig.getProtocol() == null) {
                    singleDubboConfig.setProtocol(new ProtocolConfig("dubbo"));
                }
                singleDubboConfig.getProtocol().setHost(registryIp);
            }
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            // 某些配置赋值在application.yml中，但没有配置到ServiceBean上
            if (bean instanceof ServiceBean) {
                ServiceBean serviceBean = (ServiceBean) bean;
                // 处理@Service注解上的服务注册IP地址
                if (serviceBean.getProtocol() != null && serviceBean.getProtocol().getHost() != null) {
                    String host = resolveProtocolHost(serviceBean.getProtocol().getHost());
                    serviceBean.getProtocol().setHost(host);
                }
                // 某些属性在@Service注解上没有给出，这里填充默认值
                polulateDefaultField(serviceBean);
            }
            return bean;
        }

        /**
         * 为没有配置的属性填充默认值
         *
         * @param bean 待填充Bean
         */
        protected void polulateDefaultField(ServiceBean bean) {
            Field[] declaredFields = SingleDubboConfigBindingProperties.class.getDeclaredFields();
            Arrays.asList(declaredFields).forEach(field -> {
                try {
                    Method getter = fetchGetter(bean.getClass(), field.getName());
                    // 把null的属性填充上
                    if (getter.invoke(bean) == null) {
                        field.setAccessible(true);
                        Object val = field.get(singleDubboConfig);
                        Method setter = fetchSetter(bean.getClass(), field.getName(), field.getType());
                        setter.invoke(bean, val);
                    }
                } catch (Exception ignored) {
                }
            });
        }

        /**
         * 获取某个属性的Setter方法
         */
        private Method fetchGetter(Class<?> clazz, String fieldName) {
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            return ReflectionUtils.findMethod(clazz, getterName);
        }

        /**
         * 获取某个属性的Setter方法
         */
        private Method fetchSetter(Class<?> clazz, String fieldName, Class<?> fieldType) {
            String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            return ReflectionUtils.findMethod(clazz, setterName, fieldType);
        }

        /**
         * 获取本机所有网卡的IP地址
         */
        protected void initNetworkInterfaceAddressSet() {
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
                        networkInterfaceAddressSet.add(hostAddress);
                    }
                }
            } catch (Exception e) {
                logger.error("网络IP获取失败，无法注册服务");
                e.printStackTrace();
                System.exit(255);
            }
        }

        /**
         * 注册Host选取（处理多网卡IP注册问题）
         *
         * @param pattern IP地址、IP前缀、正则表达式或主机名
         * @return 符合Pattern规则Host
         */
        protected String resolveProtocolHost(String pattern) {
            Optional<String> optional = networkInterfaceAddressSet.stream().filter(
                    hostAddress -> (pattern.equals(hostAddress)
                            || hostAddress.startsWith(pattern)
                            || Pattern.matches(pattern, hostAddress))
            ).findFirst();
            // 所有网卡地址都没配对上，则直接用回Pattern作为注册的Host（这种算法兼容主机名注册的方式）
            return optional.orElse(pattern);
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }

    @ConfigurationProperties(prefix = DUBBO_PREFIX)
    public static class SingleDubboConfigBindingProperties {

        /**
         * {@link ApplicationConfig} property
         */
        @NestedConfigurationProperty
        private ApplicationConfig application;

        /**
         * {@link ModuleConfig} property
         */
        @NestedConfigurationProperty
        private ModuleConfig module;

        /**
         * {@link RegistryConfig} property
         */
        @NestedConfigurationProperty
        private RegistryConfig registry;

        /**
         * {@link ProtocolConfig} property
         */
        @NestedConfigurationProperty
        private ProtocolConfig protocol;

        /**
         * {@link MonitorConfig} property
         */
        @NestedConfigurationProperty
        private MonitorConfig monitor;

        /**
         * {@link ProviderConfig} property
         */
        @NestedConfigurationProperty
        private ProviderConfig provider;

        /**
         * {@link ConsumerConfig} property
         */
        @NestedConfigurationProperty
        private ConsumerConfig consumer;

        public ApplicationConfig getApplication() {
            return application;
        }

        public void setApplication(ApplicationConfig application) {
            this.application = application;
        }

        public ModuleConfig getModule() {
            return module;
        }

        public void setModule(ModuleConfig module) {
            this.module = module;
        }

        public RegistryConfig getRegistry() {
            return registry;
        }

        public void setRegistry(RegistryConfig registry) {
            this.registry = registry;
        }

        public ProtocolConfig getProtocol() {
            return protocol;
        }

        public void setProtocol(ProtocolConfig protocol) {
            this.protocol = protocol;
        }

        public MonitorConfig getMonitor() {
            return monitor;
        }

        public void setMonitor(MonitorConfig monitor) {
            this.monitor = monitor;
        }

        public ProviderConfig getProvider() {
            return provider;
        }

        public void setProvider(ProviderConfig provider) {
            this.provider = provider;
        }

        public ConsumerConfig getConsumer() {
            return consumer;
        }

        public void setConsumer(ConsumerConfig consumer) {
            this.consumer = consumer;
        }
    }
}
