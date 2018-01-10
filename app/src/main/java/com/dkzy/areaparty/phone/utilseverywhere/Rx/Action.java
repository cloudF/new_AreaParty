package com.dkzy.areaparty.phone.utilseverywhere.Rx;

public interface Action<R, P> {

    R call(P p);
}