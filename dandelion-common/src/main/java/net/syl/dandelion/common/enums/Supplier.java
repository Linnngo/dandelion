package net.syl.dandelion.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum Supplier {

    TENCENT(10, "tencent", "腾讯"),
    YUNPIAN(20, "yunpian", "云片"),

    ;

    /**
     * 编码值
     */
    private Integer code;

    private String name;

    /**
     * 描述
     */
    private String description;

    public static String getSupplierNameByCode(Integer code){
        Supplier[] values = values();
        for (Supplier supplier : values) {
            if (supplier.getCode().equals(code)) {
                return supplier.getName();
            }
        }
        return null;
    }
}
