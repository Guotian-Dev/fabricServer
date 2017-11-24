package com.wuyiqukuai.fabric.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hyperledger.fabric.sdkintegration.DataJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wuyiqukuai.fabric.domain.UserInfo;
import com.wuyiqukuai.fabric.util.DataHandleUtil;
import com.wuyiqukuai.fabric.util.VerifyCodeUtil;

@RequestMapping("/user")
@Controller
public class UserController {
	
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@RequestMapping("/toLogin")
	public String toLogin() {
		
		return "login";
		
	}
	
	@RequestMapping("/getRandom")
	@ResponseBody
	public String getRandomData(HttpServletRequest request) {
		
		
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		
		
		String uuidJson = DataJsonUtils.toJson(uuid);
		
		request.getSession().setAttribute("uuid", uuidJson);
		
		
		return uuidJson;
		
	}
	
	@RequestMapping("/logOut")
	public String logOut(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		//从session中移除用户
		session.invalidate();
		
		return "brow";
		
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(UserInfo user,
						@RequestParam("verifyCode")String verifyCode,
						HttpServletRequest request,
						RedirectAttributes redirectAttributes) throws Exception {
		logger.debug("前台表单传入的参数:username->" + user.getUserName() +"，password->" + user.getPassword() +"，verifyCode->" + verifyCode);
		/**
		 * ①获取相关参数(验证码，用户名，密码)
		 * ②验证验证码正确性，若不正确，直接返回
		 * ③验证用户名和密码
		 * ④将用户信息保存在session域中
		 */
		HttpSession session = request.getSession();
		//从session域中获取验证码文本
		String sessionVerifyCode = (String) session.getAttribute("verifyCode");
		
		logger.debug("verifyCode:" + sessionVerifyCode);
		
		//若验证码输入错误，直接返回
		if(!verifyCode.equals(sessionVerifyCode)) {
			
			//显示错误信息
			redirectAttributes.addFlashAttribute("errMsg", "验证码输入错误!");
			//使用重定向，防止重复登录
			return "redirect:/user/toLogin";
		}
		
		//查询数据库进行验证
		//
		UserInfo dataUser = new UserInfo();
		dataUser.setPassword(DataHandleUtil.getSHA256StrJava("123"));
		dataUser.setUserName(DataHandleUtil.getBase64("admin"));
		
		String uuid = request.getSession().getAttribute("uuid").toString().replaceAll("\"", "");
		
		String enPwd = dataUser.getPassword() + DataHandleUtil.getSHA256StrJava(uuid);
		
		logger.debug("data->userName:" + dataUser.getUserName());
		logger.debug("data->password:" + enPwd);
		
		//信息验证(密码保存的时候，对密码进行hash)
		//用户名和密码验证失败
		if(!(enPwd.equals(user.getPassword()) && dataUser.getUserName().equals(user.getUserName()))) {
			//显示错误信息
			redirectAttributes.addFlashAttribute("errMsg", "用户名或密码错误!");
			return "redirect:/user/toLogin";
		}
		//将用户添加到session域中
		session.setAttribute("user", user);
		return "redirect:/file/handle";
	}
	
	
	
	//验证码
	@RequestMapping("/getVerifyCode")
	public void getVerifyCode(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		//禁用缓存
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		
		/**
		 * 	获取验证码文本
			type 验证码类型,参见本类的静态属性
			length 验证码长度,要求大于0的整数
			excludeString 需排除的特殊字符（无需排除则为null）
		 */
		String verifyCode = VerifyCodeUtil.generateTextCode(VerifyCodeUtil.TYPE_NUM_ONLY, 4, null);
//		String verifyCode = VerifyCodeUtil.generateTextCode(VerifyCodeUtil.TYPE_ALL_MIXED, 4, null);
		
		//将验证码文本放到session域中
		session.setAttribute("verifyCode", verifyCode);
		/**
		 * 根据验证码文本生成验证码图片
		 * textCode 文本验证码
			width 图片宽度(注意此宽度若过小,容易造成验证码文本显示不全,如4个字符的文本可使用85到90的宽度)
			height 图片高度
			interLine 图片中干扰线的条数
			randomLocation 每个字符的高低位置是否随机
			backColor 图片颜色,若为null则表示采用随机颜色
			foreColor 字体颜色,若为null则表示采用随机颜色
			lineColor 干扰线颜色,若为null则表示采用随机颜色
		 */
		BufferedImage imageCode = VerifyCodeUtil.generateImageCode(verifyCode, 100, 35, 5, true, Color.WHITE, Color.BLACK, null);
		
		response.setContentType("image/jpeg");
		
		ImageIO.write(imageCode, "jpeg", response.getOutputStream());
	}
	
}
