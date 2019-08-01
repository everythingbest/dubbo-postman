/*
 * MIT License
 *
 * Copyright (c) 2019 everythingbest
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dubbo.postman.config;

import com.dubbo.postman.util.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author everythingbest
 * redis连接相关的配置
 */
@Configuration
public class RedisConfig {

    @Value("${sentinel.master}")
    String nodeMaster;

    @Value("${redis.password}")
    String nodePassword;

    @Value("${node1.ip}")
    String node1Ip;

    @Value("${node2.ip}")
    String node2Ip;

    @Value("${node3.ip}")
    String node3Ip;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {

        String[] node1 = node1Ip.split(Constant.PORT_SPLITTER);
        String[] node2 = node1Ip.split(Constant.PORT_SPLITTER);
        String[] node3 = node1Ip.split(Constant.PORT_SPLITTER);

        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master(nodeMaster)
                .sentinel(node1[0], Integer.valueOf(node1[1]))
                .sentinel(node2[0], Integer.valueOf(node2[1]))
                .sentinel(node3[0], Integer.valueOf(node3[1]));

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfig);

        jedisConnectionFactory.setPassword(nodePassword);

        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setTestOnBorrow(true);

        jedisConnectionFactory.setPoolConfig(poolConfig);

        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate redisTemplate(){

        RedisTemplate redisTemplate = new RedisTemplate();

        redisTemplate.setConnectionFactory(jedisConnectionFactory());

        return redisTemplate;
    }
}
