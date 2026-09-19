/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aieval;
import org.junit.jupiter.api.Test;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.test.context.SpringBootTest;import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;import org.springframework.http.MediaType;import org.springframework.test.web.servlet.MockMvc;import java.nio.charset.StandardCharsets;import java.util.Base64;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@SpringBootTest @AutoConfigureMockMvc class AiEvalApiIntegrationTests{
 @Autowired MockMvc mvc;/**
                         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                         */
private String auth(){return "Basic "+Base64.getEncoder().encodeToString("admin:test-admin".getBytes(StandardCharsets.UTF_8));}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Test void evaluatorCanRunDatasetAndPersistAudit()throws Exception{String body="""
 {"requestId":"API-EVAL-1","casePassScore":0.9,"minimumAverageScore":0.9,"minimumPassRate":1.0,"approved":true,"evaluatorId":"evaluator","approverId":"approver","cases":[{"caseId":"C1","input":"退款政策","candidate":"七日内未拆封可以退款，依据政策A。","reference":"七日内未拆封可以退款，依据政策A。","expectedCitations":["政策A"],"actualCitations":["政策A"],"prohibitedTerms":["泄露密钥"],"actualLatencyMs":200,"maxLatencyMs":1000,"actualCost":0.01,"maxCost":0.03}]}
 """;mvc.perform(post("/api/ai-eval/runs").header("Authorization",auth()).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.data.decision").value("PASS")).andExpect(jsonPath("$.data.passRate").value(1.0)).andExpect(jsonPath("$.data.auditId").isNumber());}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Test void anonymousAuditAccessIsDenied()throws Exception{mvc.perform(get("/api/ai-eval/audits")).andExpect(status().isUnauthorized());}
}
