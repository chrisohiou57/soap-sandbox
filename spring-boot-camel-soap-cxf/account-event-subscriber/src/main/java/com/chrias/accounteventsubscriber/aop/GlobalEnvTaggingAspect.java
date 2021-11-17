package com.chrias.accounteventsubscriber.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.opentelemetry.api.trace.Span;

@Aspect
@Component
public class GlobalEnvTaggingAspect {
    
    Logger log = LoggerFactory.getLogger(GlobalEnvTaggingAspect.class);

    @Value("${k8s.namespace:local}")
    private String namespace;

    @Around("@annotation(GlobalEnvSpanTagger)")
    public Object addGlobalEnvironmentSpanTags(ProceedingJoinPoint jointPoint) throws Throwable {
        boolean isNoopSpan = Span.current() == Span.getInvalid();
        boolean spanUpdated = false;

        spanUpdated = handleSpanUpdate(isNoopSpan, spanUpdated);
        Object proceed = jointPoint.proceed();
        spanUpdated = handleSpanUpdate(isNoopSpan, spanUpdated);

        return proceed;
    }

    private boolean handleSpanUpdate(boolean isNoopSpan, boolean spanUpdated) {
        if (!isNoopSpan && !spanUpdated) {
            log.info(String.format("Adding global attributes to Span: namespace=%s", namespace));
            Span span = Span.current();
            span.setAttribute("namespace", namespace);
            return true;
        } else {
            log.info(String.format("Not updating span with flags isNoopSpan=%b spanUpdated=%b", isNoopSpan, spanUpdated));
            return false;
        }
    }

}
