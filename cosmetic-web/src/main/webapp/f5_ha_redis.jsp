<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="org.springframework.data.redis.connection.jedis.JedisConnectionFactory"%>
<%@ page import="org.springframework.data.redis.core.RedisTemplate"%>
<%@ page import="org.springframework.data.redis.core.StringRedisTemplate"%>
<%@ page import="com.cyberlink.core.BeanLocator"%>
<%@ page import="com.cyberlink.cosmetic.Constants"%>

<%!
	public void writeTest() {
    
	   	final JedisConnectionFactory pr = BeanLocator.getBean("core.jedisConnectionFactory.pool");
	   	RedisTemplate<String,String> prStringTemplate = new StringRedisTemplate(pr);
		prStringTemplate.opsForZSet().add("p:feawde3d:test", "OK", 0);
		prStringTemplate.opsForZSet().add("p:5tgefs2t5:test", "OK", 0);
		prStringTemplate.opsForZSet().add("p:58ilykjghfgdf:test", "OK", 0);
		prStringTemplate.opsForZSet().add("p:-124r:test", "OK", 0);
		prStringTemplate.opsForZSet().add("p:test1:test", "OK", 0);
		prStringTemplate.opsForZSet().add("p:3qefg3dwc:test", "OK", 0);
	
      	final JedisConnectionFactory fr = BeanLocator.getBean("core.jedisConnectionFactory.feed");
      	RedisTemplate<String,String> frStringTemplate = new StringRedisTemplate(fr);
        frStringTemplate.opsForZSet().add("f:qwdae_qwed:test", "OK", 0);
        frStringTemplate.opsForZSet().add("f:41324qwfq31q32:test", "OK", 0);
        frStringTemplate.opsForZSet().add("f:a2ewqas:test", "OK", 0);
        frStringTemplate.opsForZSet().add("f:awda:test", "OK", 0);
        frStringTemplate.opsForZSet().add("f:-AWdeqwD:test", "OK", 0);
        frStringTemplate.opsForZSet().add("f:t3qfgq3e:test", "OK", 0);

      	final JedisConnectionFactory mr = BeanLocator.getBean("core.jedisConnectionFactory.main");
      	RedisTemplate<String,String> mrStringTemplate = new StringRedisTemplate(mr);
        mrStringTemplate.opsForZSet().add("u:*q3r4wtdgf:test", "OK", 0);
        mrStringTemplate.opsForZSet().add("u:yhgfdw4r:test", "OK", 0);
        mrStringTemplate.opsForZSet().add("u:qwfsfb3qwedfg:test", "OK", 0);
        mrStringTemplate.opsForZSet().add("u:-we5ryhfgdy:test", "OK", 0);
        mrStringTemplate.opsForZSet().add("u:test1:test", "OK", 0);
        mrStringTemplate.opsForZSet().add("u:34rwefsgby54redf:test", "OK", 0);
		
		final JedisConnectionFactory cr = BeanLocator.getBean("core.jedisConnectionFactory.cache");
		RedisTemplate<String,String> crStringTemplate = new StringRedisTemplate(cr);
		crStringTemplate.opsForZSet().add("u:w87436t54es:test", "OK", 0);
		crStringTemplate.opsForZSet().add("u:1919qsrgs_asq:test", "OK", 0);
		crStringTemplate.opsForZSet().add("u:a4grdfg:test", "OK", 0);
		crStringTemplate.opsForZSet().add("u:ytgvcx:test", "OK", 0);
		crStringTemplate.opsForZSet().add("u:*qafe_asdaWd:test", "OK", 0);
		crStringTemplate.opsForZSet().add("u:6tyhj:test", "OK", 0);
    }

%>
<%
try {
	if(!Constants.getIsRedisFeedEnable()) {
		out.print("success");
		return;
	}

    writeTest();
	out.print("success");
} catch (Throwable e) {
    e.printStackTrace();
    out.print("fail");
}
%>
