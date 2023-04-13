package net.syl.dandelion.handler.script;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * sendAccount->SmsScript的映射关系
 *
 */
@Component
public class SmsScriptHolder {

    private Map<Integer, SmsScript> handlers = new HashMap<>(8);

    public void putHandler(Integer supplierCode, SmsScript handler) {
        handlers.put(supplierCode, handler);
    }
    public SmsScript route(Integer supplierCode) {
        SmsScript smsScript = handlers.get(supplierCode);
        return smsScript != null ? smsScript : handlers.get(10);
    }
}
