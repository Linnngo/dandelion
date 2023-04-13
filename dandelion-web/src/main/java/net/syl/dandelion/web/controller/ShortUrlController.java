package net.syl.dandelion.web.controller;

import net.syl.dandelion.support.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static net.syl.dandelion.common.constant.DandelionConstant.*;

@Controller
@RequestMapping("/short")
public class ShortUrlController {

    @Autowired
    private UrlUtils urlUtils;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("{shortUrl}")
    public void saveOrUpdate(HttpServletResponse response, @PathVariable String shortUrl) throws IOException {
        System.out.println("页面被点击一次");
        System.out.println(shortUrl);
        response.setStatus(302);

        Map param = urlUtils.getRealUrl(shortUrl);

        String businessId = param.get("businessId").toString();
        String receiver = param.get("receiver").toString();
        String url = param.get("url").toString();
        // 计算点击率，保存已点击的人
        redisTemplate.opsForZSet().add(STREAM_SHORTURL_CLICKED + businessId, receiver, System.currentTimeMillis());
        Double allNum = redisTemplate.opsForList().size(STREAM_SHORTURL + businessId).doubleValue();
        Double hasClickedNum = redisTemplate.opsForZSet().size(STREAM_SHORTURL_CLICKED + businessId).doubleValue();

        // 保存点击时间及顺序
        redisTemplate.opsForZSet().addIfAbsent(STREAM_SHORTURL_TIME + businessId, receiver, new Date().getTime());
        // 保存点击次数
        redisTemplate.opsForHash().increment(STREAM_SHORTURL + shortUrl, "count", 1);
        System.out.println(url);
        response.sendRedirect(url);
    }
}