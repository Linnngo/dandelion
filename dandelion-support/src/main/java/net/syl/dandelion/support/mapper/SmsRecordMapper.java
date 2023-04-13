package net.syl.dandelion.support.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.syl.dandelion.support.entity.SmsRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SmsRecordMapper extends BaseMapper<SmsRecord> {

    int batchInsert(List<SmsRecord> list);
}
