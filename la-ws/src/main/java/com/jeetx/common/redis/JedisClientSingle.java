package com.jeetx.common.redis;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisClientSingle implements JedisClient
{

  @Autowired
  private JedisPool jedisPool;

  public String get(String key)
  {
    Jedis jedis = this.jedisPool.getResource();
    String string = jedis.get(key);
    jedis.close();
    return string;
  }

  public String set(String key, String value)
  {
    Jedis jedis = this.jedisPool.getResource();
    String string = jedis.set(key, value);
    jedis.close();
    return string;
  }

  public String hget(String hkey, String key)
  {
    Jedis jedis = this.jedisPool.getResource();
    String string = jedis.hget(hkey, key);
    jedis.close();
    return string;
  }

  public String hmset(String hmkey, Map<String, String> map)
  {
    Jedis jedis = this.jedisPool.getResource();
    String s = jedis.hmset(hmkey, map);
    jedis.close();
    return s;
  }

  public List<String> hmget(String hmkey, String[] field)
  {
    Jedis jedis = this.jedisPool.getResource();
    List ls = jedis.hmget(hmkey, field);
    jedis.close();
    return ls;
  }

  public long hset(String hkey, String key, String value)
  {
    Jedis jedis = this.jedisPool.getResource();
    Long result = jedis.hset(hkey, key, value);
    jedis.close();
    return result.longValue();
  }

  public long incr(String key)
  {
    Jedis jedis = this.jedisPool.getResource();
    Long result = jedis.incr(key);
    jedis.close();
    return result.longValue();
  }

  public long expire(String key, int second)
  {
    Jedis jedis = this.jedisPool.getResource();
    Long result = jedis.expire(key, second);
    jedis.close();
    return result.longValue();
  }

  public long ttl(String key)
  {
    Jedis jedis = this.jedisPool.getResource();
    Long result = jedis.ttl(key);
    jedis.close();
    return result.longValue();
  }

  public long del(String key)
  {
    Jedis jedis = this.jedisPool.getResource();
    Long result = jedis.del(key);
    jedis.close();
    return result.longValue();
  }

  public long hdel(String hkey, String key)
  {
    Jedis jedis = this.jedisPool.getResource();
    Long result = jedis.hdel(hkey, new String[] { key });
    jedis.close();
    return result.longValue();
  }
}