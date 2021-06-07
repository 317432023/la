package com.jeetx.common.redis;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.JedisCluster;

public class JedisClientCluster
  implements JedisClient
{

  @Autowired
  private JedisCluster jedisCluster;

  public String get(String key)
  {
    return this.jedisCluster.get(key);
  }

  public String set(String key, String value)
  {
    return this.jedisCluster.set(key, value);
  }

  public String hget(String hkey, String key)
  {
    return this.jedisCluster.hget(hkey, key);
  }

  public String hmset(String hmkey, Map<String, String> map)
  {
    String s = this.jedisCluster.hmset(hmkey, map);
    return s;
  }

  public List<String> hmget(String hmkey, String[] field)
  {
    List ls = this.jedisCluster.hmget(hmkey, field);
    return ls;
  }

  public long hset(String hkey, String key, String value)
  {
    return this.jedisCluster.hset(hkey, key, value).longValue();
  }

  public long incr(String key)
  {
    return this.jedisCluster.incr(key).longValue();
  }

  public long expire(String key, int second)
  {
    return this.jedisCluster.expire(key, second).longValue();
  }

  public long ttl(String key)
  {
    return this.jedisCluster.ttl(key).longValue();
  }

  public long del(String key)
  {
    return this.jedisCluster.del(key).longValue();
  }

  public long hdel(String hkey, String key)
  {
    return this.jedisCluster.hdel(hkey, new String[] { key }).longValue();
  }
}