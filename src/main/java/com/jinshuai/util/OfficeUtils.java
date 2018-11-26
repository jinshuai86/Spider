package com.jinshuai.util;

import java.util.List;

public interface OfficeUtils<T> {

    List<T> read();

    void write();

}