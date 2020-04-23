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

package com.rpcpostman.config;

import com.rpcpostman.service.repository.redis.RedisRepository;
import com.rpcpostman.service.maven.Maven;
import com.rpcpostman.service.repository.redis.RedisKeys;
import com.rpcpostman.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author everythingbest
 * 启动的时候systemInit()加载需要的zk地址和已经创建的服务
 */
@Configuration
public class AppConfig {

    @Value("${nexus.url}")
    private String nexusPath;

    @Value("${dubbo.api.jar.dir}")
    String apiJarPath;

    @Autowired
    private RedisRepository redisRepository;

    @Bean
    Maven mavenProcessor(){

        return new Maven(nexusPath,apiJarPath);
    }

    @Bean
    Initializer initializer() throws Exception {

        Initializer initializer = new Initializer();

        //统一设置路径入口,其他地方通过System.getProperty获取
        System.setProperty(Constant.USER_HOME, apiJarPath);

        initializer.copySettingXml(apiJarPath);

        initializer.loadZkAddress(redisRepository);

        initializer.loadCreatedService(redisRepository, RedisKeys.RPC_MODEL_KEY);

        return initializer;
    }
}
