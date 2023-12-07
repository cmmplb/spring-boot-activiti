/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cmmplb.activiti.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ActivitiException;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author penglibo
 * @date 2023-11-03 11:44:08
 * @since jdk 1.8
 */

@Slf4j
@RestController
public class StencilSetController {

    @ApiOperation("获取配置")
    @GetMapping(value = "/editor/stencilset")
    public String getStencilSet() {
        try {
            return IOUtils.toString(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("stencilset.json")), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new ActivitiException("Error while loading stencil set", e);
        }
    }


}
