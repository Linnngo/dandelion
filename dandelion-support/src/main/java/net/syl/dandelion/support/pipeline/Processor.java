package net.syl.dandelion.support.pipeline;

/**
 * 业务执行器
 *
 */
public interface Processor<T> {

    /**
     * 真正处理逻辑
     * @param context
     */
    void process(ProcessContext<T> context);
}