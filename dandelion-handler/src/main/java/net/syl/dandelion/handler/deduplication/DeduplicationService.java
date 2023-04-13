package net.syl.dandelion.handler.deduplication;

import net.syl.dandelion.support.entity.TaskInfo;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface DeduplicationService {

    public Set<String> deduplication(TaskInfo taskInfo);

    public String deduplicationKey(TaskInfo taskInfo, String receiver);
}
