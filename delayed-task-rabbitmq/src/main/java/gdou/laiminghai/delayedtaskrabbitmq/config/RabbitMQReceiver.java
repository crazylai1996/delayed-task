package gdou.laiminghai.delayedtaskrabbitmq.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitMQReceiver {

    @RabbitListener(queues = {RabbitMQConfig.DELAYED_EXEC_QUEUE_NAME})
    public void delayedExec(String data, Message message, Channel channel){
        System.out.println("data:" + data);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
