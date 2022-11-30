package com.itdfq.common.utils.Serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额小数处理保留两位小数
 * @Author: QianMo
 * @Date: 2021/11/16 13:01
 * @Description:
 */
public class BigDecimalSerialize extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializerProvider) throws
            IOException {
        if (value != null) {
            gen.writeString(String.valueOf(value.setScale(2, RoundingMode.HALF_DOWN)));
        } else {
            gen.writeString((String) null);
        }

    }
}
