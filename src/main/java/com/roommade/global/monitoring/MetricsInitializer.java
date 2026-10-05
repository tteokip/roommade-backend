package com.roommade.global.monitoring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;

@Component
public class MetricsInitializer implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        JvmMetrics.builder().register();
    }
}
