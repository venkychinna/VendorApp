package com.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
	
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.model.User;
import com.app.service.IUserService;
import com.app.util.CodeUtil;
import com.app.util.CommonUtil;

@Controller
public class UserController {
	@Autowired
	private IUserService service;
	@Autowired
	private CodeUtil codeUtil;
	@Autowired
	private CommonUtil commonUtil;
	
	//1. show userReg page
	@RequestMapping("/regUser")
	public String showUserReg(){
		return "UserReg";
	}
	
	//2. save user
	@RequestMapping(value="/saveUser",method=RequestMethod.POST)
	public String saveUser(@ModelAttribute("user")User user,ModelMap map){
		//generate pwd
		String pwd=codeUtil.genPwd();
		//set pwd to user
		user.setUserPassword(pwd);
		//save user
		int userId=service.saveUser(user);
	
		//send email
		boolean flag=commonUtil.sendEmail(user.getUserEmail(), "Welcome to User...", 
				"Hello.., Your userName is :"+user.getUserEmail()+" (or) "+user.getUserContact()
				+" and password is: "+user.getUserPassword()
				+" you are registered with id:"+userId, null);
		
		//send success message to UI
		String msg="User Registered with Id:"+userId;
		if(flag)
			msg += ", Email also sent.";
		else
			msg += ", Email is not sent.";
		
		map.addAttribute("msg",msg);
		
		//goto Register Page
		return "UserReg";
	}
	
	//3. ShowLogin Page
	@RequestMapping("/showLogin")
	public String showLoginPage(){
		return "UserLogin";
	}
	
	//4. login check
	@RequestMapping(value="/loginUser",method=RequestMethod.POST)
	public String loginCheck(
	 @RequestParam("uid")String un,			
     @RequestParam("pwd")String pwd,ModelMap map,
     HttpServletRequest req){
		
		String pageName=null;
		User user=service.getUserByNameAndPwd(un, pwd);
		if(user==null){
			map.addAttribute("msg","UserName or password is invalid..");
			pageName="UserLogin";
		}else{
			//create http Session
			HttpSession ses=req.getSession();
			ses.setAttribute("un", user.getUserName());
			pageName="LocationReg";
		}
		return pageName;
	}
	
	
	//5. logout
	@RequestMapping("/logout")
	public String userLogout(HttpServletRequest req,ModelMap map){
		HttpSession ses=req.getSession(false);
		ses.setAttribute("un", null);
		ses.invalidate();
		map.addAttribute("msg","Logout successful");
		return "UserLogin";
	}
}
