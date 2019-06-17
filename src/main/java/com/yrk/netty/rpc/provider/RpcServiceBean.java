package com.yrk.netty.rpc.provider;

import com.yrk.netty.rpc.api.RpcService;

public class RpcServiceBean implements RpcService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

    @Override
    public int multiple(int a, int b) {
        return a * b;
    }

    @Override
    public int divide(int a, int b) {
        return a / b;
    }
}
