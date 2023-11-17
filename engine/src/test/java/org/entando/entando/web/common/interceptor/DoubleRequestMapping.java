package org.entando.entando.web.common.interceptor;

import java.lang.annotation.Annotation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

class DoubleRequestMapping implements RequestMapping {

    @Override
    public String name() {
        return null;
    }

    @Override
    public String[] value() {
        return new String[]{"test"};
    }

    @Override
    public String[] path() {
        return new String[0];
    }

    @Override
    public RequestMethod[] method() {
        return new RequestMethod[0];
    }

    @Override
    public String[] params() {
        return new String[0];
    }

    @Override
    public String[] headers() {
        return new String[0];
    }

    @Override
    public String[] consumes() {
        return new String[0];
    }

    @Override
    public String[] produces() {
        return new String[0];
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}