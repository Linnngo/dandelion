package net.syl.dandelion.support.mapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes({Object.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class WriteClassNameTypeHandler extends FastjsonTypeHandler {
    private final Class<?> type;

    public WriteClassNameTypeHandler(Class<?> type) {
        super(type);
        this.type = type;
    }

    @Override
    protected Object parse(String json) {
        return JSON.parseObject(json, this.type);
    }

    @Override
    protected String toJson(Object obj) {
        return JSON.toJSONString(obj, SerializerFeature.WriteClassName);
    }
}
