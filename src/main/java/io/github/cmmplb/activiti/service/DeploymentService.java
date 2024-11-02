package io.github.cmmplb.activiti.service;

import io.github.cmmplb.activiti.beans.PageResult;
import io.github.cmmplb.activiti.beans.QueryPageBean;
import io.github.cmmplb.activiti.domain.vo.DeploymentVO;
import org.activiti.core.common.project.model.ProjectManifest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author penglibo
 * @date 2024-10-31 16:39:04
 * @since jdk 1.8
 */
public interface DeploymentService {

    PageResult<DeploymentVO> getByPaged(QueryPageBean queryPageBean);

    boolean upload(MultipartFile[] files);

    boolean removeById(String id);

    ProjectManifest buildProjectManifest(String name, String description, String version);
}