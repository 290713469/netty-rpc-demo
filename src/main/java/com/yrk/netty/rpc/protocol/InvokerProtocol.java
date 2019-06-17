package com.yrk.netty.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class InvokerProtocol implements Serializable {

    private String className; //服务名称
    private String methodName; //方法名称
    private Class<?>[] paramTypes; // 参数类型
    private Object[] paramValues; // 参数值

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParamValues() {
        return paramValues;
    }

    public void setParamValues(Object[] paramValues) {
        this.paramValues = paramValues;
    }
}
