package io.github.cmmplb.activiti.configuration.properties;

import lombok.Data;
import org.activiti.engine.impl.persistence.entity.DeploymentEntity;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author penglibo
 * @date 2024-11-01 15:33:25
 * @since jdk 1.8
 */

@Data
@ConfigurationProperties(prefix = ActivitiProperties.PREFIX)
public class ActivitiProperties {

    /**
     * 整合模型相关信息配置
     */
    private Model model = new Model();

    /**
     * 整合部署相关信息配置
     */
    private Deployment deployment = new Deployment();

    public static final String PREFIX = "activiti";

    @Data
    public static class Model {

        /**
         * 导出模型时是否导出流程图片, 为 true 时流程文件和流程图片压缩成 zip 导出
         */
        private boolean isExportEditorSourceExtra = false;
    }

    @Data
    public static class Deployment {

        /**
         * 是否过滤重复, 默认为 false, 防止资源没有发生变化而再次执行部署方法产生的重复部署
         * - false: 每次部署 ACT_RE_DEPLOYMENT 都会新增一条部署信息, 版本号是 1, ACT_RE_PROCDEF 表会根据部署时的模型文件数量新增对应 n 条数据, 同时版本 +1
         * - true: 部署时会判断部署名称和流程定义文件与数据库中是否相同:
         * -- 名称相同, 流程定义文件内容相同, 数据过滤不做处理
         * -- 名称相同, 流程定义文件内容不同, ACT_RE_DEPLOYMENT 表新增一条记录, 版本号 +1 , ACT_RE_PROCDEF 表不会新增数据
         * -- 名称不同, 流程定义文件内容相同, ACT_RE_DEPLOYMENT 表新增一条记录, 版本号是 1, ACT_RE_PROCDEF 会根据部署时的模型文件数量新增对应 n 条数据, 同时版本 +1
         * -- 名称不同, 流程定义文件内容不同, ACT_RE_DEPLOYMENT 表新增一条记录, 版本号是 1, ACT_RE_PROCDEF 会根据部署时的模型文件数量新增对应 n 条数据, 同时版本也是 1
         */
        private boolean isAutoDeploymentEnabled = false;

        /**
         * 是否开启项目清单配置, 用来测试 ACT_RE_PROCDEF 表 APP_VERSION_ 字段和 ACT_RE_DEPLOYMENT 表 PROJECT_RELEASE_VERSION_ 字段
         * 部署相同流程时需要搭配 auto-deployment-enabled = true 一起使用, 否则模型部署和上传部署会冲突报错 ACT_RE_PROCDEF: UNIQUE KEY `ACT_UNIQ_PROCDEF`
         * **注意** 如果设置了项目资源清单版本, 则 isAutoDeploymentEnabled 的判断规则会失效, 判断逻辑改为对比数据库中项目资源清单版本号:
         * - 版本号相同则数据过滤不做处理
         * - 版本号不同则 ACT_RE_DEPLOYMENT 表新增一条记录, 版本号 +1, ACT_RE_PROCDEF 表会根据部署时的模型文件数量新增对应 n 条数据, 同时版本 +1
         * {@link org.activiti.engine.impl.cmd.DeployCmd#deploymentsDiffer(DeploymentEntity, DeploymentEntity)}
         * !deployment.getProjectReleaseVersion().equals(saved.getProjectReleaseVersion());
         */
        private boolean isProjectManifestEnabled = false;
    }
}