package com.example.demo.controller;

import java.io.File;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.map.HashedMap;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class MailController {
	private final static String MAIL_FROM = "15850731033@139.com";
	private final static String MAIL_TO = "3162310162@qq.com";
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private VelocityEngine velocityEngine;

	/**
	 * SimpleMailMessage简单邮件的发送
	 */
	@RequestMapping(value="/send",method=RequestMethod.GET)
	public void send() {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(MAIL_FROM);
		message.setTo(MAIL_TO);
		message.setSubject("主题：2222222");
		message.setText("邮件正文：3333333");
		javaMailSender.send(message);
	}
	
	/**
	 * 发送带附件的邮件
	 * @throws MessagingException 
	 */
	@RequestMapping(value="/sendAttachment",method=RequestMethod.GET)
	public void sendAttachmentMail() throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
		helper.setFrom(MAIL_FROM);
		helper.setTo(MAIL_TO);
		helper.setSubject("主题：有附件");
		helper.setText("有附件的邮件");
		FileSystemResource file = new FileSystemResource(new File("weixin.jpg"));
		helper.addAttachment("附件-1.jpg", file);
		helper.addAttachment("附件-2.jpg", file);
		javaMailSender.send(mimeMessage);
	}
	
	/**
	 * 邮件嵌入静态资源
	 * @throws MessagingException 
	 */
	@RequestMapping(value="/sendInline",method=RequestMethod.GET)
	public void sendInlineResourceMail() throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom(MAIL_FROM);
		helper.setTo(MAIL_TO);
		helper.setSubject("主题：嵌入静态资源");
		helper.setText("<html><body><img src=\"cid:weixin\" ></body></html>", true);
		FileSystemResource file = new FileSystemResource(new File("weixin.jpg"));
		helper.addInline("weixin", file);
		javaMailSender.send(mimeMessage);
	}
	
	/**
	 * 创建模板页面template.vm，发送模板邮件
	 * @throws MessagingException 
	 */
	@RequestMapping(value="/sendTemplate",method=RequestMethod.GET)
	public void sendTemplateMail() throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom(MAIL_FROM);
		helper.setTo(MAIL_TO);
		helper.setSubject("主题：模板邮件");
		
		Map<String,Object> model = new HashedMap();
		model.put("username", "jianghuchaofan");
		String text = VelocityEngineUtils.
				mergeTemplateIntoString(velocityEngine, "template.vm", "UTF-8", model);
		helper.setText(text,true);
		javaMailSender.send(mimeMessage);
	}
}