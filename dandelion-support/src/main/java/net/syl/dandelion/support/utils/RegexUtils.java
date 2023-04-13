package net.syl.dandelion.support.utils;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import net.syl.dandelion.common.enums.IdType;

import java.lang.reflect.Field;
import java.util.Locale;

public class RegexUtils {

    /**
     * 手机号正则
     */
    public static final String PHONE_REGEX = "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$";
    /**
     * 邮箱正则
     */
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    public static Boolean isInvalid(Integer idType, String data) {
        String fieldName = IdType.getIdTypeByCode(idType).toUpperCase(Locale.ROOT) +"_REGEX";
        Field field = ReflectUtil.getField(RegexUtils.class, fieldName);
        Object regexObj = ReflectUtil.getFieldValue(null, field);
        if (regexObj == null) {
            return true;
        }
        return mismatch(data, regexObj.toString());
    }

    /**
     * 是否是无效手机格式
     * @param phone 要校验的手机号
     * @return true:符合，false：不符合
     */
    public static boolean isPhoneInvalid(String phone){
        return mismatch(phone, PHONE_REGEX);
    }
    /**
     * 是否是无效邮箱格式
     * @param email 要校验的邮箱
     * @return true:符合，false：不符合
     */
    public static boolean isEmailInvalid(String email){
        return mismatch(email, EMAIL_REGEX);
    }

    // 校验是否不符合正则格式
    private static boolean mismatch(String str, String regex){
        if (StrUtil.isBlank(str)) {
            return false;
        }
        return str.matches(regex);
    }
}
