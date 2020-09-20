package com.example.demo;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests2 {
    @Resource
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    HistoryService historyService;


    /**
     * 全部流程实例挂起
     */
    @Test
    public void suspendOrActivateProcessDefinition() {
        // 流程定义id
        String processDefinitionId = "fund_flow:1:6";
        // 获得流程定义
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        //是否暂停
        boolean suspend = processDefinition.isSuspended();
        if (suspend) {
            //如果暂停则激活，这里将流程定义下的所有流程实例全部激活
            repositoryService.activateProcessDefinitionById(processDefinitionId,
                    true, null);
            System.out.println("流程定义：" + processDefinitionId + "激活");
        } else {
            //如果激活则挂起，这里将流程定义下的所有流程实例全部挂起
            repositoryService.suspendProcessDefinitionById(processDefinitionId,
                    true, null);
            System.out.println("流程定义：" + processDefinitionId + "挂起");
        }
    }

    /**
     * 单个流程实例挂起
     */
    @Test
    public void suspendOrActiveProcessInstance() {
        // 流程实例id
        String processInstanceId = "20001";
        //根据流程实例id查询流程实例
        ProcessInstance processInstance =
                runtimeService.createProcessInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .singleResult();
        boolean suspend = processInstance.isSuspended();
        if (suspend) {
            //如果暂停则激活
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println("流程实例：" + processInstanceId + "激活");
        } else {
            //如果激活则挂起
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println("流程实例：" + processInstanceId + "挂起");
        }
    }


}
