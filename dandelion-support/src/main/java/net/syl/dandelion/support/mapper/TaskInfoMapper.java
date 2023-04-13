package net.syl.dandelion.support.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.syl.dandelion.support.entity.TaskInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {

    int batchInsert(List<TaskInfo> list);
}
