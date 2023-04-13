package net.syl.dandelion.cron.resolver;


import cn.hutool.core.util.ArrayUtil;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.cron.constants.CronConstants;
import net.syl.dandelion.cron.pending.MessageParamHolder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CSVResolver {

    @Async
    public void resolve(String path, Long messageTemplateId){
        long count = 0;
        String[] head;
        List<Map<String, String>> list = new ArrayList();

        try {
            LineIterator it = FileUtils.lineIterator(new File(path), "UTF-8");
            if (!it.hasNext()){
                return;
            }
            head = it.nextLine().split(",");
            // 没有收件人信息
            if (!ArrayUtil.contains(head, CronConstants.RECEIVERS_KEY)){
                log.error("CSVResolver#resolve not receivers! path{}", path);
                return;
            }
            MessageParamHolder.put(path + messageTemplateId, list);

            while (it.hasNext()){
                String[] param = it.nextLine().split(",");

                // 将csv每一行转换成kv结构
                Map<String, String> paramMap = flapMap(head, param);
                // 放入list，给消费者消费
                list.add(paramMap);
                count++;
            }
        } catch (IOException e) {
            log.error("CSVResolver#resolve file not found:{}"
                    , Throwables.getStackTraceAsString(e));
        } catch (Exception e) {
            log.error("CSVResolver#resolve count:{} filePath:{} exception:{}"
                    , count, path, Throwables.getStackTraceAsString(e));
        }

    }

    public Map<String, String> flapMap(String[] head, String[] param) {
        HashMap<String, String> paramMap = new HashMap();
        for (int i = 0; i < head.length; i++) {
            paramMap.put(head[i], param[i]);
        }
        return paramMap;
    }
}
