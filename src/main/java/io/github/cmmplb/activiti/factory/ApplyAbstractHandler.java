package io.github.cmmplb.activiti.factory;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author penglibo
 * @date 2024-11-08 14:58:49
 * @since jdk 1.8
 */

public abstract class ApplyAbstractHandler<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements InitializingBean {

    public Long invoke(String jsonParams) throws Exception {
        // 子类未实现该方法就不让其调用抛出异常
        throw new UnsupportedOperationException();
    }
}
