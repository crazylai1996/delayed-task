package gdou.laiminghai.delayedtaskrabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig
        implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    //Dead Letter Exchange
    public static final String DELAYED_EXEC_EXCHANGE_NAME = "delayed.exec.exchange";

    //Dead Letter Queue
    public static final String DELAYED_EXEC_QUEUE_NAME = "delayed.exec.queue";

    //Dead Letter Routing Key
    public static final String DELAYED_EXEC_ROUTING_KEY = "delayed.exec.routing.key";

    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";

    public static final String DELAYED_QUEUE_NAME = "delayed.queue";

    public static final String DELAYED_ROUTING_KEY = "delayed.routing.key";

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
        return rabbitTemplate;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("correlationData:" + correlationData + ",cause:" + cause);
        if(!ack){
            System.out.println("消息发送失败！");
        }else {
            System.out.println("消息发送成功！");
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("没有找到对应的队列!");
        System.out.println("message:" + message +
                ",replyCode:" + replyCode +
                ",replyText:" + replyText +
                ",exchange:" + exchange +
                ",routingKey:" + routingKey);
    }

    @Bean
    public Queue delayedQueue(){
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", DELAYED_EXEC_EXCHANGE_NAME);
        params.put("x-dead-letter-routing-key", DELAYED_EXEC_ROUTING_KEY);
//        params.put("x-message-ttl", 5 * 1000);
        return new Queue(DELAYED_QUEUE_NAME, true,false, false, params);
    }

    @Bean
    public DirectExchange delayedExchange(){
        return new DirectExchange(DELAYED_EXCHANGE_NAME);
    }

    @Bean
    public Binding delayedBind(){
        return BindingBuilder.bind(delayedQueue()).to(delayedExchange()).with(DELAYED_ROUTING_KEY);
    }

    @Bean
    public Queue delayedExecQueue(){
        return new Queue(DELAYED_EXEC_QUEUE_NAME,true);
    }

    @Bean
    public TopicExchange delayedExecExchange(){
        return new TopicExchange(DELAYED_EXEC_EXCHANGE_NAME);
    }

    @Bean
    public Binding delayedExecBind(){
        return BindingBuilder.bind(delayedExecQueue()).to(delayedExecExchange()).with(DELAYED_EXEC_ROUTING_KEY);
    }
}
