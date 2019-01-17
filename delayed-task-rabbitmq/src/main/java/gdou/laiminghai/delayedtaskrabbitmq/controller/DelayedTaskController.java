package gdou.laiminghai.delayedtaskrabbitmq.controller;

import gdou.laiminghai.delayedtaskrabbitmq.config.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DelayedTaskController {

    @Autowired
    private RabbitMQSender sender;

    @PostMapping("/send")
    public String send(String message){
        sender.sendDelayedMsg(message);
        return "";
    }
}
