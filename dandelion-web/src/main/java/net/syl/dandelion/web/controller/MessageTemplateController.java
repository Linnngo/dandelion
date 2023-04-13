package net.syl.dandelion.web.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.service.client.SendClient;
import net.syl.dandelion.service.entity.SendRequest;
import net.syl.dandelion.service.entity.SendResponse;
import net.syl.dandelion.support.entity.MessageTemplate;
import net.syl.dandelion.web.service.MessageTemplateService;
import net.syl.dandelion.web.vo.MessageTemplateParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RefreshScope
@RequestMapping("/messageTemplate")
@CrossOrigin(origins = "${dandelion.web.admin.host}", allowCredentials = "true", allowedHeaders = "*")
public class MessageTemplateController {

    @Autowired
    private MessageTemplateService messageTemplateService;
    @Autowired
    private SendClient sendClient;
    @Value("${dandelion.web.upload.crowd.path}")
    private String dataPath;

    /**
     * 如果Id存在，则修改
     * 如果Id不存在，则保存
     */
    @PostMapping("/save")
    public BasicResultVO saveOrUpdate(@RequestBody MessageTemplate messageTemplate) {
        System.out.println(messageTemplate);
        return messageTemplateService.saveOrUpdate(messageTemplate);
    }

    /**
     * 列表数据
     */
    @GetMapping("/list")
//    @ApiOperation("/列表页")
    public BasicResultVO queryList(MessageTemplateParam messageTemplateParam) {
        return messageTemplateService.queryList(messageTemplateParam);
    }

    /**
     * 根据Id查找
     */
    @GetMapping("query/{id}")
//    @ApiOperation("/根据Id查找")
    public BasicResultVO queryById(@PathVariable("id") Long id) {
        return messageTemplateService.queryById(id);
    }

    @DeleteMapping("delete/{id}")
//    @ApiOperation("/根据Id查找")
    public BasicResultVO deleteById(@PathVariable("id") String id) {
        if (StrUtil.isNotBlank(id)) {
            List<Long> idList = Arrays.stream(id.split(StrUtil.COMMA)).map(s -> Long.valueOf(s)).collect(Collectors.toList());
            return messageTemplateService.deleteById(idList);
        }
        return BasicResultVO.fail();
    }

    /**
     * 启动模板的定时任务
     */
    @PostMapping("start/{id}")
//    @ApiOperation("/启动模板的定时任务")
    public BasicResultVO start(@PathVariable("id") Long id) {
        return messageTemplateService.startCronTask(id);
    }

    /**
     * 暂停模板的定时任务
     */
    @PostMapping("stop/{id}")
//    @ApiOperation("/暂停模板的定时任务")
    public BasicResultVO stop(@PathVariable("id") Long id) {
        return messageTemplateService.stopCronTask(id);
    }

    /**
     * 上传人群文件
     */
    @PostMapping("upload")
//    @ApiOperation("/上传人群文件")
    public BasicResultVO upload(@RequestParam("file") MultipartFile file) {
        String filePath = new StringBuilder(dataPath)
                .append(IdUtil.fastSimpleUUID())
                .append(file.getOriginalFilename())
                .toString();
        try {
            File localFile = new File(filePath);
            if (!localFile.exists()) {
                localFile.mkdirs();
            }
            file.transferTo(localFile);


        } catch (Exception e) {
            log.error("MessageTemplateController#upload fail! e:{},params{}", Throwables.getStackTraceAsString(e), JSON.toJSONString(file));
            return BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR);
        }
        return BasicResultVO.success(MapUtil.of(new String[][]{{"value", filePath}}));
    }

    @PostMapping("/test")
    @ResponseBody
    public BasicResultVO test(@RequestBody SendRequest sendRequest){
        System.out.println(sendRequest);
        SendResponse send = sendClient.send(sendRequest);
        System.out.println(send);
        return BasicResultVO.success(send);
    }
}
