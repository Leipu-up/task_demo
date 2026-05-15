基础任务审批系统
使用 SpringBoot 实现一个简化的任务审批系统Demo。
核心需求:1.任务创建与串行审批创建任务时指定一个审批人任务状态:DRAFT(草稿)一PENDING(待审批)一APPROVED(已批准)/REJECTED(已驳回)
审批人可以批准或驳回，驳回时任务返回 DRAFT状态
2.简单的任务分发(单层)
当任务被批准后，创建者可以将其拆分为2-3个子任务每个子任务指定不同的处理人
子任务并行处理，所有子任务完成后，父任务自动标记为COMPLETED
3.基础要求
使用 Spring Data JPA+ MySQL数据库
提供 REST API:
POST/tasks-创建任务
PUT/tasks/{id/approve - 批准任务
PUT/tasks/id/reject- 驳回任务
POST/tasks/id}/subtasks-拆分子任务
PUT/subtasks/{id}/complete- 完成子任务
需要处理简单的状态校验(如:不能批准已批准的任务)
可选扩展(加分项):
添加用户身份验证(Spring Security)
使用WebSocket 实时推送状态变更
编写单元测试覆盖核心流程

交付物:
可运行的代码
数据库表结构说明
Postman/curl测试示例
