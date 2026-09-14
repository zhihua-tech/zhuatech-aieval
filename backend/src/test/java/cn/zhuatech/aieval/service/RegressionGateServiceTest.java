/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aieval.service;

import cn.zhuatech.aieval.model.EvaluationAudit;
import cn.zhuatech.aieval.repository.EvaluationAuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class RegressionGateServiceTest {
    private EvaluationAuditRepository repository;
    private RegressionGateService service;

    @BeforeEach
    void setUp() {
        repository = mock(EvaluationAuditRepository.class);
        service = new RegressionGateService(repository);
        when(repository.findByRequestId("baseline")).thenReturn(Optional.of(
                new EvaluationAudit("baseline", "PASS", .95, 1.0, "baseline", "admin")));
    }

    @Test
    void promotesCandidateInsideRegressionLimits() {
        when(repository.findByRequestId("candidate")).thenReturn(Optional.of(
                new EvaluationAudit("candidate", "PASS", .93, .98, "candidate", "admin")));
        var result = service.evaluate(new RegressionGateService.Request("baseline", "candidate", .03, .03));
        assertThat(result.decision()).isEqualTo(RegressionGateService.Decision.PROMOTE);
        assertThat(result.averageScoreDrop()).isEqualTo(.02);
        assertThat(result.passRateDrop()).isEqualTo(.02);
    }

    @Test
    void blocksRegressionAndFailedCandidate() {
        when(repository.findByRequestId("candidate")).thenReturn(Optional.of(
                new EvaluationAudit("candidate", "BLOCKED", .80, .75, "candidate", "admin")));
        var result = service.evaluate(new RegressionGateService.Request("baseline", "candidate", .03, .03));
        assertThat(result.decision()).isEqualTo(RegressionGateService.Decision.BLOCK);
        assertThat(result.blockers()).contains("候选评测未通过发布门禁", "平均质量分回退超过阈值", "用例通过率回退超过阈值");
    }

    @Test
    void rejectsSameRunAndMissingRun() {
        assertThatThrownBy(() -> service.evaluate(new RegressionGateService.Request("baseline", "baseline", 0, 0)))
                .isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> service.evaluate(new RegressionGateService.Request("baseline", "missing", 0, 0)))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void blocksSmallRegressionThatWouldRoundToZero() {
        when(repository.findByRequestId("candidate")).thenReturn(Optional.of(
                new EvaluationAudit("candidate", "PASS", .94999, 1.0, "candidate", "admin")));
        var result = service.evaluate(new RegressionGateService.Request("baseline", "candidate", 0, 0));
        assertThat(result.decision()).isEqualTo(RegressionGateService.Decision.BLOCK);
    }
}
