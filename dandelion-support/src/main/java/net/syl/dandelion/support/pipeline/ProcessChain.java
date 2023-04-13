package net.syl.dandelion.support.pipeline;

import java.util.List;

/**
 * 业务执行模板（把责任链的逻辑串起来）
 *
 */
public class ProcessChain {

    private List<Processor> processList;

    public List<Processor> getProcessList() {
        return processList;
    }
    public void setProcessList(List<Processor> processList) {
        this.processList = processList;
    }
}