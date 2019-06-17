package com.yrk.netty.rpc.provider;

import com.yrk.netty.rpc.api.RpcHelloService;

public class RpcHelloServiceBean implements RpcHelloService {
    @Override
    public String hello(String name) {
        return "Hello " + name + " !";
    }
}
