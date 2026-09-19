/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aieval.service;

import cn.zhuatech.aieval.model.EvaluationAudit;
import cn.zhuatech.aieval.repository.EvaluationAuditRepository;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * 比较已持久化的两次评测，阻止质量或通过率回退的版本晋级。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class RegressionGateService {
    private final EvaluationAuditRepository repository;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public RegressionGateService(EvaluationAuditRepository repository) {
        this.repository = repository;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Result evaluate(Request request) {
        if (request.baselineRequestId().equals(request.candidateRequestId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "基线与候选评测不能相同");
        }
        EvaluationAudit baseline = find(request.baselineRequestId());
        EvaluationAudit candidate = find(request.candidateRequestId());
        double averageDrop = baseline.getScore() - candidate.getScore();
        double passRateDrop = baseline.getPassRate() - candidate.getPassRate();
        List<String> blockers = new ArrayList<>();
        if (!"PASS".equals(baseline.getDecision())) blockers.add("基线评测未通过发布门禁");
        if (!"PASS".equals(candidate.getDecision())) blockers.add("候选评测未通过发布门禁");
        if (averageDrop > request.maxAverageScoreDrop()) blockers.add("平均质量分回退超过阈值");
        if (passRateDrop > request.maxPassRateDrop()) blockers.add("用例通过率回退超过阈值");
        Decision decision = blockers.isEmpty() ? Decision.PROMOTE : Decision.BLOCK;
        return new Result(decision, baseline.getRequestId(), candidate.getRequestId(),
                round(baseline.getScore()), round(candidate.getScore()), round(averageDrop),
                round(baseline.getPassRate()), round(candidate.getPassRate()), round(passRateDrop),
                List.copyOf(blockers));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private EvaluationAudit find(String requestId) {
        return repository.findByRequestId(requestId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "评测记录不存在: " + requestId));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private double round(double value) {
        return Math.round(value * 10000d) / 10000d;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Request(@NotBlank String baselineRequestId, @NotBlank String candidateRequestId,
                          @DecimalMin("0.0") @DecimalMax("1.0") double maxAverageScoreDrop,
                          @DecimalMin("0.0") @DecimalMax("1.0") double maxPassRateDrop) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Result(Decision decision, String baselineRequestId, String candidateRequestId,
                         double baselineAverageScore, double candidateAverageScore, double averageScoreDrop,
                         double baselinePassRate, double candidatePassRate, double passRateDrop,
                         List<String> blockers) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public enum Decision { PROMOTE, BLOCK }
}
