package com.example.demo;

import cn.hutool.core.io.IoUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
    @Resource
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    HistoryService historyService;


    /**
     * 部署任务
     */
    @Test
    public void TestDeployProcess() {
        repositoryService.createDeployment()
                .addClasspathResource("processes/fund.bpmn")
                .addClasspathResource("processes/fund.png")
                .name("资金申领流程")
                .deploy();
    }

    /**
     * 部署任务ZIP (线上部署的时候用到)
     */
    @Test
    public void TestDeployZIPProcess() {
        InputStream inputStream = DemoApplicationTests.class.getClassLoader().getResourceAsStream("processes/fund.zip");

        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("资金申领流程")
                .deploy();
    }

    /**
     * 开启任务
     */
    @Test
    public void TestStartProcess() {
        //业务关联标识DemoApplicationTests
        String businessKey = "1001";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("fund_flow",businessKey);

        System.out.println("流程部署ID: " + processInstance.getDeploymentId());
        System.out.println("流程定义ID: " + processInstance.getProcessDefinitionId());
        System.out.println("流程实例ID: " + processInstance.getId());
        System.out.println("活动ID: " + processInstance.getActivityId());
    }

    /**
     * 查询任务
     */
    @Test
    public void TestTaskProcess() {
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("fund_flow")
                .taskAssignee("张三")
                .list();

        for (Task task : taskList) {
            System.out.println(" 流 程 实 例 id ： " +
                    task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }

    }

    /**
     * 完成任务
     */
    @Test
    public void TestComplateTask() {
        //任务id
        String taskId = "5002";
        taskService.complete(taskId);
        System.out.println("完成任务id=" + taskId);
    }

    /**
     * 流程定义查询
     */
    @Test
    public void TestQueryProceccDefinition() {
        // 流程定义key
        String processDefinitionKey = "fund_flow";
        // 查询流程定义
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        //遍历查询结果
        List<ProcessDefinition> list = processDefinitionQuery
                .processDefinitionKey(processDefinitionKey)
                .orderByProcessDefinitionVersion().desc().list();
        for (ProcessDefinition processDefinition : list) {
            System.out.println("------------------------");
            System.out.println(" 流 程 部 署 id ： " +
                    processDefinition.getDeploymentId());
            System.out.println("流程定义id：" + processDefinition.getId());
            System.out.println("流程定义名称：" + processDefinition.getName());
            System.out.println("流程定义key：" + processDefinition.getKey());
            System.out.println("流程定义版本：" + processDefinition.getVersion());
            System.out.println("流程部署ID：" + processDefinition.getDeploymentId());
        }
    }

    @Test
    public void deleteDeployment() {
        // 流程部署id
        String deploymentId = "1";
        //删除流程定义，如果该流程定义已有流程实例启动则删除时出错
//        repositoryService.deleteDeployment(deploymentId);
        //设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设
        //  置为false非级别删除方式，如果流程
        repositoryService.deleteDeployment(deploymentId, true);
    }

    @Test
    public void getProcessResources() throws IOException {
        // 流程定义id
        String processDefinitionId = "fund_flow:1:2504";

        // 流程定义对象
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();

        //获取bpmn
        String resource_bpmn = processDefinition.getResourceName();
        //获取png
        String resource_png = processDefinition.getDiagramResourceName();

        // 资源信息
        System.out.println("bpmn：" + resource_bpmn);
        System.out.println("png：" + resource_png);


        // 输出bpmn
        InputStream resourceAsStream = null;
        resourceAsStream = repositoryService.getResourceAsStream(
                processDefinition.getDeploymentId(), resource_bpmn);
        FileOutputStream fileOutputStream = new FileOutputStream(new File("d:/purchasingflow01.bpmn"));

        IoUtil.copy(resourceAsStream, fileOutputStream);

//        byte[] b = new byte[1024];
//        int len = -1;
//        while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
//            fileOutputStream.write(b, 0, len);
//        }


        // 输出图片
        resourceAsStream = repositoryService.getResourceAsStream(
                processDefinition.getDeploymentId(), resource_png);
        fileOutputStream = new FileOutputStream(new File("d:/purchasingflow01.png"));
        IoUtil.copy(resourceAsStream, fileOutputStream);

        // byte[] b = new byte[1024];
        // int len = -1;
//        while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
//            fileOutputStream.write(b, 0, len);
//        }

    }

    @Test
    public void testHistoric01() {
        HistoricActivityInstanceQuery query =
                historyService.createHistoricActivityInstanceQuery();
        query.processInstanceId("5001");

        List<HistoricActivityInstance> list = query.orderByHistoricActivityInstanceEndTime().asc().list();
        for (HistoricActivityInstance ai : list) {
            System.out.println(ai.getActivityId());
            System.out.println(ai.getActivityName());
            System.out.println(ai.getProcessDefinitionId());
            System.out.println(ai.getProcessInstanceId());
            System.out.println("==============================");
        }
    }
}
