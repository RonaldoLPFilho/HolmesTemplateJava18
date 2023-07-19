package com.despegar.javatemplate.config.server;

import com.despegar.javatemplate.config.server.model.JettyProperties;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import static java.util.Optional.ofNullable;

@Component
public class JettyServerFactoryCustomizer implements WebServerFactoryCustomizer<JettyServletWebServerFactory> {

    private final JettyProperties serverProperties;
    private final ThreadPool jettyThreadPool;

    @Autowired
    public JettyServerFactoryCustomizer(JettyProperties serverProperties,
                                        @Qualifier("jettyThreadPool") ThreadPool jettyThreadPool) {
        this.serverProperties = serverProperties;
        this.jettyThreadPool = jettyThreadPool;
    }

    @Override
    public void customize(JettyServletWebServerFactory factory) {
        var port = ofNullable(serverProperties.port()).orElse(9290);
        factory.setPort(port);
        factory.setThreadPool(jettyThreadPool);
        factory.addServerCustomizers(server -> {
            Handler handler = server.getHandler();
            server.setHandler(disableTraceHttpMethodFromHandler(handler));
        });
    }

    @Autowired
    private void registerServletRegistartionBeans(GenericWebApplicationContext applicationContext) {
        ofNullable(serverProperties.contextPath()).ifPresent(contextPath -> this.registerServletRegistrationBean(applicationContext, contextPath + "/*", "cloudia"));
        this.registerServletRegistrationBean(applicationContext, "/*", "nebula");
    }

    private void registerServletRegistrationBean(GenericWebApplicationContext applicationContext, String path, String name) {
        var dispatcherServlet = new DispatcherServlet();
        var ctx = new AnnotationConfigWebApplicationContext();

        ctx.setParent(applicationContext);
        ctx.register(PropertyPlaceholderAutoConfiguration.class,
                DispatcherServletAutoConfiguration.class);
        dispatcherServlet.setApplicationContext(ctx);

        var servletRegistrationBean = new ServletRegistrationBean<>(dispatcherServlet, path);
        servletRegistrationBean.setName(name);

        applicationContext.registerBean(name, ServletRegistrationBean.class, () -> servletRegistrationBean);
    }

    private Handler disableTraceHttpMethodFromHandler(Handler h){
        Constraint disableTraceConstraint = new Constraint();
        disableTraceConstraint.setName("Disable TRACE");
        disableTraceConstraint.setAuthenticate(true);

        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setConstraint(disableTraceConstraint);
        mapping.setMethod("TRACE");
        mapping.setPathSpec("/");

        Constraint omissionConstraint = new Constraint();
        ConstraintMapping omissionMapping = new ConstraintMapping();
        omissionMapping.setConstraint(omissionConstraint);
        omissionMapping.setMethod("*");
        omissionMapping.setPathSpec("/");

        ConstraintSecurityHandler handler = new ConstraintSecurityHandler();
        handler.addConstraintMapping(mapping);
        handler.addConstraintMapping(omissionMapping);
        handler.setHandler(h);
        return handler;
    }
}
