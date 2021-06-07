package com.jeetx.common.redis;

import java.util.List;
import java.util.Map;

public abstract interface JedisClient
{
  public abstract String get(String paramString);

  public abstract String set(String paramString1, String paramString2);

  public abstract String hget(String paramString1, String paramString2);

  public abstract String hmset(String paramString, Map<String, String> paramMap);

  public abstract List<String> hmget(String paramString, String[] paramArrayOfString);

  public abstract long hset(String paramString1, String paramString2, String paramString3);

  public abstract long incr(String paramString);

  public abstract long expire(String paramString, int paramInt);

  public abstract long ttl(String paramString);

  public abstract long del(String paramString);

  public abstract long hdel(String paramString1, String paramString2);
}