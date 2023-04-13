package net.syl.dandelion.support.utils;

import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

import static net.syl.dandelion.common.constant.DandelionConstant.STREAM_SHORTURL;

@Component
public class UrlUtils {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String baseString = "0123456789abcdefghijklmnopgrstuvwxyzABCDEFGHYJKLMNOPGRSTUVWXYZ";
    private static final int length = 6;

    public String getShortUrl(String url, String receiver, Long businessId) {
        String shortUrl = RandomUtil.randomString(baseString, length);
//        url = url + "?receiver=" + receiver + "&businessId=" + businessId;
        while (redisTemplate.hasKey(STREAM_SHORTURL + shortUrl)) {
            shortUrl = RandomUtil.randomString(baseString, length);
        }
        if (!url.contains("http")) {
            url = "http://" + url;
        }
        redisTemplate.opsForHash().put(STREAM_SHORTURL + shortUrl, "url", url);
        redisTemplate.opsForHash().put(STREAM_SHORTURL + shortUrl, "receiver", receiver);
        redisTemplate.opsForHash().put(STREAM_SHORTURL + shortUrl, "businessId", businessId.toString());
        redisTemplate.opsForList().leftPush(STREAM_SHORTURL + businessId, shortUrl);
        return shortUrl;
    }

    public Map getRealUrl(String shortUrl) {
        return redisTemplate.opsForHash().entries(STREAM_SHORTURL + shortUrl);
    }
}
