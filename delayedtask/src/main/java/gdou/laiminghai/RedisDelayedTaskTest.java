package gdou.laiminghai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class RedisDelayedTaskTest {
    public static void main(String[] args) {
        RedisTasksExecutor<String> executor = new RedisTasksExecutor<>("zset");
        new Thread(() -> {
            for (int i = 0; i < 10; i++){
                int ii = new Random().nextInt(10);
                executor.submit((ii + 1) + "s后执行", (ii+1) * 1000);
            }
        }).start();
        new Thread(() -> {
            executor.loop();
        }).start();
    }
}

class RedisTasksExecutor<T>{

    private String queueKey;

    private RedisPoolWrapper redisPoolWrapper;

    public RedisTasksExecutor(String queueKey){
        this.queueKey = queueKey;
        this.redisPoolWrapper = new RedisPoolWrapper();
    }

    public void submit(T msg,final long delayTime){
        final DelayTask<T> task = new DelayTask<T>();
        task.id = UUID.randomUUID().toString();
        task.msg = msg;
        redisPoolWrapper.execute((Jedis jedis) -> {

            jedis.zadd(queueKey, System.currentTimeMillis() + delayTime, JSON.toJSONString(task));
        });
    }

    public void handle(T msg){
        System.out.println(msg);
    }

    public void loop(){
        String luaScript = ScriptLoad.load("delayed.lua");
        redisPoolWrapper.execute((Jedis jedis) -> {
            for (;;){
                String taskStr = (String) jedis.eval(luaScript, Arrays.asList(queueKey), Arrays.asList("0", String.valueOf(System.currentTimeMillis())));
                if(taskStr == null){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    continue;
                }
                DelayTask<T> task = JSON.parseObject(taskStr, new TypeReference<DelayTask<T>>(){}.getType());
                this.handle(task.msg);
            }
        });
    }

    static class DelayTask<T>{
        public String id;
        public T msg;
    }

}



