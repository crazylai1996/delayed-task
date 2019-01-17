package gdou.laiminghai.delayedtaskrabbitmq.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendDelayedMsg(String data){
        rabbitTemplate.convertAndSend(RabbitMQConfig.DELAYED_EXCHANGE_NAME, RabbitMQConfig.DELAYED_ROUTING_KEY, data, message -> {
            message.getMessageProperties().setExpiration(5 * 1000 + "");
            return message;
        });
    }

}
