<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="org.springframework.data.redis.connection.RedisConnection"
%><%@ page import="com.cyberlink.core.BeanLocator"
%><%@ page import="org.springframework.data.redis.connection.jedis.JedisConnectionFactory"
%><%@ page import="org.springframework.data.redis.connection.jedis.JedisConnection"
%><%
try {
    final JedisConnectionFactory f = BeanLocator.getBean("core.jedisConnectionFactory");
    JedisConnection conn = f.getConnection();
    String result = conn.ping();

    if ("PONG".equals(result)) {
        out.print("success");
    } else {
        out.print("fail");
    }
    conn.close();
} catch (Throwable e) {
    e.printStackTrace();
    out.print("fail");
}
%>
