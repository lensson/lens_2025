package com.lens.common.db.mybatis.reflect;

/**
 * 来源于 MyBatis Plus 更高版本
 *
 * @author geshanzsq
 * @date 2024/6/4
 */
public interface IGenericTypeResolver {

    Class<?>[] resolveTypeArguments(final Class<?> clazz, final Class<?> genericIfc);

}
