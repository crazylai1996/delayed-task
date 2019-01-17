package gdou.laiminghai;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@FunctionalInterface
interface CallWithJedis{
    public void call(Jedis jedis);
}

public class RedisPoolWrapper{

    private JedisPool jedisPool;

    public RedisPoolWrapper(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //设置最大连接数
        jedisPoolConfig.setMaxTotal(30);
        //设置最大空闲连接数
        jedisPoolConfig.setMaxIdle(10);
        this.jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6379);
    }

    public void execute(CallWithJedis caller){
        try (Jedis jedis = jedisPool.getResource()){
            jedis.auth("redis");
            caller.call(jedis);
        }
    }
}