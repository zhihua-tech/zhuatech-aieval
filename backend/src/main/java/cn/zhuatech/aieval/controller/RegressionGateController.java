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

@RestController
@RequestMapping("/api/ai-eval")
public class RegressionGateController {
    private final RegressionGateService service;

    public RegressionGateController(RegressionGateService service) {
        this.service = service;
    }

    @PostMapping("/regression-checks")
    @PreAuthorize("hasAnyRole('ADMIN','AUDITOR')")
    public ApiResponse<RegressionGateService.Result> evaluate(
            @Valid @RequestBody RegressionGateService.Request request) {
        return ApiResponse.ok("评测回归门禁完成", service.evaluate(request));
    }
}
