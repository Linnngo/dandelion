package net.syl.dandelion.support.aop.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import net.syl.dandelion.common.enums.RespStatusEnum;
import net.syl.dandelion.common.vo.BasicResultVO;
import net.syl.dandelion.support.aop.AnchorLog;
import net.syl.dandelion.support.aop.SPelContextHolder;
import net.syl.dandelion.support.aop.annotation.Anchor;
import net.syl.dandelion.support.aop.service.LogListener;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@Slf4j
@Aspect
public class AnchorAspect {

    @Autowired(required = false)
    private LogListener logListener;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Around("@annotation(annotation)")
    public Object doAround(ProceedingJoinPoint joinPoint, Anchor annotation) throws Throwable {
        Object result = null;

        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            SPelContextHolder.putVariables("response", BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
            throw throwable;
        } finally {
            AnchorLog anchorLog = resolveExpress(joinPoint, annotation);
            if (CollUtil.isNotEmpty(anchorLog.getReceivers())){
                String logJson = JSON.toJSONString(anchorLog,
                        SerializerFeature.WriteMapNullValue,
                        SerializerFeature.WriteClassName);
                if (logListener != null) {
                    logListener.createLog(logJson);
                }else {
                    log.info(logJson);
                }
            }
            SPelContextHolder.clearContext();
        }
        return result;
    }

    public AnchorLog resolveExpress(ProceedingJoinPoint joinPoint, Anchor annotation){
        String bizIdSPel = annotation.bizId();
        String responseSPel = annotation.response();
        String templateIdSPel = annotation.templateId();
        String receiversSPel = annotation.receivers();
        Long bizId = null;
        Long templateId = null;
        BasicResultVO response = null;
        Set<String> receivers = null;

        Object[] arguments = joinPoint.getArgs();
        Method method = getMethod(joinPoint);
        EvaluationContext context = SPelContextHolder.getContext();

        AnchorLog anchorLog;
        try {
            // 定义上下文，添加变量到上下文中
            String[] params = discoverer.getParameterNames(method);
            if (params != null) {
                for (int len = 0; len < params.length; len++) {
                    context.setVariable(params[len], arguments[len]);
                }
            }

            // bizId 处理：直接传入字符串会抛出异常，写入默认传入的字符串
            if (StrUtil.isNotBlank(bizIdSPel)) {
                Expression bizIdExpression = parser.parseExpression(bizIdSPel);
                bizId = bizIdExpression.getValue(context, Long.class);
            }

            // responseSPel 处理：写入默认传入的字符串
            if (StrUtil.isNotBlank(responseSPel)) {
                Expression msgExpression = parser.parseExpression(responseSPel);
                response = msgExpression.getValue(context, BasicResultVO.class);
            }

            // templateId 处理：写入默认传入的字符串
            if (StrUtil.isNotBlank(templateIdSPel)) {
                Expression msgExpression = parser.parseExpression(templateIdSPel);
                templateId = msgExpression.getValue(context, Long.class);
            }

            // receivers 处理：写入默认传入的字符串
            if (StrUtil.isNotBlank(receiversSPel)) {
                Expression msgExpression = parser.parseExpression(receiversSPel);
                receivers = msgExpression.getValue(context, Set.class);
            }
        } catch (Exception e){
            log.error("SystemLogAspect resolveExpress error", e);
        } finally {
            anchorLog = AnchorLog.builder().bizId(bizId)
                    .response(response)
                    .templateId(templateId)
                    .receivers(receivers)
                    .localDateTime(LocalDateTime.now())
                    .tag(annotation.tag())
                    .build();
        }
        return anchorLog;
    }

    protected Method getMethod(ProceedingJoinPoint joinPoint) {
        Method method = null;
        try {
            Signature signature = joinPoint.getSignature();
            MethodSignature ms = (MethodSignature) signature;
            Object target = joinPoint.getTarget();
            method = target.getClass().getMethod(ms.getName(), ms.getParameterTypes());
        } catch (NoSuchMethodException e) {
            log.error("SystemLogAspect getMethod error", e);
        }
        return method;
    }
}
