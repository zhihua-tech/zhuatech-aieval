/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aieval.service;
import cn.zhuatech.aieval.model.EvaluationAudit;import cn.zhuatech.aieval.repository.EvaluationAuditRepository;import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service public class AiEvaluationService{
 private final EvaluationAuditRepository repository;/**
                                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                     */
public AiEvaluationService(EvaluationAuditRepository r){repository=r;}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Transactional public RunResult run(RunRequest r,String actor){var old=repository.findByRequestId(r.requestId());if(old.isPresent()){var e=old.get();return new RunResult(e.getId(),Decision.valueOf(e.getDecision()),e.getScore(),e.getPassRate(),List.of(),"重复请求已返回原结果",true);}
  List<CaseResult>results=new ArrayList<>();for(EvalCase c:r.cases())results.add(evaluate(c,r.casePassScore()));
  double score=results.stream().mapToDouble(CaseResult::score).average().orElse(0);long passed=results.stream().filter(CaseResult::passed).count();double passRate=(double)passed/results.size();
  List<String> blockers=new ArrayList<>();if(score<r.minimumAverageScore())blockers.add("平均质量分低于发布阈值");if(passRate<r.minimumPassRate())blockers.add("用例通过率低于发布阈值");if(results.stream().anyMatch(x->!x.safetyPassed()))blockers.add("存在安全门禁失败用例");if(r.evaluatorId().equals(r.approverId()))blockers.add("评测人与发布审批人必须职责分离");if(!r.approved())blockers.add("评测结果尚未完成发布审批");
  Decision decision=blockers.isEmpty()?Decision.PASS:Decision.BLOCKED;var saved=repository.save(new EvaluationAudit(r.requestId(),decision.name(),score,passRate,"cases="+results.size()+", blockers="+blockers.size(),actor));return new RunResult(saved.getId(),decision,round(score),round(passRate),List.copyOf(results),String.join("；",blockers),false);
 }
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private CaseResult evaluate(EvalCase c,double threshold){double similarity=f1(ngrams(c.candidate()),ngrams(c.reference()));Set<String>expected=new HashSet<>(c.expectedCitations());Set<String>actual=new HashSet<>(c.actualCitations());long hit=expected.stream().filter(actual::contains).count();double citation=expected.isEmpty()?1:(double)hit/expected.size();String lower=c.candidate().toLowerCase();List<String>failures=new ArrayList<>();boolean safety=c.prohibitedTerms().stream().map(String::toLowerCase).noneMatch(lower::contains);if(!safety)failures.add("命中禁止内容");if(c.actualLatencyMs()>c.maxLatencyMs())failures.add("响应延迟超限");if(c.actualCost()>c.maxCost())failures.add("单次成本超限");if(citation<1)failures.add("引用证据不完整");double score=similarity*.65+citation*.25+(safety?0.10:0);boolean passed=score>=threshold&&failures.isEmpty();return new CaseResult(c.caseId(),round(similarity),round(citation),safety,round(score),passed,List.copyOf(failures));}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private Set<String>ngrams(String text){String n=text.toLowerCase().replaceAll("\\s+","");Set<String>s=new HashSet<>();if(n.length()<2){s.add(n);return s;}for(int i=0;i<n.length()-1;i++)s.add(n.substring(i,i+2));return s;}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private double f1(Set<String>a,Set<String>b){if(a.isEmpty()&&b.isEmpty())return 1;if(a.isEmpty()||b.isEmpty())return 0;long hit=a.stream().filter(b::contains).count();double p=(double)hit/a.size(),r=(double)hit/b.size();return p+r==0?0:2*p*r/(p+r);}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private double round(double n){return Math.round(n*10000d)/10000d;}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Transactional(readOnly=true)public List<EvaluationAudit>audits(){return repository.findTop100ByOrderByCreatedAtDesc();}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record EvalCase(@NotBlank String caseId,@NotBlank String input,@NotBlank String candidate,@NotBlank String reference,@NotNull List<String>expectedCitations,@NotNull List<String>actualCitations,@NotNull List<String>prohibitedTerms,@Min(1)long actualLatencyMs,@Min(1)long maxLatencyMs,@DecimalMin("0")double actualCost,@DecimalMin("0")double maxCost){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record RunRequest(@NotBlank String requestId,@NotEmpty List<@Valid EvalCase>cases,@DecimalMin("0")@DecimalMax("1")double casePassScore,@DecimalMin("0")@DecimalMax("1")double minimumAverageScore,@DecimalMin("0")@DecimalMax("1")double minimumPassRate,boolean approved,@NotBlank String evaluatorId,@NotBlank String approverId){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record CaseResult(String caseId,double similarityScore,double citationCoverage,boolean safetyPassed,double score,boolean passed,List<String>failures){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record RunResult(Long auditId,Decision decision,double averageScore,double passRate,List<CaseResult>cases,String blockers,boolean duplicate){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public enum Decision{PASS,BLOCKED}
}
