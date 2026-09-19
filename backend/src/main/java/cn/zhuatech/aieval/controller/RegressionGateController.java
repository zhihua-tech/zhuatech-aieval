/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aieval.controller;

import cn.zhuatech.aieval.common.ApiResponse;
import cn.zhuatech.aieval.service.RegressionGateService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/ai-eval")
public class RegressionGateController {
    private final RegressionGateService service;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public RegressionGateController(RegressionGateService service) {
        this.service = service;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/regression-checks")
    @PreAuthorize("hasAnyRole('ADMIN','AUDITOR')")
    public ApiResponse<RegressionGateService.Result> evaluate(
            @Valid @RequestBody RegressionGateService.Request request) {
        return ApiResponse.ok("评测回归门禁完成", service.evaluate(request));
    }
}
