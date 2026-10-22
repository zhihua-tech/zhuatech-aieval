# 检索质量 MRR 与 NDCG 评测

上海如静知华信息科技有限公司 · https://www.zhuatech.cn/

日期：2026-10-23。商业授权或定制开发请微信添加微信号 zhuatech 或 zhuatech2 进行咨询。

对带相关性标注的查询批次计算 MRR、Recall@K、Precision@K、NDCG@K，重复候选不重复计分，并给出无有效标注的查询提醒。

## 操作入口

页面右下方“业务计算工具”打开独立工作区，支持恢复示例、修改JSON、执行计算、查看结构化结果及导出完整JSON。显示加载、空结果、错误及登录失效状态；大表格显示前100行并明确提示，导出保留全部结果。沿用当前项目JWT登录；Basic项目可填写现有账号，密码只留在内存中，请求结束立即清空，不写入本地存储。未登录无法调用后端。

## API

`POST /api/workbench/retrieval-metrics`，Content-Type: application/json；认证沿用原有JWT或Basic机制。不建立新默认账号。

```json
{
  "k": 3,
  "queries": [
    {
      "id": "q1",
      "relevant": {
        "a": 3,
        "b": 1
      },
      "retrieved": [
        "a",
        "a",
        "c",
        "b"
      ]
    }
  ]
}
```

成功为HTTP 200与 `{success:true,data:{...}}`；业务格式错误400；无登录401（原安全过滤器可能提前返回401/403）。返回数据为实际计算，不使用静态成功兜底。

输入整体限制：最多12层嵌套、10000个节点、单段文本64000个UTF-16代码单元、累计文本128000个代码单元；单数组最多500项，各业务集合另有更严格限制。数值必须为有限JSON数字，禁止把文本当数值。未知顶层字段拒绝。当前不保存计算记录，不查询任何租户或供应商数据，不发送原始文本到外部服务。

## 实现与测试

实现位于 `backend/src/main/java/cn/zhuatech/aieval/workbench/`。新增单元/HTTP映射测试 `AiWorkbenchServiceTest`，覆盖业务结果、业务边界、确定性、输入不被修改、空值、缺失字段、未知字段、过长及过深输入、匿名拦截、登录计算和400反馈。运行 `cd backend && mvn test`，前端运行 `cd frontend && npm run build`。实际本轮测试及远端核验见批次交付记录，不能将单元测试当作生产验收。

## English

**Retrieval MRR and NDCG metrics** provides a deterministic authenticated computation over caller-supplied data. Open **业务计算工具** at the bottom right, edit the synthetic JSON example, run it and inspect/export the calculated result. No model invocation, database mutation, scheduled execution or external network access is performed by this new endpoint. Existing JWT/Basic credentials are required; no new default credentials are introduced. Scope is limited to this input transformation, not a complete replacement for domain authorization, model evaluation or production acceptance.

The endpoint, exact request schema and source files above are shared by both language editions. The result is computed, not a mock. English support: [han@zhuatech.cn](mailto:han@zhuatech.cn), [jack@zhuatech.cn](mailto:jack@zhuatech.cn), [WhatsApp +86 17521234993](https://wa.me/8617521234993).
