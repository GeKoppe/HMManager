package org.hmdms.hmmanager;

import jakarta.jms.ConnectionFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@SpringBootApplication
@EnableJms
class JmsBroker {
    @Bean
    public JmsListenerContainerFactory<?> hmmanagerFactory(ConnectionFactory connFact,
                                                      DefaultJmsListenerContainerFactoryConfigurer conf) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        conf.configure(factory, connFact);
        return factory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter conv = new MappingJackson2MessageConverter();
        conv.setTargetType(MessageType.TEXT);
        conv.setTypeIdPropertyName("_type");
        return conv;
    }

}
