/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aieval.common;

import java.time.Instant;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public record ApiResponse<T>(boolean success, String message, T data, Instant timestamp) {

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public static <T> ApiResponse<T> ok(String m, T d) {
        return new ApiResponse<>(true, m, d, Instant.now());
    }
}
