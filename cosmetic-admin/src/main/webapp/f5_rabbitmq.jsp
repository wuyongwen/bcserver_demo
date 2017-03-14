<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="com.cyberlink.cosmetic.Constants"%>
<%@ page import="com.cyberlink.cosmetic.amqp.MessageProducer"%>
<%@ page import="com.cyberlink.cosmetic.event.post.OfficialPostCreateEvent"%>

<%!
	public void publishTest() {
	    MessageProducer messageProducer = BeanLocator.getBean("core.amqp.messageProducer");
	    messageProducer.convertAndSend(new OfficialPostCreateEvent());
	}

%>
<%
try {
	if(!Constants.getIsRabbitMqEnable()) {
		out.print("success");
		return;
	}

    publishTest();
	out.print("success");
} catch (Throwable e) {
    e.printStackTrace();
    out.print("fail");
}
%>
