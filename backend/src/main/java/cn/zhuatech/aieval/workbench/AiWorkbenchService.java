/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aieval.workbench;
import org.springframework.stereotype.Service;
import java.math.*;
import java.util.*;
import static cn.zhuatech.aieval.workbench.WorkbenchInput.*;
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
    /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    public Map<String,Object> execute(Map<String,Object> r){
        validate(r);

        keys(r,"k","queries");int k=integer(r,"k",1,100);var queries=rows(r,"queries",1,100);List<Map<String,Object>> details=new ArrayList<>();Set<String> ids=new HashSet<>();double rr=0,recall=0,precision=0,ndcg=0;int evaluable=0;
        for(var q:queries){keys(q,"id","relevant","retrieved");String id=id(q,"id");if(!ids.add(id))throw new IllegalArgumentException("查询标识重复");var rel=object(q.get("relevant"));if(rel.size()>200)throw new IllegalArgumentException("标注最多200项");
            for(String key:rel.keySet())integer(rel,key,0,5);List<String> retrieved=strings(q,"retrieved",0,200);List<String> ranked=new ArrayList<>(new LinkedHashSet<>(retrieved));if(ranked.size()>k)ranked=ranked.subList(0,k);
            long total=rel.keySet().stream().filter(x->integer(rel,x,0,5)>0).count();if(total==0){details.add(Map.of("id",id,"evaluable",false,"reason","缺少正相关标注"));continue;}
            int hit=0;double first=0,dcg=0;for(int i=0;i<ranked.size();i++){int grade=rel.containsKey(ranked.get(i))?integer(rel,ranked.get(i),0,5):0;if(grade>0){hit++;if(first==0)first=1.0/(i+1);}dcg+=(Math.pow(2,grade)-1)/(Math.log(i+2)/Math.log(2));}
            List<Integer> grades=rel.keySet().stream().map(x->integer(rel,x,0,5)).sorted(Comparator.reverseOrder()).limit(k).toList();double ideal=0;for(int i=0;i<grades.size();i++)ideal+=(Math.pow(2,grades.get(i))-1)/(Math.log(i+2)/Math.log(2));
            double rc=(double)hit/total,pr=(double)hit/k,n=ideal==0?0:dcg/ideal;details.add(Map.of("id",id,"evaluable",true,"reciprocalRank",round(first),"recall",round(rc),"precision",round(pr),"ndcg",round(n)));rr+=first;recall+=rc;precision+=pr;ndcg+=n;evaluable++;
        }
        return Map.of("queries",details,"evaluableQueries",evaluable,"meanReciprocalRank",round(evaluable==0?0:rr/evaluable),"meanRecall",round(evaluable==0?0:recall/evaluable),"meanPrecision",round(evaluable==0?0:precision/evaluable),"meanNdcg",round(evaluable==0?0:ndcg/evaluable));

    }
}
