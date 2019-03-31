package com.*.*.jms.listener;

import com.*.*.cache.BusRealDataCacheService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * @author 
 * @date 
 * @Description:
 * @since V1.0 
 */
public class ConsumerGPS implements MessageListener {
    @Override
    public void onMessage(Message message) {
        byte[] body, protobyte;
        body = message.getBody();
        // 总长度
        int msgLength = (short) (((body[0] << 8) | body[1] & 0xff));
        // 类型 2-gps 3-到离站 4-违规数据 12-终端状态上报数据 13-客流数据
        int msgType = (short) (((body[2] << 8) | body[3] & 0xff));
        protobyte = new byte[msgLength - 2];
        System.arraycopy(body, 4, protobyte, 0, msgLength - 2);
        // 更新车辆实时数据缓存
        BusRealDataCacheService.addOrUpdateCache(msgType, protobyte);
    }
}
