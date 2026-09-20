package com.cxcron.common.ip;

import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.Ip2Region;
import org.lionsoul.ip2region.service.InvalidConfigException;
import org.lionsoul.ip2region.xdb.InetAddressException;
import org.lionsoul.ip2region.xdb.XdbException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;

/**
 * IP 地址归属地查询服务。
 */
@Component
public class Ip2RegionService {

    private final Ip2Region ip2Region;

    public Ip2RegionService() {
        try (InputStream inputStream = new ClassPathResource("ip2region_v4.xdb").getInputStream()) {
            Config config = Config.custom()
                    .setXdbInputStream(inputStream)
                    .setCachePolicy(Config.BufferCache)
                    .asV4();
            this.ip2Region = Ip2Region.create(config, null);
        } catch (IOException | XdbException | InvalidConfigException exception) {
            throw new IllegalStateException("加载 IP 地址归属地数据库失败", exception);
        }
    }

    /**
     * 查询 IP 地址归属地。
     *
     * @param ip IP 地址
     * @return 国家、区域、省份、城市与运营商信息
     */
    public String search(String ip) {
        try {
            return ip2Region.search(ip);
        } catch (InetAddressException exception) {
            throw new IllegalArgumentException("IP 地址格式不正确", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("查询 IP 地址归属地失败", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("IP 地址归属地查询被中断", exception);
        }
    }

    @PreDestroy
    public void close() {
        try {
            ip2Region.close();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
